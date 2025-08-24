package pl.umcs.picto3.symbol

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import pl.umcs.picto3.common.StorageService

@RestController
@RequestMapping("/symbols")
class SymbolController(
    private val storageService: StorageService
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadBatchSymbols(
        @RequestParam("file") files: List<MultipartFile>,
        @RequestParam("names") names: List<String>,
    ): ResponseEntity<String> {
        storageService.uploadBatchSymbols(files, names)
        return ResponseEntity.ok("Symbols uploaded successfully")
    }
}
