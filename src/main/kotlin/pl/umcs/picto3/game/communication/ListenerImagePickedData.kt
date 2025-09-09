package pl.umcs.picto3.game.communication

import java.util.UUID

data class ListenerImagePickedData(
    val imageId: UUID,
    val listenerResponseTime: Int
)