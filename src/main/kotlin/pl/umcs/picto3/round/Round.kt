package pl.umcs.picto3.round

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import pl.umcs.picto3.image.Image
import pl.umcs.picto3.player.Player
import pl.umcs.picto3.symbol.Symbol
import java.time.LocalDateTime

@Entity
@Table(name = "rounds")
data class Round(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @ManyToOne
    val listener: Player,

    @ManyToOne
    val speaker: Player,

    @ManyToOne
    @JoinColumn(name = "topic_image_id")
    val topic: Image,

    @ManyToOne
    @JoinColumn(name = "selected_image_id")
    val selectedImage: Image?,

    @OneToMany
    @JoinColumn(name = "symbol_id")
    val selectedSymbols: List<Symbol> = emptyList(),

    @Column(name = "speaker_response_time_ms")
    val speakerResponseTime: Short?,

    @Column(name = "listener_response_time_ms")
    val listenerResponseTime: Short?,

    @CreationTimestamp
    @Column(name = "started_at")
    val startedAt: LocalDateTime,

    @Column(name = "ended_at")
    val endedAt: LocalDateTime?
)