package pl.umcs.picto3.player


import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.web.socket.WebSocketSession
import java.util.UUID


@Entity
@Table(name = "players")
data class Player(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Transient
    val wsSession: WebSocketSession,

    @Transient
    val sessionAccessCode: String,

    @Transient
    var lastPlayerId: UUID? = null

)