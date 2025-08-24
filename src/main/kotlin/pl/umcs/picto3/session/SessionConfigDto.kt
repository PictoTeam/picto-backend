package pl.umcs.picto3.session


import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import pl.umcs.picto3.image.ImageRepository
import pl.umcs.picto3.symbol.SymbolMapper
import pl.umcs.picto3.symbol.SymbolMatrixConfigDto

data class SessionConfigDto(
    @field:NotNull(message = "Symbols matrix is required") val symbols: SymbolMatrixConfigDto,
    @field:NotEmpty(message = "At least one image ID is required") val imagesId: Set<Long>,
    @field:Min(1) val speakerImageCount: Short? = null,
    @field:Min(1) val listenerImageCount: Short? = null,
    @field:Max(300000) val speakerAnswerTime: Int? = null,
    @field:Max(300000) val listenerAnswerTime: Int? = null,
    val correctAnswerPoints: Short,
    val wrongAnswerPoints: Short,
    @field:Min(1000) @field:Max(300000) val resultScreenTime: Short? = null
)

@Component
class SessionMapper(
    @Value("{admin.game.api-key}:dev-admin-key")
    private val adminGameApiKey: String,

    private val imageRepository: ImageRepository,
    private val symbolMapper: SymbolMapper
) {
    fun createSessionByConfig(dto: SessionConfigDto, accessCode: String): Session {
        return Session(
            symbols = symbolMapper.toSymbolMatrix(dto.symbols),
            images = imageRepository.findAllById(dto.imagesId).toSet(),
            speakerImageCount = dto.speakerImageCount,
            listenerImageCount = dto.listenerImageCount,
            speakerAnswerTime = dto.speakerAnswerTime,
            listenerAnswerTime = dto.listenerAnswerTime,
            correctAnswerPoints = dto.correctAnswerPoints,
            wrongAnswerPoints = dto.wrongAnswerPoints,
            resultScreenTime = dto.resultScreenTime,
            accessCode = accessCode,
            activeMembers = mutableSetOf(),
            adminApiKey = adminGameApiKey,
        )
    }
}


