package pl.umcs.picto3.round

import pl.umcs.picto3.image.InMemoryImage
import java.time.LocalDateTime
import java.util.UUID

data class InMemoryRound(
    val gameAccessCode: String,
    val listenerId: UUID, //todo moze by tutaj wrzucac calego playera ?
    val speakerId: UUID,
    val randomImages: Set<InMemoryImage>,
    val topicImageId: UUID,
    val speakerPickedSymbolsIds: List<UUID> = mutableListOf(),
    val speakerResponseTime: Int? = null,
    val listenerPickedImageId: UUID? = null,
    val listenerResponseTime: Int? = null,
    val startedAt: LocalDateTime? = null,
    val endedAt: LocalDateTime? = null,
)