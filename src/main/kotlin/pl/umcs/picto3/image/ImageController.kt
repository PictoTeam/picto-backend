package pl.umcs.picto3.image

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import pl.umcs.picto3.common.StorageService
import reactor.core.publisher.Mono

// TODO: restrict access to admin users only
@RestController
@RequestMapping("/images")
class ImageController(
    private val storageService: StorageService
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadBatchImages(
        @RequestParam("file") files: List<MultipartFile>,
        @RequestParam("names") names: List<String>,
    ): ResponseEntity<String> {
        storageService.uploadBatch(files, names)
        return ResponseEntity.ok("Files uploaded successfully")
    }

    @GetMapping // keep this endpoint to test webflux with rest
    fun test(): Mono<ResponseEntity<String>> {
        return Mono.just(ResponseEntity.ok("Files uploaded successfully"))
    }

}