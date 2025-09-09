package pl.umcs.picto3.round


import java.util.UUID

data class InMemoryRound(
    val gameAccessCode: String,
    val listenerId: UUID,
    val speakerId: UUID,
    val randomImages: Set<UUID>,
    val topicImageId: UUID,
    val speakerPickedSymbolsIds: Set<UUID> = mutableSetOf(),
    val listenerPickedImageId: UUID? = null,
    val speakerResponseTime: Int? = null,
    val listenerResponseTime: Int? = null
)