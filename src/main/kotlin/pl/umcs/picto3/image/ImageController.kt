package pl.umcs.picto3.image


import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import pl.umcs.picto3.common.StorageService


@RestController
@RequestMapping("/images")
class ImageController(
    private val storageService: StorageService
) {

    @PostMapping
    fun uploadBatchImages(
        @RequestParam("file") files: List<MultipartFile>,
        @RequestParam("names") names: List<String>
    ): ResponseEntity<String> {
        storageService.uploadBatch(files, names)
        TODO("implement what should be returned & storageException handling")

    }

}