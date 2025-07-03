package pl.umcs.picto3.session

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/sessions")
class SessionController(
    private val sessionService: SessionService
) {
    @PostMapping
    fun createSession(@Valid @RequestBody sessionSetup: SessionConfigDto): Session {
        sessionService.createSession(sessionSetup)
        TODO("response code & error handling")
    }
}