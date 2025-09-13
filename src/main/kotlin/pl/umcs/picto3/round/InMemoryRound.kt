package pl.umcs.picto3.round

import java.time.LocalDateTime
import java.util.UUID

data class InMemoryRound(
    val gameAccessCode: String,
    val listenerId: UUID,
    val speakerId: UUID,
    val randomImagesIds: Set<UUID>,
    val topicImageId: UUID,
    val speakerPickedSymbolsIds: List<UUID> = mutableListOf(),
    val speakerResponseTime: Int? = null,
    val listenerPickedImageId: UUID? = null,
    val listenerResponseTime: Int? = null,
    val startedAt: LocalDateTime? = null,
    val endedAt: LocalDateTime? = null,
)