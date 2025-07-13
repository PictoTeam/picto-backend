package pl.umcs.picto3.session

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/sessions")
class SessionController(
    private val sessionService: SessionService
) {
    @PostMapping
    suspend fun createSession(@Valid @RequestBody sessionSetup: SessionConfigDto): ResponseEntity<String> {
        return ResponseEntity.ok(sessionService.createSession(sessionSetup))
    }
}