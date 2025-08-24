package pl.umcs.picto3.session


import pl.umcs.picto3.gameconfig.GameConfig
import pl.umcs.picto3.player.Player
import java.util.concurrent.ConcurrentHashMap

data class Session(
    val gameConfig: GameConfig,
    val accessCode: String,
    val activeMembers: MutableSet<Player> = ConcurrentHashMap.newKeySet(),
    val adminsWsSessions: MutableSet<String> = ConcurrentHashMap.newKeySet(),
    val adminApiKey: String
)