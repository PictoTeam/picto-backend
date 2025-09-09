package pl.umcs.picto3.image

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import pl.umcs.picto3.storage.StorageService

// TODO: restrict access to admin users only
@RestController
@RequestMapping("/images")
class ImageController(
    private val storageService: StorageService
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadBatchImages(
        @RequestParam("files") files: List<MultipartFile>,
        @RequestParam("names") names: List<String>,
    ): ResponseEntity<String> {
        storageService.uploadBatchImages(files, names)
        return ResponseEntity.ok("Files uploaded successfully")
    }

    @GetMapping
    fun getAllImages(): ResponseEntity<List<ImageDto>> {
        return ResponseEntity.ok(storageService.getAllImages())
    }

    @GetMapping("/{sessionAccessCode}")
    fun getImagesRoundSet(@PathVariable sessionAccessCode: String): ResponseEntity<List<ImageDto>> {
        return ResponseEntity.ok(storageService.getImagesForRoundWithSessionAccessCode(sessionAccessCode))
    }
}