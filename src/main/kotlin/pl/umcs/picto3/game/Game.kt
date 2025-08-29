package pl.umcs.picto3.game

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import pl.umcs.picto3.gameconfig.GameConfig
import pl.umcs.picto3.round.Round
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "games")
data class Game(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne
    val gameConfig: GameConfig,

    @OneToMany
    val rounds: MutableList<Round> = mutableListOf(),

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime? = null,

    @Column(name = "started_at")
    val startedAt: LocalDateTime? = null,

    @Column(name = "ended_at")
    val endedAt: LocalDateTime? = null,

    @Column(name = "session_access_code")
    val sessionAccessCode: String
)
