package pl.umcs.picto3.session

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import pl.umcs.picto3.gameconfig.GameConfig
import pl.umcs.picto3.player.Player
import java.util.concurrent.ConcurrentHashMap

@Service
class SessionService(
    @Value("{admin.game.api-key}:dev-admin-key")
    private val adminGameApiKey: String,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    private val activeSessions = ConcurrentHashMap<String, Session>()

    fun createSession(gameConfig: GameConfig): String {
        val newCreatedSessionAccessCode = generateUniqueJoinCode()
        val newSession = Session(gameConfig, newCreatedSessionAccessCode, adminApiKey = adminGameApiKey)
        activeSessions[newCreatedSessionAccessCode] = newSession
        applicationEventPublisher.publishEvent(SessionCreatedEvent(newCreatedSessionAccessCode))
        return newCreatedSessionAccessCode
    }

    fun sessionExists(sessionId: String): Boolean {
        return activeSessions.containsKey(sessionId)
    }

    fun getSession(accessCode: String): Session {
        return activeSessions[accessCode] ?: throw IllegalArgumentException("Session with code $accessCode not found")
    }

    fun addPlayerToSession(newPlayer: Player, sessionAccessCode: String) {
        val session = activeSessions[sessionAccessCode]
            ?: throw IllegalArgumentException("Session with code $sessionAccessCode not found")
        session.activeMembers.add(newPlayer)
    }

    fun findSessionAccessCodeByAdminWsSession(sessionId: String): String {
        return activeSessions.values.find { session ->
            session.adminsWsSessions.contains(sessionId)
        }?.accessCode ?: throw TODO("lepsza obsluga bledu tutaj raczej rzadzko bedzie wiec luz")
    }

    private fun generateUniqueJoinCode(): String {
        var code: String
        do {
            code = (100000..999999).random().toString()
        } while (activeSessions.containsKey(code))

        return code
    }
}