package pl.umcs.picto3.gameconfig

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import pl.umcs.picto3.image.Image
import pl.umcs.picto3.symbol.SymbolMatrix
import java.time.Instant

@Entity
@Table(name = "game_configs")
data class GameConfig(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @ManyToOne
    val symbols: SymbolMatrix,

    @ManyToMany
    @JoinTable(
        name = "game_configs_images",
        joinColumns = [JoinColumn(name = "game_config_id")],
        inverseJoinColumns = [JoinColumn(name = "image_id")]
    )
    val images: Set<Image>,

    @Column(name = "speaker_image_count")
    val speakerImageCount: Short = 4,

    @Column(name = "listener_image_count")
    val listenerImageCount: Short = 4,

    @Column(name = "speaker_answer_time_ms")
    val speakerAnswerTime: Int = -1,

    @Column(name = "listener_answer_time_ms")
    val listenerAnswerTime: Int = -1,

    @Column(name = "correct_answer_points")
    val correctAnswerPoints: Short = 1,

    @Column(name = "wrong_answer_points")
    val wrongAnswerPoints: Short = -1,

    @Column(name = "result_screen_time_ms")
    val resultScreenTime: Int = 3000,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    val createdAt: Instant?,
)