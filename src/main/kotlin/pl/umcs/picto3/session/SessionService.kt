package pl.umcs.picto3.session


import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import pl.umcs.picto3.game.Game
import pl.umcs.picto3.game.GameFinishedEvent
import pl.umcs.picto3.game.GameRepository
import pl.umcs.picto3.game.GameStartedEvent
import pl.umcs.picto3.gameconfig.GameConfigRepository
import pl.umcs.picto3.symbol.SymbolMatrix
import java.util.UUID

@Service
class SessionService(
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val gameConfigRepository: GameConfigRepository,
    private val gameRepository: GameRepository,
    private val sessionRepository: SessionRepository
) {

    fun createSession(gameConfigId: UUID): String {
        val gameConfig = gameConfigRepository.findById(gameConfigId).orElseThrow {
            IllegalArgumentException("Game config with id: $gameConfigId not found")
        }
        val newCreatedSessionAccessCode = generateUniqueJoinCode()
        sessionRepository.save(Session(newCreatedSessionAccessCode, gameConfig = gameConfig))
        applicationEventPublisher.publishEvent(SessionCreatedEvent(newCreatedSessionAccessCode, gameConfig))
        return newCreatedSessionAccessCode
    }

    fun getSymbolsForSession(accessCode: String): SymbolMatrix {
        return sessionRepository.findById(accessCode).orElseThrow {
            IllegalArgumentException("Session with code: $accessCode not found")
        }.gameConfig.symbols
    }

    fun startGameInSessionWithGivenAccessCode(accessCode: String) {
        val sessionToStartGame = sessionRepository.findById(accessCode).orElseThrow {
            IllegalArgumentException("Session with code: $accessCode not found")
        }
        val newGame = Game(gameConfig = sessionToStartGame.gameConfig, sessionAccessCode = accessCode)
        gameRepository.save(newGame)
        sessionToStartGame.gameId = newGame.id
        sessionRepository.save(sessionToStartGame)
        applicationEventPublisher.publishEvent(GameStartedEvent(accessCode, newGame.id!!))
    }

    fun finishGameInSessionWithGivenAccessCode(accessCode: String) {
        if (gameRepository.existsBySessionAccessCode(accessCode)) {
            val currentSession = sessionRepository.findById(accessCode).orElseThrow {
                IllegalArgumentException("Session with code: $accessCode not found")
            }
            applicationEventPublisher.publishEvent(GameFinishedEvent(accessCode, currentSession.gameId!!))
        } else {
            throw IllegalArgumentException("Game with accessCode $accessCode not found")
        }
    }

    fun closeSession(accessCode: String) {
        if (sessionRepository.findById(accessCode).isPresent) {
            applicationEventPublisher.publishEvent(SessionClosedEvent(accessCode))
            sessionRepository.deleteById(accessCode)
        } else {
            throw IllegalArgumentException("Session with code $accessCode not found")
        }
    }

    private fun generateUniqueJoinCode(): String {
        var code: String
        do {
            code = (100000..999999).random().toString()
        } while (gameRepository.existsBySessionAccessCode(code))
        return code
    }
}