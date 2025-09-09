package pl.umcs.picto3.round


import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import pl.umcs.picto3.game.Game
import pl.umcs.picto3.image.Image
import pl.umcs.picto3.player.Player
import pl.umcs.picto3.symbol.Symbol
import java.time.LocalDateTime
import java.util.UUID


@Entity
@Table(name = "rounds")
data class Round(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne
    @JoinColumn(name = "game_id")
    val game: Game,

    @ManyToOne
    @JoinColumn(name = "listener_id")
    val listener: Player,

    @ManyToOne
    @JoinColumn(name = "speaker_id")
    val speaker: Player,

    @ManyToOne
    @JoinColumn(name = "topic_image_id")
    val topic: Image,

    @ManyToOne
    @JoinColumn(name = "selected_image_id")
    val selectedImage: Image?,

    @OneToMany
    @JoinColumn(name = "round_id")
    val selectedSymbols: List<Symbol> = emptyList(),

    @Column(name = "speaker_response_time_ms")
    val speakerResponseTime: Int?,

    @Column(name = "listener_response_time_ms")
    val listenerResponseTime: Int?,

    @CreationTimestamp
    @Column(name = "started_at")
    val startedAt: LocalDateTime,

    @Column(name = "ended_at")
    val endedAt: LocalDateTime?
)
