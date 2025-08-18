package pl.umcs.picto3.session


import pl.umcs.picto3.gameconfig.GameConfig
import pl.umcs.picto3.player.Player

data class Session(
    val gameConfig: GameConfig,
    val accessCode: String,
    // TODO: check if open sockets/socket states should be stored here
    val startingPlayers: MutableSet<Player>
)