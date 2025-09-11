package pl.umcs.picto3.game


import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import pl.umcs.picto3.gameconfig.GameConfig
import pl.umcs.picto3.round.Round
import java.time.LocalDateTime


@Entity
@Table(name = "games")
data class Game(
    @Id
    val id: String,

    @ManyToOne
    val gameConfig: GameConfig,

    @OneToMany(mappedBy = "game")
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
