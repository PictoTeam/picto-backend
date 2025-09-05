package pl.umcs.picto3.session

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

// TODO: restrict access to admin users only
@RestController
@RequestMapping("/sessions")
class SessionController(
    private val sessionService: SessionService
) {
    @PostMapping("/{gameConfigId}")
    fun createSession(@PathVariable gameConfigId: UUID): ResponseEntity<String> {
        return ResponseEntity.ok(sessionService.createSession(gameConfigId))
    }

    @PatchMapping("/{sessionAccessCode}")
    fun startSession(@PathVariable sessionAccessCode: String): ResponseEntity<String> {
        return ResponseEntity.ok(sessionService.startSession(sessionAccessCode))
    }
}