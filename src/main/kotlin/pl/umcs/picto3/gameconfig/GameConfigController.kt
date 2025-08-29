package pl.umcs.picto3.gameconfig

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

// TODO: restrict access to admin users only
@RestController
@RequestMapping("/game-configs")
class GameConfigController(
    private val gameConfigService: GameConfigService
) {
    @PostMapping
    fun createGameConfig(@Valid @RequestBody gameConfig: GameConfigDto): ResponseEntity<GameConfigDto> {
        return ResponseEntity.ok(gameConfigService.createGameConfig(gameConfig))
    }

    @GetMapping
    fun getGameConfigs(): ResponseEntity<List<GameConfigDto>> {
        return ResponseEntity.ok(gameConfigService.getGameConfigs())
    }

    @GetMapping("/{id}")
    fun getGameConfig(@PathVariable id: UUID): ResponseEntity<GameConfigDto> {
        return ResponseEntity.ok(gameConfigService.getGameConfig(id))
    }

    @PutMapping("/{id}")
    fun updateGameConfig(
        @PathVariable id: UUID,
        @Valid @RequestBody gameConfig: GameConfigDto
    ): ResponseEntity<GameConfigDto> {
        return ResponseEntity.ok(gameConfigService.updateGameConfig(id, gameConfig))
    }

    @DeleteMapping("/{id}")
    fun deleteGameConfig(@PathVariable id: UUID): ResponseEntity<Void> {
        gameConfigService.deleteGameConfig(id)
        return ResponseEntity.noContent().build()
    }
}
