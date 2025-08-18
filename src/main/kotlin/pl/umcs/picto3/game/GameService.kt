package pl.umcs.picto3.game

import org.springframework.stereotype.Service
import pl.umcs.picto3.gameconfig.GameConfigDto

@Service
class GameService() {
    fun createGame(gameConfigId: Long): GameConfigDto {
        TODO("Implement game creation logic")
    }
}