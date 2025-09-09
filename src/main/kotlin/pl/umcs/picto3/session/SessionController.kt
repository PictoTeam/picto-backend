package pl.umcs.picto3.session

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID


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