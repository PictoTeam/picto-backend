package pl.umcs.picto3.game

import pl.umcs.picto3.gameconfig.GameConfig
import pl.umcs.picto3.player.Player
import pl.umcs.picto3.round.InMemoryRound
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap


data class InMemoryGame(
    val gameConfig: GameConfig,
    val presentPlayers: MutableMap<UUID, Player> = ConcurrentHashMap(),
    val gameRounds: MutableMap<UUID, InMemoryRound> = ConcurrentHashMap(),
)