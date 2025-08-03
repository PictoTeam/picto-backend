package pl.umcs.picto3.game_config

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.umcs.picto3.game.GameService

// TODO: restrict access to admin users only
@RestController
@RequestMapping("/game-configs")
class GameConfigController(
    private val gameService: GameService
) {
    @PostMapping
    fun createGameConfig(@Valid @RequestBody gameConfig: GameConfigDto): ResponseEntity<GameConfigDto> {
        return ResponseEntity.ok(gameService.createGame(gameConfig))
    }

    @GetMapping
    fun getGameConfigs(): ResponseEntity<List<GameConfigDto>> {
        return ResponseEntity.ok(gameService.getGameConfigs())
    }

    @GetMapping("/{id}")
    fun getGameConfig(@PathVariable id: Long): ResponseEntity<GameConfigDto> {
        return ResponseEntity.ok(gameService.getGameConfig(id))
    }

    @PostMapping("/{id}")
    fun updateGameConfig(
        @PathVariable id: Long,
        @Valid @RequestBody gameConfig: GameConfigDto
    ): ResponseEntity<GameConfigDto> {
        return ResponseEntity.ok(gameService.updateGameConfig(id, gameConfig))
    }

    @DeleteMapping("/{id}")
    fun deleteGameConfig(@PathVariable id: Long): ResponseEntity<Void> {
        gameService.deleteGameConfig(id)
        return ResponseEntity.noContent().build()
    }
}
