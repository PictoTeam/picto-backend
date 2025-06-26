package pl.umcs.picto3.player

import jakarta.persistence.*
import pl.umcs.picto3.round.Round

@Entity
@Table(name = "players")
data class Player(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:Column(name = "uuid64", nullable = false)
    val uuid64: String,

    @field:OneToMany(fetch = FetchType.LAZY)
    val rounds: List<Round> = emptyList(),
)