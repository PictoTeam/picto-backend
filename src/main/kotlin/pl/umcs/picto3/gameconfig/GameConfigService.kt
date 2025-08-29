package pl.umcs.picto3.gameconfig

import org.springframework.stereotype.Service
import java.util.*

@Service
class GameConfigService(
    private val gameConfigMapper: GameConfigMapper,
    private val gameConfigRepository: GameConfigRepository
) {
    fun createGameConfig(gameConfigDto: GameConfigDto): GameConfigDto {
        val gameConfig = gameConfigMapper.toGameConfig(gameConfigDto)
        val savedGameConfig = gameConfigRepository.save(gameConfig)
        return gameConfigMapper.toDto(savedGameConfig)
    }

    fun getGameConfigs(): List<GameConfigDto> {
        return gameConfigRepository.findAll().map(gameConfigMapper::toDto)
    }

    fun getGameConfig(id: UUID): GameConfigDto {
        val gameConfig = gameConfigRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Game config with id $id not found") }

        return gameConfigMapper.toDto(gameConfig)
    }

    fun updateGameConfig(id: UUID, gameConfigDto: GameConfigDto): GameConfigDto {
        if (!gameConfigRepository.existsById(id)) {
            throw IllegalArgumentException("Game config with id $id not found")
        }

        val existingGameConfig = gameConfigRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Game config with id $id not found") }
        val updatedGameConfig = GameConfig(
            symbols = existingGameConfig.symbols,
            images = existingGameConfig.images,
            speakerImageCount = gameConfigDto.speakerImageCount,
            listenerImageCount = gameConfigDto.listenerImageCount,
            speakerAnswerTime = gameConfigDto.speakerAnswerTime,
            listenerAnswerTime = gameConfigDto.listenerAnswerTime,
            correctAnswerPoints = gameConfigDto.correctAnswerPoints,
            wrongAnswerPoints = gameConfigDto.wrongAnswerPoints,
            resultScreenTime = gameConfigDto.resultScreenTime,
            createdAt = existingGameConfig.createdAt
        )

        val savedGameConfig = gameConfigRepository.save(updatedGameConfig)
        return gameConfigMapper.toDto(savedGameConfig)
    }

    // TODO: set flag instead of deleting
    fun deleteGameConfig(id: UUID) {
        if (!gameConfigRepository.existsById(id)) {
            throw IllegalArgumentException("Game config with id $id not found")
        }

        gameConfigRepository.deleteById(id)
    }
}