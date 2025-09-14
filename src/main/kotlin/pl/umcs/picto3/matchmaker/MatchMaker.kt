package pl.umcs.picto3.matchmaker

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import pl.umcs.picto3.player.Player


@Component
class MatchMaker {

    private val waitingQueue = mutableMapOf<String, MutableList<Player>>()

    @Volatile
    var isGameGoing: Boolean = false

    @Value("\${matchmaker.frequency}")
    private val matchmakerRate: Long = 5000L

    init {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                if (isGameGoing) {
                    synchronized(waitingQueue) {
                        waitingQueue.forEach { (_, queue) ->
                            val pairedPlayers = mutableListOf<Pair<Player, Player>>()

                            for (i in 0 until queue.size) {
                                val p1 = queue[i]
                                for (j in i + 1 until queue.size) {
                                    val p2 = queue[j]
                                    if (p1.lastOpponentId != p2.id && p2.lastOpponentId != p1.id) {
                                        pairedPlayers.add(p1 to p2)
                                        break
                                    }
                                }
                            }

                            pairedPlayers.forEach { (p1, p2) ->
                                queue.remove(p1)
                                queue.remove(p2)
                                p1.lastOpponentId = p2.id
                                p2.lastOpponentId = p1.id

                                CoroutineScope(Dispatchers.IO).launch {
                                    onPairReady(p1, p2)
                                    removePlayerFromQueue(p1)
                                    removePlayerFromQueue(p2)
                                }
                            }
                        }
                    }
                }

                kotlinx.coroutines.delay(matchmakerRate)
            }
        }
    }

    lateinit var onPairReady: (Player, Player) -> Unit

    fun addPlayerToQueue(player: Player) {
        val queue = waitingQueue.computeIfAbsent(player.sessionAccessCode) { mutableListOf() }
        synchronized(queue) { queue.add(player) }
    }

    fun removePlayerFromQueue(player: Player) {
        val queue = waitingQueue[player.sessionAccessCode] ?: return
        synchronized(queue) { queue.removeIf { it.id == player.id } }
    }
}