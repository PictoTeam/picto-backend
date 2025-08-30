package pl.umcs.picto3.player


import jakarta.persistence.*
import pl.umcs.picto3.round.Round
import java.util.*

@Entity
@Table(name = "players")
data class Player(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @OneToMany
    val rounds: MutableList<Round> = mutableListOf(),

    @Transient
    val sessionAccessCode: String

)