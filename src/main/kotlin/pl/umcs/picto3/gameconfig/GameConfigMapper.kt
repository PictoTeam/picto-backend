package pl.umcs.picto3.gameconfig

import org.springframework.stereotype.Component
import pl.umcs.picto3.image.ImageRepository
import pl.umcs.picto3.symbol.SymbolMapper
import pl.umcs.picto3.symbol.SymbolService

@Component
class GameConfigMapper(
    private val imageRepository: ImageRepository,
    private val symbolMapper: SymbolMapper,
    private val symbolService: SymbolService
) {
    fun toGameConfig(dto: GameConfigDto): GameConfig {
        return GameConfig(
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