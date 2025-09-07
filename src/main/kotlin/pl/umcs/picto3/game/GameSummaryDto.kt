package pl.umcs.picto3.game

data class GameSummaryDto(
    val totalRounds: Int,
    val successfulRounds: Int,
    val successRatio: Double,
    val totalPointsEarned: Int
)