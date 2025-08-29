package pl.umcs.picto3.image

import org.springframework.stereotype.Component

@Component
class ImageMapper {

    private val serverPath = "src/main/resources/static/"

    fun toNotMainDto(image: Image): ImageDto {
        return ImageDto(serverPath + image.fileName, image.id!!, false)
    }
}