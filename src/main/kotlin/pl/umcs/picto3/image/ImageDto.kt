package pl.umcs.picto3.image

import java.util.*

data class ImageDto(
    val imagePath: String,
    val imageId: UUID,
    val isMainImage: Boolean,
)