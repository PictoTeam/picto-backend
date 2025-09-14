package pl.umcs.picto3.session


import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import pl.umcs.picto3.gameconfig.GameConfig
import java.util.UUID

@Entity
@Table(name = "sessions")
data class Session(
    @Id
    @Column(name = "id")
    val id: String,

    @Column(name = "game_id")
    val gameId: UUID? = null,

    @ManyToOne
    @JoinColumn(name = "game_config_id")
    val gameConfig: GameConfig,
)