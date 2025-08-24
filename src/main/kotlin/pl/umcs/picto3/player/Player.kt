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

    @Column(name = "uuid")
    val uuid: UUID = UUID.randomUUID(),

    @OneToMany
    val rounds: List<Round> = emptyList(),

    @Transient
    val sessionAccessCode: String

)