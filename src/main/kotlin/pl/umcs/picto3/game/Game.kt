package pl.umcs.picto3.game

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import pl.umcs.picto3.image.Image
import pl.umcs.picto3.round.Round
import pl.umcs.picto3.symbol.SymbolMatrix
import java.time.LocalDateTime

@Entity
@Table(name = "games")
data class Game(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @OneToMany
    val rounds: List<Round>,

    @ManyToOne
    val symbols: SymbolMatrix,

    @ManyToMany
    val images: Set<Image>,

    @Column(name = "speaker_image_count")
    val speakerImageCount: Short = 4,

    @Column(name = "listener_image_count")
    val listenerImageCount: Short = 4,

    @Column(name = "speaker_answer_time_ms")
    val speakerAnswerTime: Short = -1,

    @Column(name = "listener_answer_time_ms")
    val listenerAnswerTime: Short = -1,

    @Column(name = "correct_answer_points")
    val correctAnswerPoints: Short = 1,

    @Column(name = "wrong_answer_points")
    val wrongAnswerPoints: Short = -1,

    @Column(name = "result_screen_time_ms")
    val resultScreenTime: Short = 3000,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime,

    @UpdateTimestamp
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime,

    @Column(name = "started_at")
    val startedAt: LocalDateTime?,

    @Column(name = "ended_at")
    val endedAt: LocalDateTime?,
)
