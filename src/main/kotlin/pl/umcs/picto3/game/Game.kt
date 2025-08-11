package pl.umcs.picto3.game

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import pl.umcs.picto3.game_config.GameConfig
import pl.umcs.picto3.round.Round
import java.time.LocalDateTime

@Entity
@Table(name = "games")
data class Game(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @ManyToOne
    val gameConfig: GameConfig,

    @OneToMany
    val rounds: List<Round>,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime,

    @Column(name = "started_at")
    val startedAt: LocalDateTime?,

    @Column(name = "ended_at")
    val endedAt: LocalDateTime?,
)
