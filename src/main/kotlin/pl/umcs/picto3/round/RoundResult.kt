package pl.umcs.picto3.round

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "round_results")
data class RoundResult(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @OneToOne
    @JoinColumn(name = "round_id")
    val round: Round,

    @Column(name = "was_successful")
    val wasSuccessful: Boolean,

    @Column(name = "points_awarded")
    val pointsAwarded: Int
)