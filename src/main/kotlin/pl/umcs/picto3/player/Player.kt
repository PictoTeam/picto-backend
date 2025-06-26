package pl.umcs.picto3.player

import jakarta.persistence.*
import pl.umcs.picto3.round.Round

@Entity
@Table(name = "players")
data class Player(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "uuid64", nullable = false)
    val uuid64: String,

    @OneToMany(fetch = FetchType.LAZY)
    val rounds: List<Round> = emptyList(),
)