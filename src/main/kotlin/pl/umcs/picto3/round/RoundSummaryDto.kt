package pl.umcs.picto3.round

import pl.umcs.picto3.image.ImageDto
import java.util.*

data class RoundSummaryDto(
    val roundId: UUID,
    val wasSuccessful: Boolean,
    val pointsAwarded: Int,
    val topicImage: ImageDto,
    val speakerResponseTimeMs: Int?,
    val listenerResponseTimeMs: Int?
)