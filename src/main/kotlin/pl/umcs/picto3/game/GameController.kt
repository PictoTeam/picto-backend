package pl.umcs.picto3.game

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// TODO: restrict access to admin users only
@RestController
@RequestMapping("/games")
class GameController(
    private val gameService: GameService
) {
}