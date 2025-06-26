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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    val game: Game,

    @ManyToOne(fetch = FetchType.LAZY)
    val listener: Player,

    @ManyToOne(fetch = FetchType.LAZY)
    val speaker: Player,

    @ManyToOne(fetch = FetchType.LAZY)
    val topic: Image,

    @ManyToOne(fetch = FetchType.LAZY)
    val selectedImage: Image? = null,

    @OneToMany(fetch = FetchType.LAZY)
    val selectedSymbols: List<Symbol> = emptyList(),

    @Column(name = "speaker_response_time_ms", nullable = true)
    val speakerResponseTime: Short? = null,

    @Column(name = "listener_response_time_ms", nullable = true)
    val listenerResponseTime: Short? = null,

    @Column(name = "started_at", nullable = false)
    val startedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "ended_at")
    val endedAt: LocalDateTime? = null
)