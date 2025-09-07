package pl.umcs.picto3.round

import org.springframework.stereotype.Service
import pl.umcs.picto3.game.GameSummaryDto
import pl.umcs.picto3.image.ImageMapper
import java.util.*

@Service
class RoundSummaryService(
    private val roundResultRepository: RoundResultRepository,
    private val imageMapper: ImageMapper
) {

    fun createRoundResult(round: Round, wasSuccessful: Boolean, pointsAwarded: Int): RoundResult {
        val roundResult = RoundResult(
            round = round,
            wasSuccessful = wasSuccessful,
            pointsAwarded = pointsAwarded
        )
        return roundResultRepository.save(roundResult)
    }

    fun getRoundSummary(roundId: UUID): RoundSummaryDto? {
        val roundResult = roundResultRepository.findByRoundId(roundId) ?: return null
        val round = roundResult.round

        return RoundSummaryDto(
            roundId = round.id!!,
            wasSuccessful = roundResult.wasSuccessful,
            pointsAwarded = roundResult.pointsAwarded,
            topicImage = imageMapper.toNotMainDto(round.topic).copy(isTopic = true),
            speakerResponseTimeMs = round.speakerResponseTime,
            listenerResponseTimeMs = round.listenerResponseTime
        )
    }

    fun getGameSummary(sessionAccessCode: String): GameSummaryDto {
        val totalRounds = roundResultRepository.countTotalRoundsBySessionAccessCode(sessionAccessCode).toInt()
        val successfulRounds = roundResultRepository.countSuccessfulRoundsBySessionAccessCode(sessionAccessCode).toInt()
        val totalPoints = roundResultRepository.sumPointsBySessionAccessCode(sessionAccessCode)?.toInt() ?: 0

        val successRatio = if (totalRounds > 0) {
            successfulRounds.toDouble() / totalRounds.toDouble()
        }
        else 0.0

        return GameSummaryDto(
            totalRounds = totalRounds,
            successfulRounds = successfulRounds,
            successRatio = successRatio,
            totalPointsEarned = totalPoints
        )
    }
}