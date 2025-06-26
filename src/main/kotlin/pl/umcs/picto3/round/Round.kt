package pl.umcs.picto3.round

import jakarta.persistence.*
import pl.umcs.picto3.game.Game
import pl.umcs.picto3.image.Image
import pl.umcs.picto3.player.Player
import pl.umcs.picto3.symbol.Symbol
import java.time.LocalDateTime

@Entity
@Table(name = "rounds")
data class Round(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:ManyToOne(fetch = FetchType.LAZY)
    val game: Game,

    @field:ManyToOne(fetch = FetchType.LAZY)
    val listener: Player,

    @field:ManyToOne(fetch = FetchType.LAZY)
    val speaker: Player,

    @field:ManyToOne(fetch = FetchType.LAZY)
    val topic: Image,

    @field:ManyToOne(fetch = FetchType.LAZY)
    val selectedImage: Image? = null,

    @field:OneToMany(fetch = FetchType.LAZY)
    val selectedSymbols: List<Symbol> = emptyList(),

    @field:Column(name = "speaker_response_time_ms", nullable = true)
    val speakerResponseTime: Short? = null,

    @field:Column(name = "listener_response_time_ms", nullable = true)
    val listenerResponseTime: Short? = null,

    @field:Column(name = "started_at", nullable = false)
    val startedAt: LocalDateTime = LocalDateTime.now(),

    @field:Column(name = "ended_at")
    val endedAt: LocalDateTime? = null
)