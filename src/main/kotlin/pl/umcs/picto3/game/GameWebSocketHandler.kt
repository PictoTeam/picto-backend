package pl.umcs.picto3.game

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import org.springframework.web.util.UriTemplate
import pl.umcs.picto3.player.Player
import pl.umcs.picto3.player.PlayerRepository
import pl.umcs.picto3.session.Session
import pl.umcs.picto3.session.SessionCreatedEvent
import pl.umcs.picto3.session.SessionService
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Component
class GameWebSocketHandler(
    private val playerRepository: PlayerRepository,
    private val sessionService: SessionService,
    private val objectMapper: ObjectMapper
) : TextWebSocketHandler() {

    private val logger = KotlinLogging.logger {}
    private val gameWsSession = ConcurrentHashMap<String, ConcurrentHashMap<UUID, WebSocketSession>>()

    @EventListener
    fun handleSessionCreated(event: SessionCreatedEvent) {
        createNewSession(event.accessCode)
    }

    @EventListener
    fun handleGameStarted(event: GameStartedEvent) {
        logger.info { "🚀 Starting game session [${event.accessCode}]" }
        CoroutineScope(Dispatchers.IO).launch {
            sendToMultipleSessions(
                gameWsSession[event.accessCode]?.values?.toMutableSet() ?: mutableSetOf(),
                GameMessage.GAME_STARTED.type,
                mapOf(
                    "message" to "Game has started"
                )
            )
        }
        logger.info { "✅ Game [${event.accessCode}] has started" }
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

        val accessCode = gameSession.accessCode
        val newPlayer = playerRepository.save(Player(sessionAccessCode = gameSession.accessCode))
        logger.debug { "Player '${newPlayer.id}' joined session with accessCode: [$accessCode]" }
        sessionService.addPlayerToSession(newPlayer, accessCode)
        gameWsSession.get(gameSession.accessCode)?.put(newPlayer.id!!, wsSession)

        CoroutineScope(Dispatchers.IO).launch {
            sendToMultipleSessions(
                gameWsSession[gameSession.accessCode]?.values?.toMutableSet() ?: mutableSetOf(),
                GameMessage.PLAYER_JOINED.type,
                mapOf(
                    "newPlayerJoined" to "New player just joined session",
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
            val messageData = objectMapper.readValue(message.payload, Map::class.java) as Map<String, Any>
            val type = messageData["type"] as? String ?: throw Exception("Missing message type")

            when (type) {
                else -> {
                    logger.warn { "Unknown message type: $type" }
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

    override fun afterConnectionClosed(wsSession: WebSocketSession, status: CloseStatus) {
        val uri = wsSession.uri ?: throw Exception("Missing data in url")
        val template = UriTemplate("/ws/games/{gameAccessCode}/{role}")
        val variables = template.match(uri.path)
        val accessCode = variables["gameAccessCode"]
            ?: throw Exception("Missing gameAccessCode")
        val activeSession = sessionService.getSession(accessCode)
        val sessionConnections = gameWsSession[activeSession.accessCode]
        if (sessionConnections != null) {
            val playerEntry = sessionConnections.entries.find { it.value.id == wsSession.id }
            if (playerEntry != null) {
                sessionConnections.remove(playerEntry.key)
                logger.info { "⬅️ Player [${playerEntry.key}] disconnected from game [${accessCode}]" }
            } else {
                logger.warn { "Could not find player for disconnected session [${wsSession.id}] in game [${accessCode}]" }
            }
        } else {
            logger.warn { "No session connections found for game [${accessCode}]" }
        }
        logger.info { "⬅️ Player disconnected from game [${accessCode}]" }
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


    fun createNewSession(accessCode: String) {
        val newSessionWsConnections = ConcurrentHashMap<UUID, WebSocketSession>()
        gameWsSession.put(accessCode, newSessionWsConnections)
    }
}
