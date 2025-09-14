package pl.umcs.picto3.round

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.umcs.picto3.game.GameRepository
import java.util.UUID

@Service
class RoundService(
    private val roundRepository: RoundRepository,
    private val gameRepository: GameRepository,
    private val roundMapper: RoundMapper
) {
    @Transactional
    fun saveNewRound(dto: InMemoryRound) {
        roundRepository.save(roundMapper.toEntity(dto))
    }

    @Transactional
    fun saveAllGameRounds(gameId: UUID) {
        val rounds = roundRepository.findAllByGameId(gameId)
        val game = gameRepository.findById(gameId)
            .orElseThrow { IllegalArgumentException("Game not found with id: $gameId") }

        game.rounds.addAll(rounds)
        gameRepository.save(game)
    }
}