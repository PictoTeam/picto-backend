package pl.umcs.picto3.round

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RoundResultRepository : JpaRepository<RoundResult, UUID> {
    fun findByRoundId(roundId: UUID): RoundResult?

    @Query("SELECT rr FROM RoundResult rr JOIN rr.round r WHERE r.game.sessionAccessCode = :sessionAccessCode")
    fun findAllBySessionAccessCode(sessionAccessCode: String): List<RoundResult>

    @Query("SELECT COUNT(rr) FROM RoundResult rr JOIN rr.round r WHERE r.game.sessionAccessCode = :sessionAccessCode AND rr.wasSuccessful = true")
    fun countSuccessfulRoundsBySessionAccessCode(sessionAccessCode: String): Long

    @Query("SELECT COUNT(rr) FROM RoundResult rr JOIN rr.round r WHERE r.game.sessionAccessCode = :sessionAccessCode")
    fun countTotalRoundsBySessionAccessCode(sessionAccessCode: String): Long

    @Query("SELECT SUM(rr.pointsAwarded) FROM RoundResult rr JOIN rr.round r WHERE r.game.sessionAccessCode = :sessionAccessCode")
    fun sumPointsBySessionAccessCode(sessionAccessCode: String): Long?
}