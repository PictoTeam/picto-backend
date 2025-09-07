package pl.umcs.picto3.round

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import pl.umcs.picto3.image.ImageRepository
import java.util.*

@Service
class RoundCompletionService(
    private val roundSummaryService: RoundSummaryService,
    private val imageRepository: ImageRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) {


    fun completeRound(
        round: Round,
        listenerSelectedImageId: UUID?,
        listenerResponseTime: Int?
    ): RoundResult {
        val game = round.game
        val gameConfig = game.gameConfig

        // determine if the round was successful
        val wasSuccessful = listenerSelectedImageId == round.topic.id

        // calculate points
        val pointsAwarded = if (wasSuccessful) {
            gameConfig.correctAnswerPoints.toInt()
        }
        else {
            gameConfig.wrongAnswerPoints.toInt()
        }

        // update the round
        val selectedImage = listenerSelectedImageId?.let { id ->
            imageRepository.findById(id).orElse(null)
        }

        val updatedRound = round.copy(
            selectedImage = selectedImage,
            listenerResponseTime = listenerResponseTime,
            endedAt = java.time.LocalDateTime.now()
        )

        // create round result
        val roundResult = roundSummaryService.createRoundResult(updatedRound, wasSuccessful, pointsAwarded)

        // publish round completion event
        applicationEventPublisher.publishEvent(
            RoundCompletedEvent(
                roundId = round.id!!,
                sessionAccessCode = game.sessionAccessCode,
                wasSuccessful = wasSuccessful,
                pointsAwarded = pointsAwarded
            )
        )

        return roundResult
    }
}