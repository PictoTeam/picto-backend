package pl.umcs.picto3.game

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
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
import pl.umcs.picto3.game.communication.setRoundId
import pl.umcs.picto3.matchmaker.MatchMaker
import pl.umcs.picto3.player.Player
import pl.umcs.picto3.player.PlayerMessageType
import pl.umcs.picto3.player.PlayerRepository
import pl.umcs.picto3.round.InMemoryRound
import pl.umcs.picto3.round.RoundService
import pl.umcs.picto3.session.SessionClosedEvent
import pl.umcs.picto3.session.SessionCreatedEvent
import pl.umcs.picto3.symbol.SymbolMapper
import pl.umcs.picto3.symbol.SymbolMatrixDto
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Component
class GameWebSocketHandler(
    private val playerRepository: PlayerRepository,
    private val objectMapper: ObjectMapper,
    private val matchMaker: MatchMaker,
    private val symbolMapper: SymbolMapper,
    private val roundService: RoundService,
) : TextWebSocketHandler() {

    private val logger = KotlinLogging.logger {}
    private val sessions: MutableMap<String, InMemoryGame> =
        ConcurrentHashMap() //TODO nieironicznie tutaj redisa powinnismy uzyc

    @EventListener
    fun handleSessionCreated(event: SessionCreatedEvent) {
        sessions.put(event.accessCode, InMemoryGame(gameConfig = event.gameConfig))
    }

    @EventListener
    fun handleGameStarted(event: GameStartedEvent) {
        matchMaker.isGameGoing = true
        logger.info { "🚀 Starting game [${event.accessCode}]" }
        CoroutineScope(Dispatchers.IO).launch {
            val players = sessions[event.accessCode]?.presentPlayers?.values
            val webSocketSessions = players?.map { it.wsSession }?.toMutableSet() ?: mutableSetOf()
            if (webSocketSessions.isNotEmpty()) {
                sendToMultipleSessions(
                    webSocketSessions,
                    GameMessage.GAME_STARTED.type,
                    mapOf(
                        "message" to "Game has started"
                    )
                )
            }
        }
        logger.info { "✅ Game [${event.accessCode}] has started" }
    }

    @EventListener
    fun handleGameFinished(event: GameFinishedEvent) {
        matchMaker.isGameGoing = false
        logger.info { "🛑 Finishing game [${event.accessCode}]" }

        CoroutineScope(Dispatchers.IO).launch {
            val game = sessions[event.accessCode] ?: return@launch
            val gameConfig = game.gameConfig

            val timeToFinishRounds = (gameConfig.speakerAnswerTime + gameConfig.listenerAnswerTime) * 1000L
            delay(timeToFinishRounds)

            val playerSessions = game.presentPlayers.values.mapNotNull { it.wsSession }.toSet()

            sendToMultipleSessions(
                playerSessions,
                GameMessage.GAME_FINISHED.type,
                mapOf(
                    "message" to "Game has finished"
                )
            )
            logger.debug { "Players notified of game finish after ${timeToFinishRounds / 1000}s delay" }
            saveAllGameRounds(event.gameId)
        }
    }

    @EventListener
    fun handleSessionClosed(event: SessionClosedEvent) {
        sessions.remove(event.accessCode)
    }

    @PostConstruct
    fun initMatchMaker() {
        matchMaker.onPairReady = { p1, p2 ->
            val game = sessions[p2.sessionAccessCode]
            val randomImages =
                game?.gameConfig?.images?.shuffled()?.take(game.gameConfig.speakerImageCount.toInt())
                    ?.mapNotNull { it.id }?.toSet() ?: emptySet()
            val topicImageId = randomImages.randomOrNull() ?: UUID.randomUUID()
            val newRoundId = UUID.randomUUID()
            game?.gameRounds?.put(
                newRoundId,
                InMemoryRound(
                    p2.sessionAccessCode,
                    p1.id!!,
                    p2.id!!,
                    randomImages,
                    topicImageId,
                    startedAt = LocalDateTime.now(),
                )
            )
            //setup p1 info
            p1.lastOpponentId = p2.id
            p2.wsSession?.setRoundId(newRoundId)
            //setup p2 info
            p2.lastOpponentId = p1.id
            p2.wsSession?.setRoundId(newRoundId)
            sessions[p2.sessionAccessCode]?.presentPlayers?.put(p1.id!!, p1)
            sessions[p2.sessionAccessCode]?.presentPlayers?.put(p2.id!!, p2)
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
        if (sessions.containsKey(accessCode)) {
            when (role) {
                "player" -> {
                    autoJoinAsPlayer(wsSession, accessCode)
                } //TODO dorobienie wywyolania ze gracz wraca go gry musi podac swoj id w pathie
//                TLDR: szukasz po id gracza z session players i podmieniasz adres websocketa + dodaje do puli

                else -> throw Exception("Unknown role: $role.")
            }
        } else {
            throw Exception("Session id does not match any active session")
        }
    }

    private fun autoJoinAsPlayer(wsSession: WebSocketSession, gameSessionAccessCode: String) {
        logger.info { "➡️ Player tries to join to game [${gameSessionAccessCode}]" }

        val newPlayer = playerRepository.save(Player(sessionAccessCode = gameSessionAccessCode, wsSession = wsSession))
        logger.debug { "Player '${newPlayer.id}' joined session with accessCode: [$gameSessionAccessCode]" }
        wsSession.setPlayerId(newPlayer.id!!)
        matchMaker.addPlayerToQueue(newPlayer)
        CoroutineScope(Dispatchers.IO).launch {
            val players = sessions[gameSessionAccessCode]?.presentPlayers?.values
            val webSocketSessions = players?.map { it.wsSession }?.toMutableSet() ?: mutableSetOf()
            if (webSocketSessions.isNotEmpty()) {
                sendToMultipleSessions(
                    webSocketSessions,
                    GameMessage.PLAYER_JOINED.type,
                    mapOf(
                        "message" to "Game has started"
                    )
                )
                sendToSession(
                    wsSession,
                    GameMessage.PLAYER_WELCOME.type,
                    mapOf(
                        "playerWelcomeMessage" to "Welcome to picto game!",
                    )
                )
            }
        }
    }

    override fun handleTextMessage(wsSession: WebSocketSession, message: TextMessage) {
        try {
            val messageWrapper = objectMapper.readValue(message.payload, MessageWrapper::class.java)
            when (messageWrapper.type) {
                PlayerMessageType.LISTENER_IMAGE_PICKED.type -> {
                    val data = objectMapper.convertValue(messageWrapper.data, ListenerImagePickedData::class.java)
                    handleListenerPick(
                        wsSession.getAccessCode()!!, wsSession.getPlayerId(), data
                    )
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
                        "errorMessage" to "Unexpected error occurred!",
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

        val game = sessions[accessCode] ?: run {
            logger.warn { "Game not found for access code: $accessCode" }
            return
        }

        val round = game.gameRounds[roundId] ?: run {
            logger.warn { "Round not found for ID: $roundId" }
            return
        }

        val pickedSymbolsCount = data.symbolsIds.size
        val requiredCount = game.gameConfig.speakerImageCount.toInt()

        when {
            pickedSymbolsCount < requiredCount -> {
                TODO("Exception to inform user to pick more symbols")
            }

            pickedSymbolsCount > requiredCount -> {
                TODO("Exception to inform user to pick less symbols")
            }

            else -> {
                val symbolUUIDs = data.symbolsIds
                val updatedRound = round.copy(
                    speakerPickedSymbolsIds = symbolUUIDs,
                    speakerResponseTime = data.speakerResponseTime
                )
                game.gameRounds[roundId] = updatedRound
            }
        }
    }

    private fun startRoundForSpeaker(
        wsSession: WebSocketSession?,
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

    private fun startRoundForListener(wsSession: WebSocketSession?) {
        wsSession?.let { session ->
            CoroutineScope(Dispatchers.IO).launch {
                sendToSession(
                    session,
                    GameMessage.LISTENER_STAGE_1.type,
                    mapOf(
                        "info" to "Speaker is picking question",
                    )
                )
            }
        }
    }

    private fun handleListenerPick(accessCode: String, roundId: UUID?, data: ListenerImagePickedData) {
        if (roundId == null) {
            logger.warn { "Round ID is null for access code: $accessCode" }
            return
        }

        val game = sessions[accessCode] ?: run {
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
        endRound(accessCode, roundId)
    }

    override fun afterConnectionClosed(wsSession: WebSocketSession, status: CloseStatus) {
        sessions[wsSession.getAccessCode()]?.presentPlayers[wsSession.getPlayerId()]?.let {
            it.wsSession = null
            matchMaker.removePlayerFromQueue(it)
        }
        logger.info { "⬅️ Player disconnected from game [${wsSession.getAccessCode()}]" }
    }

    private suspend fun sendToMultipleSessions(
        sessions: Set<WebSocketSession?>,
        type: String,
        data: Map<String, Any>
    ) {
        val message = createJsonMessage(type, data)

        coroutineScope {
            sessions.filterNotNull().map { session ->
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

    private suspend fun sendToSession(session: WebSocketSession?, type: String, data: Map<String, Any>) {
        session?.let {
            withContext(Dispatchers.IO) {
                if (it.isOpen) {
                    try {
                        val message = createJsonMessage(type, data)
                        it.sendMessage(TextMessage(message))
                        logger.debug { "📤 Sent to ${it.id}: $type" }
                    } catch (e: Exception) {
                        logger.error(e) { "❌ Error sending msg to ${it.id}" }
                    }
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

    private fun endRound(accessCode: String, roundId: UUID) {
        val game = sessions[accessCode] ?: return
        val round = game.gameRounds[roundId] ?: return
        sendResultToPlayers(game, round)
        saveRoundResult(round)
        val endedRoundPlayers = setOf(
            game.presentPlayers[round.speakerId],
            game.presentPlayers[round.listenerId]
        )
        movePlayersToMatchMaker(endedRoundPlayers)

    }

    private fun sendResultToPlayers(game: InMemoryGame, round: InMemoryRound) {

        val wsSessionsToSendResult = setOf(
            game.presentPlayers[round.listenerId]?.wsSession,
            game.presentPlayers[round.speakerId]?.wsSession
        )
        val roundResult = if (round.topicImageId == round.listenerPickedImageId) {
            mapOf(
                "roundResult" to "Correct",
                "receivedPoints" to "10" //TODO podmianka na wartosci z game configa
            )
        } else {
            mapOf(
                "roundResult" to "Wrong",
                "receivedPoints" to "10" //TODO podmianka na wartosci z game configa
            )
        }
        CoroutineScope(Dispatchers.IO).launch {
            sendToMultipleSessions(
                wsSessionsToSendResult,
                GameMessage.ROUND_RESULT.type,
                roundResult
            )
        }
    }

    private fun movePlayersToMatchMaker(
        playersToMoveBack: Set<Player?>,
        delaySeconds: Long = 5
    ) { //TODO podmianka na wartosc z configa
        CoroutineScope(Dispatchers.IO).launch {
            delay(delaySeconds * 1000)
            playersToMoveBack.filterNotNull().forEach { player ->
                matchMaker.addPlayerToQueue(player)
            }
            logger.debug { "Players moved back to matchmaker after ${delaySeconds}s delay" }
        }
    }

    private fun saveRoundResult(round: InMemoryRound) {
        roundService.saveNewRound(round)
    }

    private fun saveAllGameRounds(accessCode: UUID) {
        roundService.saveAllGameRounds(accessCode)
    }
}
