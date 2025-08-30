package pl.umcs.picto3.symbol

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import pl.umcs.picto3.storage.StorageService

@RestController
@RequestMapping("/symbols")
class SymbolController(
    private val storageService: StorageService
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadBatchSymbols(
        @RequestParam("files") files: List<MultipartFile>,
        @RequestParam("names") names: List<String>,
    ): ResponseEntity<String> {
        storageService.uploadBatchSymbols(files, names)
        return ResponseEntity.ok("Symbols uploaded successfully")
    }

    @GetMapping
    fun getAllSymbols(): ResponseEntity<List<SymbolDto>> {
        return ResponseEntity.ok(storageService.getAllSymbols())
    }

    @GetMapping("/{sessionAccessCode}")
    fun getSymbolsForGame(@PathVariable sessionAccessCode: String): ResponseEntity<SymbolMatrixDto> {
        return ResponseEntity.ok(storageService.getSymbolsForGameWithSessionAccessCode(sessionAccessCode))
    }
}
