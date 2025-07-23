package pl.umcs.picto3.symbol

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/symbols")
class SymbolController(
    private val symbolService: SymbolService
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadBatchSymbols(
        @RequestParam("file") files: List<MultipartFile>,
        @RequestParam("names") names: List<String>,
    ): ResponseEntity<String> {
        symbolService.uploadBatch(files, names)
        return ResponseEntity.ok("Symbols uploaded successfully")
    }
}
