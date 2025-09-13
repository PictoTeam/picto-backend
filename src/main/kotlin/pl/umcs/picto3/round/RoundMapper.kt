package pl.umcs.picto3.round

import org.springframework.stereotype.Component
import pl.umcs.picto3.game.GameRepository
import pl.umcs.picto3.image.ImageRepository
import pl.umcs.picto3.player.PlayerRepository
import pl.umcs.picto3.session.SessionRepository
import pl.umcs.picto3.symbol.SymbolRepository
import java.time.LocalDateTime

@Component
class RoundMapper(
    private val sessionRepository: SessionRepository,
    private val playerRepository: PlayerRepository,
    private val imageRepository: ImageRepository,
    private val symbolRepository: SymbolRepository,
    private val roundRepository: RoundRepository,
    private val gameRepository: GameRepository,
) {
    fun toEntity(dto: InMemoryRound): Round {
        //TODO moze da sie jakos ladniej tego mappera zrobic ?
        val session = sessionRepository.findById(dto.gameAccessCode)
            .orElseThrow { IllegalArgumentException("Session not found: ${dto.gameAccessCode}") }

        val listener = playerRepository.findById(dto.listenerId)
            .orElseThrow { IllegalArgumentException("Listener not found: ${dto.listenerId}") }

        val speaker = playerRepository.findById(dto.speakerId)
            .orElseThrow { IllegalArgumentException("Speaker not found: ${dto.speakerId}") }

        val topic = imageRepository.findById(dto.topicImageId)
            .orElseThrow { IllegalArgumentException("Topic image not found: ${dto.topicImageId}") }

        val selectedImage = dto.listenerPickedImageId?.let { imageId ->
            imageRepository.findById(imageId).orElse(null)
        }

        val selectedSymbols = symbolRepository.findAllById(dto.speakerPickedSymbolsIds)

        val game = gameRepository.findById(session.gameId!!)
            .orElseThrow { IllegalArgumentException("Game not found: ${session.gameId}") }

        val newRound = Round(
            game = game,
            listener = listener,
            speaker = speaker,
            topic = topic,
            selectedImage = selectedImage,
            selectedSymbols = selectedSymbols,
            speakerResponseTime = dto.speakerResponseTime,
            listenerResponseTime = dto.listenerResponseTime,
            startedAt = dto.startedAt!!,
            endedAt = LocalDateTime.now()
        )

        return roundRepository.save(newRound)
    }
}