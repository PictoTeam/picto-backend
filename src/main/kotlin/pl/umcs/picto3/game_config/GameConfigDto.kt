package pl.umcs.picto3.game_config

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.springframework.stereotype.Component
import pl.umcs.picto3.image.ImageRepository
import pl.umcs.picto3.symbol.SymbolMapper
import pl.umcs.picto3.symbol.SymbolMatrixConfigDto
import pl.umcs.picto3.symbol.SymbolService

data class GameConfigDto(
    @field:NotNull(message = "Symbols matrix is required")
    val symbols: SymbolMatrixConfigDto,

    @field:NotEmpty(message = "At least one image ID is required")
    val imagesId: Set<Long>,

    @field:Min(1)
    @field:Max(value = 100, message = "Speaker image count cannot exceed 100")
    val speakerImageCount: Short = 4,

    @field:Min(1)
    @field:Max(value = 100, message = "Listener image count cannot exceed 100")
    val listenerImageCount: Short = 4,

    @field:Min(0)
    @field:Max(300000)
    val speakerAnswerTime: Int = -1,

    @field:Min(0)
    @field:Max(300000)
    val listenerAnswerTime: Int = -1,

    val correctAnswerPoints: Short = 1,
    val wrongAnswerPoints: Short = -1,

    @field:Min(0)
    @field:Max(300000)
    val resultScreenTime: Int = 3000
)

@Component
class GameConfigMapper(
    private val imageRepository: ImageRepository,
    private val symbolMapper: SymbolMapper,
    private val symbolService: SymbolService
) {
    fun fromDto(dto: GameConfigDto, id: Long? = null): GameConfig {
        return GameConfig(
            id = id,
            symbols = symbolService.toSymbolMatrix(dto.symbols),
            images = imageRepository.findAllById(dto.imagesId).toSet(),
            speakerImageCount = dto.speakerImageCount,
            listenerImageCount = dto.listenerImageCount,
            speakerAnswerTime = dto.speakerAnswerTime,
            listenerAnswerTime = dto.listenerAnswerTime,
            correctAnswerPoints = dto.correctAnswerPoints,
            wrongAnswerPoints = dto.wrongAnswerPoints,
            resultScreenTime = dto.resultScreenTime,
            createdAt = null,
        )
    }

    fun toDto(gameConfig: GameConfig): GameConfigDto {
        return GameConfigDto(
            symbols = symbolMapper.toSymbolMatrixConfigDto(gameConfig.symbols),
            imagesId = gameConfig.images.map { it.id!! }.toSet(),
            speakerImageCount = gameConfig.speakerImageCount,
            listenerImageCount = gameConfig.listenerImageCount,
            speakerAnswerTime = gameConfig.speakerAnswerTime,
            listenerAnswerTime = gameConfig.listenerAnswerTime,
            correctAnswerPoints = gameConfig.correctAnswerPoints,
            wrongAnswerPoints = gameConfig.wrongAnswerPoints,
            resultScreenTime = gameConfig.resultScreenTime
        )
    }
}
