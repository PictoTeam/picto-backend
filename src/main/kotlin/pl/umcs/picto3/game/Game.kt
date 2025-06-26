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
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:OneToMany(fetch = FetchType.LAZY)
    val rounds: List<Round>,

    @field:ManyToOne(fetch = FetchType.LAZY)
    val symbols: SymbolMatrix,

    @field:ManyToMany(fetch = FetchType.LAZY)
    val images: Set<Image>,

    @field:Column(name = "speaker_image_count", nullable = false)
    val speakerImageCount: Short = 4,

    @field:Column(name = "listener_image_count", nullable = false)
    val listenerImageCount: Short = 4,

    @field:Column(name = "speaker_answer_time_ms", nullable = false)
    val speakerAnswerTime: Short = -1,

    @field:Column(name = "listener_answer_time_ms", nullable = false)
    val listenerAnswerTime: Short = -1,

    @field:Column(name = "correct_answer_points", nullable = false)
    val correctAnswerPoints: Short = 1,

    @field:Column(name = "wrong_answer_points", nullable = false)
    val wrongAnswerPoints: Short = -1,

    @field:Column(name = "result_screen_time_ms", nullable = false)
    val resultScreenTime: Short = 3000,

    @field:CreationTimestamp
    @field:Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @field:UpdateTimestamp
    @field:Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    @field:Column(name = "started_at")
    val startedAt: LocalDateTime? = null,

    @field:Column(name = "ended_at")
    val endedAt: LocalDateTime? = null,
)
