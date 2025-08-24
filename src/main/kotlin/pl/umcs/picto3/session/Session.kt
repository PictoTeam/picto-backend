package pl.umcs.picto3.session


import pl.umcs.picto3.image.Image
import pl.umcs.picto3.player.Player
import pl.umcs.picto3.symbol.SymbolMatrix
import java.util.concurrent.ConcurrentHashMap

data class Session(
    val symbols: SymbolMatrix,
    val images: Set<Image>,
    val speakerImageCount: Short? = null,
    val listenerImageCount: Short? = null,
    val speakerAnswerTime: Int? = null,
    val listenerAnswerTime: Int? = null,
    val correctAnswerPoints: Short? = null,
    val wrongAnswerPoints: Short? = null,
    val resultScreenTime: Short? = null,
    val accessCode: String,
    val activeMembers: MutableSet<Player> = ConcurrentHashMap.newKeySet(),
    val adminsWsSessions: MutableSet<String> = ConcurrentHashMap.newKeySet(),
    val adminApiKey: String
)