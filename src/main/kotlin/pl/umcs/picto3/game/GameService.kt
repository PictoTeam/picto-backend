package pl.umcs.picto3.game

import org.springframework.stereotype.Service
import pl.umcs.picto3.game_config.GameConfig
import pl.umcs.picto3.game_config.GameConfigDto
import pl.umcs.picto3.game_config.GameConfigMapper
import pl.umcs.picto3.game_config.GameConfigRepository

@Service
class GameService(
    private val gameConfigMapper: GameConfigMapper,
    private val gameConfigRepository: GameConfigRepository
) {
    fun createGame(gameConfigDto: GameConfigDto): GameConfigDto {
        val gameConfig = gameConfigMapper.fromDto(gameConfigDto)
        val savedGameConfig = gameConfigRepository.save(gameConfig)

        return gameConfigMapper.toDto(savedGameConfig)
    }

    fun getGameConfigs(): List<GameConfigDto> {
        return gameConfigRepository.findAll().map(gameConfigMapper::toDto)
    }

    fun getGameConfig(id: Long): GameConfigDto {
        val gameConfig = gameConfigRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Game config with id $id not found") }

        return gameConfigMapper.toDto(gameConfig)
    }

    fun updateGameConfig(id: Long, gameConfigDto: GameConfigDto): GameConfigDto {
        if (!gameConfigRepository.existsById(id)) {
            throw IllegalArgumentException("Game config with id $id not found")
        }
        
        val existingGameConfig = gameConfigRepository.findById(id).get()
        val updatedGameConfig = GameConfig(
            id = id,
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
    fun deleteGameConfig(id: Long) {
        if (!gameConfigRepository.existsById(id)) {
            throw IllegalArgumentException("Game config with id $id not found")
        }

        gameConfigRepository.deleteById(id)
    }
}