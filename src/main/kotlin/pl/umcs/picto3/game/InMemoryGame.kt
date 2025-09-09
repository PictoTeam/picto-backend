package pl.umcs.picto3.game

import org.springframework.web.socket.WebSocketSession
import pl.umcs.picto3.gameconfig.GameConfig
import pl.umcs.picto3.round.InMemoryRound


data class InMemoryGameDto(
    var gameConfig: GameConfig,
    val gameRounds: MutableSet<InMemoryRound> = mutableSetOf(),
    val webSocketSessions: MutableSet<WebSocketSession> = mutableSetOf(),
)