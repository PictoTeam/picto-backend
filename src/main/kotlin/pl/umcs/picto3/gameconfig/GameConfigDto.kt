package pl.umcs.picto3.gameconfig

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import pl.umcs.picto3.symbol.SymbolMatrixConfigDto
import java.util.*

data class GameConfigDto(
    val id: UUID? = null,

    @field:NotNull(message = "Symbols matrix is required")
    val symbols: SymbolMatrixConfigDto,

    @field:NotEmpty(message = "At least one image ID is required")
    val imagesId: Set<UUID>,

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
