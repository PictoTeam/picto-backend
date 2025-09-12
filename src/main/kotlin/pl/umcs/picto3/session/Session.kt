package pl.umcs.picto3.session


import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import pl.umcs.picto3.gameconfig.GameConfig
import java.util.UUID

@Entity
@Table(name = "sessions")
data class Session(
    @Id
    val id: String,
    val gameId: UUID? = null,
    @ManyToOne
    val gameConfig: GameConfig,
)