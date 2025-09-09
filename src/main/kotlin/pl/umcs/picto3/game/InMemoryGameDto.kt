package pl.umcs.picto3.game

import org.springframework.web.socket.WebSocketSession
import pl.umcs.picto3.gameconfig.GameConfig
import pl.umcs.picto3.round.InMemoryRound
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap


data class InMemoryGameDto(
    var gameConfig: GameConfig,
    val gameRounds: MutableMap<UUID, InMemoryRound> = ConcurrentHashMap(),
    val webSocketSessions: MutableSet<WebSocketSession> = mutableSetOf(),
)