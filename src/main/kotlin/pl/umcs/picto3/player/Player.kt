package pl.umcs.picto3.player

import jakarta.persistence.*
import pl.umcs.picto3.round.Round
import java.util.*

@Entity
@Table(name = "players")
data class Player(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @Column(name = "uuid")
    val uuid: UUID,

    @OneToMany
    val rounds: List<Round>
)