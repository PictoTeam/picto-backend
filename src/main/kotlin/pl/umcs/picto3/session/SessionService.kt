package pl.umcs.picto3.session

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import pl.umcs.picto3.game.Game
import pl.umcs.picto3.game.GameRepository
import pl.umcs.picto3.game.GameStartedEvent
import pl.umcs.picto3.gameconfig.GameConfigRepository
import pl.umcs.picto3.player.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Service
class SessionService(
    @Value("\${admin.game.api-key}")
    private var adminGameApiKey: String = "dev-admin-key",
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val gameConfigRepository: GameConfigRepository,
    private val gameRepository: GameRepository
) {

    fun createSession(gameConfigId: UUID): String {
        val gameConfig =
            gameConfigRepository.getReferenceById(gameConfigId) //lepiej zeby sie ktos nie pomyli tu z UUID bo nie wyskoczy blad xd
        val newCreatedSessionAccessCode = generateUniqueJoinCode()
        val newSession = Session(gameConfig, newCreatedSessionAccessCode, adminApiKey = adminGameApiKey)
        val newGame = Game(gameConfig = gameConfig, sessionAccessCode = newCreatedSessionAccessCode)
        gameRepository.save(newGame)
        applicationEventPublisher.publishEvent(SessionCreatedEvent(newCreatedSessionAccessCode, gameConfig))
        return newCreatedSessionAccessCode
    }

    fun sessionExists(sessionId: String): Boolean {
        return activeSessions.containsKey(sessionId)
    }

    fun startSession(accessCode: String): String {
        if (sessionExists(accessCode)) {
            applicationEventPublisher.publishEvent(GameStartedEvent(accessCode))
            return "Game session $accessCode started successfully"
        } else {
            throw IllegalArgumentException("Session with code $accessCode not found")
        }
    }


    private fun generateUniqueJoinCode(): String {
        var code: String
        do {
            code = (100000..999999).random().toString()
        } while (activeSessions.containsKey(code))

        return code
    }
}