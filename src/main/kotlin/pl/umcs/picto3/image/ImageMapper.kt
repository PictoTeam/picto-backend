package pl.umcs.picto3.image

import org.springframework.stereotype.Component

@Component
class ImageMapper {
    fun toNotMainDto(image: Image): ImageDto {
        return ImageDto("/static/images/" + image.storedFileName, image.id!!, false)
    }
}