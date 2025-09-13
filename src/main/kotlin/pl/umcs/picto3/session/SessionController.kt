package pl.umcs.picto3.session

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID


// TODO: restrict access to admin users only
@RestController
@RequestMapping("/sessions")
class SessionController(
    private val sessionService: SessionService
) {

    //TODO dorobic endpoint do pauzowania gry na podstawie access id zwyskly patch wystarczy imo
    @PostMapping
    fun createSession(@RequestBody request: CreateSessionRequest): ResponseEntity<CreateSessionResponse> {
        val accessCode = sessionService.createSession(request.gameConfigId)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(CreateSessionResponse(accessCode))
    }

    @PostMapping("/{sessionAccessCode}/start")
    fun startGameInSession(@PathVariable sessionAccessCode: String): ResponseEntity<GameStartResponse> {
        sessionService.startGameInSessionWithGivenAccessCode(sessionAccessCode)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(GameStartResponse("Game started successfully", sessionAccessCode))
    }

    @PatchMapping("/{sessionAccessCode}/finish")
    fun finishGameInSession(@PathVariable sessionAccessCode: String): ResponseEntity<Unit> {
        sessionService.finishGameInSessionWithGivenAccessCode(sessionAccessCode)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{sessionAccessCode}")
    fun closeSession(@PathVariable sessionAccessCode: String): ResponseEntity<Unit> {
        sessionService.closeSession(sessionAccessCode)
        return ResponseEntity.noContent().build()
    }
}


data class CreateSessionRequest(
    val gameConfigId: UUID
)

data class CreateSessionResponse(
    val accessCode: String
)

data class GameStartResponse(
    val message: String,
    val sessionAccessCode: String
)