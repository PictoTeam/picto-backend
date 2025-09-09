package pl.umcs.picto3.image

import java.util.UUID


data class ImageDto(
    val imagePath: String,
    val imageId: UUID,
    val isTopic: Boolean,
)