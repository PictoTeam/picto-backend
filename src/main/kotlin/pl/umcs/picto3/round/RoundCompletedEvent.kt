package pl.umcs.picto3.round

import java.util.*

data class RoundCompletedEvent(
    val roundId: UUID,
    val sessionAccessCode: String,
    val wasSuccessful: Boolean,
    val pointsAwarded: Int
)