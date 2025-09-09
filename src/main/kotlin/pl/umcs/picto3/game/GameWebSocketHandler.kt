package pl.umcs.picto3.game

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import org.springframework.web.util.UriTemplate
import pl.umcs.picto3.game.communication.ListenerImagePickedData
import pl.umcs.picto3.game.communication.MessageWrapper
import pl.umcs.picto3.game.communication.SpeakerSymbolsPickedData
import pl.umcs.picto3.game.communication.getAccessCode
import pl.umcs.picto3.game.communication.getPlayerId
import pl.umcs.picto3.game.communication.getRoundId
import pl.umcs.picto3.game.communication.setAccessCode
import pl.umcs.picto3.game.communication.setPlayerId
import pl.umcs.picto3.matchmaker.MatchMaker
import pl.umcs.picto3.player.Player
import pl.umcs.picto3.player.PlayerMessageType
import pl.umcs.picto3.player.PlayerRepository
import pl.umcs.picto3.round.InMemoryRound
import pl.umcs.picto3.session.Session
import pl.umcs.picto3.session.SessionCreatedEvent
import pl.umcs.picto3.session.SessionService
import pl.umcs.picto3.symbol.SymbolMapper
import pl.umcs.picto3.symbol.SymbolMatrixDto
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Component
class GameWebSocketHandler(
    private val playerRepository: PlayerRepository,
    private val sessionService: SessionService,
    private val objectMapper: ObjectMapper,
    private val matchMaker: MatchMaker,
    private val symbolMapper: SymbolMapper
) : TextWebSocketHandler() {

    private val logger = KotlinLogging.logger {}
    private val games: MutableMap<String, InMemoryGameDto> = ConcurrentHashMap()

    @EventListener
    fun handleSessionCreated(event: SessionCreatedEvent) {
        games.put(event.accessCode, InMemoryGameDto(gameConfig = event.gameConfig))
    }

    @EventListener
    fun handleGameStarted(event: GameStartedEvent) {
        matchMaker.isGameGoing = true
        logger.info { "🚀 Starting game session [${event.accessCode}]" }
        CoroutineScope(Dispatchers.IO).launch {
            games[event.accessCode]?.webSocketSessions?.let {
                sendToMultipleSessions(
                    it,
                    GameMessage.GAME_STARTED.type,
                    mapOf(
                        "message" to "Game has started"
                    )
                )
            }
        }
        logger.info { "✅ Game [${event.accessCode}] has started" }
    }

    @PostConstruct
    fun initMatchMaker() {
        matchMaker.onPairReady = { p1, p2 ->
            val game = games[p2.wsSession.getAccessCode()!!]
            val randomImages =
                game?.gameConfig?.images?.shuffled()?.take(game.gameConfig.speakerImageCount.toInt())
                    ?.mapNotNull { it.id }?.toSet() ?: emptySet()
            val topicImageId = randomImages.randomOrNull() ?: UUID.randomUUID()
            val newRoundId = UUID.randomUUID()
            game?.gameRounds?.put(
                newRoundId,
                InMemoryRound(
                    p2.wsSession.getAccessCode()!!,
                    p1.id!!,
                    p2.id!!,
                    randomImages,
                    topicImageId
                )
            )
            startRoundForSpeaker(
                p2.wsSession,
                randomImages,
                topicImageId,
                symbolMapper.toSymbolMatrixDto(game?.gameConfig?.symbols!!),
                game.gameConfig.speakerAnswerTime
            )
            startRoundForListener(p1.wsSession)
        }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        logger.info { "New connection: [${session.id}]" }
        try {
            processConnectionUrl(session)
        } catch (e: Exception) {
            logger.error(e) { "Error while connecting : ${e.message}" }
            session.close(CloseStatus.BAD_DATA.withReason("Error while establishing connection"))
        }
    }

    private fun processConnectionUrl(wsSession: WebSocketSession) {
        val uri = wsSession.uri ?: throw Exception("Missing data in url")
        val template = UriTemplate("/ws/games/{gameAccessCode}/{role}")

        val variables = template.match(uri.path)
        val accessCode = variables["gameAccessCode"]
            ?: throw Exception("Missing gameAccessCode")
        val role = variables["role"]
            ?: throw Exception("Missing role")
        wsSession.setAccessCode(accessCode)
        if (sessionService.sessionExists(accessCode)) {
            val activeSession = sessionService.getSession(accessCode)
            when (role) {
                "player" -> {
                    autoJoinAsPlayer(wsSession, activeSession)
                }

                "admin" -> {
                    val apiKey = uri.query?.split("&")
                        ?.find { it.startsWith("apikey=") }
                        ?.substringAfter("=")
                        ?: throw Exception("Missing apikey parameter for admin role")

                    autoJoinAsAdmin(wsSession, activeSession, apiKey)
                }

                else -> throw Exception("Unknown role: $role.")
            }
        } else {
            throw Exception("Session id does not match any active session")
        }
    }

    private fun autoJoinAsPlayer(wsSession: WebSocketSession, gameSession: Session) {
        logger.info { "➡️ Player tries to join to game [${gameSession.accessCode}]" }

        val newPlayer = playerRepository.save(Player(sessionAccessCode = gameSession.accessCode, wsSession = wsSession))
        logger.debug { "Player '${newPlayer.id}' joined session with accessCode: [$gameSession.accessCode]" }
        sessionService.addPlayerToSession(newPlayer, gameSession.accessCode)
        wsSession.setPlayerId(newPlayer.id!!)
        matchMaker.addPlayerToQueue(newPlayer)
        CoroutineScope(Dispatchers.IO).launch {
            games[gameSession.accessCode]?.webSocketSessions?.let {
                sendToMultipleSessions(
                    it,
                    GameMessage.PLAYER_JOINED.type,
                    mapOf(
                        "newPlayerJoined" to "New player just joined session",
                    )
                )
            }
            sendToSession(
                wsSession,
                GameMessage.PLAYER_WELCOME.type,
                mapOf(
                    "playerWelcomeMessage" to "Welcome to picto game!",
                )
            )
        }
    }

    private fun autoJoinAsAdmin(wsSession: WebSocketSession, gameSession: Session, adminApiKey: String) {
        logger.info { "👑 Admin tries to join to game [${gameSession.accessCode}]" }

        if (gameSession.adminApiKey != adminApiKey) {
            throw Exception("Wrong admin token!")
        }
        gameSession.adminsWsSessions.add(wsSession.id)

        logger.info { "✅ Admin connected to game [${gameSession.accessCode}]" }

        CoroutineScope(Dispatchers.IO).launch {
            sendToSession(
                wsSession,
                GameMessage.ADMIN_CONNECTED.type,
                mapOf(
                    "adminWelcomeMessage" to "Welcome admin to picto game!",
                )
            )
        }
    }

    override fun handleTextMessage(wsSession: WebSocketSession, message: TextMessage) {
        try {
            val messageWrapper = objectMapper.readValue(message.payload, MessageWrapper::class.java)
            when (messageWrapper.type) {
                PlayerMessageType.LISTENER_IMAGE_PICKED.type -> {
                    val data = objectMapper.convertValue(messageWrapper.data, ListenerImagePickedData::class.java)
                    handleListenerPick(wsSession.getAccessCode()!!, wsSession.getRoundId()!!, data)

                }

                PlayerMessageType.SPEAKER_SYMBOLS_PICKED.type -> {
                    val data = objectMapper.convertValue(messageWrapper.data, SpeakerSymbolsPickedData::class.java)
                    handleSpeakerPicks(wsSession.getAccessCode()!!, wsSession.getRoundId(), data)
                    CoroutineScope(Dispatchers.IO).launch {
                        sendToSession(
                            wsSession,
                            GameMessage.SPEAKER_STAGE_2.type,
                            mapOf(
                                "info" to "Listener is picking answer",
                            )
                        )
                    }
                }

                else -> {
                    logger.warn { "Unknown message type: ${messageWrapper.type}" }
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Error processing message: ${e.message}" }
            CoroutineScope(Dispatchers.IO).launch {
                sendToSession(
                    wsSession,
                    GameMessage.ERROR.type,
                    mapOf(
                        "error message" to "Unexpected error occurred!",
                    )
                )
            }
        }
    }

    private fun handleSpeakerPicks(accessCode: String, roundId: UUID?, data: SpeakerSymbolsPickedData) {
        if (roundId == null) {
            logger.warn { "Round ID is null for access code: $accessCode" }
            return
        }

        val game = games[accessCode] ?: run {
            logger.warn { "Game not found for access code: $accessCode" }
            return
        }

        val round = game.gameRounds[roundId] ?: run {
            logger.warn { "Round not found for ID: $roundId" }
            return
        }

        val pickedSymbolsCount = data.symbols.size
        val requiredCount = game.gameConfig.speakerImageCount.toInt()

        when {
            pickedSymbolsCount < requiredCount -> {
                TODO("Exception to inform user to pick more symbols")
            }

            pickedSymbolsCount > requiredCount -> {
                TODO("Exception to inform user to pick less symbols")
            }

            else -> {
                val symbolUUIDs = data.symbols.map { UUID.fromString(it) }
                val updatedRound = round.copy(
                    speakerPickedSymbolsIds = symbolUUIDs,
                    speakerResponseTime = data.speakerResponseTime
                )
                game.gameRounds[roundId] = updatedRound
            }
        }
    }

    private fun startRoundForSpeaker(
        wsSession: WebSocketSession,
        randomImages: Set<UUID>,
        topicImageId: UUID,
        symbolMatrix: SymbolMatrixDto,
        speakerAnswerTime: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            sendToSession(
                wsSession,
                GameMessage.SPEAKER_STAGE_1.type,
                mapOf(
                    "images" to randomImages,
                    "topic" to topicImageId,
                    "symbolMatrix" to symbolMatrix,
                    "answerTime" to speakerAnswerTime
                )
            )
        }
    }

    private fun startRoundForListener(wsSession: WebSocketSession) {
        CoroutineScope(Dispatchers.IO).launch {
            sendToSession(
                wsSession,
                GameMessage.LISTENER_STAGE_1.type,
                mapOf(
                    "info" to "Speaker is picking question",
                )
            )
        }
    }

    private fun handleListenerPick(accessCode: String, roundId: UUID?, data: ListenerImagePickedData) {
        if (roundId == null) {
            logger.warn { "Round ID is null for access code: $accessCode" }
            return
        }

        val game = games[accessCode] ?: run {
            logger.warn { "Game not found for access code: $accessCode" }
            return
        }

        val round = game.gameRounds[roundId] ?: run {
            logger.warn { "Round not found for ID: $roundId" }
            return
        }
        val updatedRound = round.copy(
            listenerPickedImageId = data.imageId,
            listenerResponseTime = data.listenerResponseTime,
        )
        game.gameRounds[roundId] = updatedRound
        // jeszcze zaznaczenie ze oni grali teraz razem i dodanie ich do puli
//        1.tutaj funkcja ktora wysle z opoznieniem graczy znowu do puli
//        2.pozniej nastepuje zapis rundy do bazy w osobnym threadzie
//        3.na sam koniec gracze dostaja odpowiedz czy ich odpowiedz sie zgadzala czy nie

    }

    override fun afterConnectionClosed(wsSession: WebSocketSession, status: CloseStatus) {
        games[wsSession.getAccessCode()]?.webSocketSessions?.removeIf { it.id == wsSession.id }
        val playerToRemove = playerRepository.getReferenceById(wsSession.getPlayerId()!!)
        matchMaker.removePlayerFromQueue(playerToRemove)
        logger.info { "⬅️ Player disconnected from game [${wsSession.getAccessCode()}]" }
    }

    private suspend fun sendToMultipleSessions(
        sessions: MutableSet<WebSocketSession>,
        type: String,
        data: Map<String, Any>
    ) {
        val message = createJsonMessage(type, data)

        coroutineScope {
            sessions.map { session ->
                async(Dispatchers.IO) {
                    if (session.isOpen) {
                        try {
                            session.sendMessage(TextMessage(message))
                            logger.debug { "📤 Sent to ${session.id}: $type" }
                        } catch (e: Exception) {
                            logger.error(e) { "❌ Error sending msg to ${session.id}" }
                        }
                    }
                }
            }.awaitAll()
        }

        logger.debug { "📤 Broadcast to ${sessions.size} $type" }
    }

    private suspend fun sendToSession(session: WebSocketSession, type: String, data: Map<String, Any>) {
        withContext(Dispatchers.IO) {
            if (session.isOpen) {
                try {
                    val message = createJsonMessage(type, data)
                    session.sendMessage(TextMessage(message))
                    logger.debug { "📤 Sent to ${session.id}: $type" }
                } catch (e: Exception) {
                    logger.error(e) { "❌ Error sending msg to ${session.id}" }
                }
            }
        }
    }

    private fun createJsonMessage(type: String, data: Map<String, Any>): String {
        return objectMapper.writeValueAsString(
            mapOf(
                "type" to type,
                "data" to data,
                "timestamp" to System.currentTimeMillis()
            )
        )
    }
}
