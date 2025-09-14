package pl.umcs.picto3.image

import java.util.UUID

data class InMemoryImage(
    val imageId: UUID,
    val imagePath: String,
)