package pl.umcs.picto3.gameconfig

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

// TODO: restrict access to admin users only
@RestController
@RequestMapping("/game-configs")
class GameConfigController(
    private val gameConfigService: GameConfigService
) {
    @PostMapping
    fun createGameConfig(@Valid @RequestBody gameConfig: GameConfigDto): ResponseEntity<GameConfigDto> {
        return ResponseEntity.ok(gameConfigService.createGame(gameConfig))
    }

    @GetMapping
    fun getGameConfigs(): ResponseEntity<List<GameConfigDto>> {
        return ResponseEntity.ok(gameConfigService.getGameConfigs())
    }

    @GetMapping("/{id}")
    fun getGameConfig(@PathVariable id: Long): ResponseEntity<GameConfigDto> {
        return ResponseEntity.ok(gameConfigService.getGameConfig(id))
    }

    @PostMapping("/{id}")
    fun updateGameConfig(
        @PathVariable id: Long,
        @Valid @RequestBody gameConfig: GameConfigDto
    ): ResponseEntity<GameConfigDto> {
        return ResponseEntity.ok(gameConfigService.updateGameConfig(id, gameConfig))
    }

    @DeleteMapping("/{id}")
    fun deleteGameConfig(@PathVariable id: Long): ResponseEntity<Void> {
        gameConfigService.deleteGameConfig(id)
        return ResponseEntity.noContent().build()
    }
}
