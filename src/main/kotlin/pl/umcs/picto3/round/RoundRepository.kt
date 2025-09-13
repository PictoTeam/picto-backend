package pl.umcs.picto3.round

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RoundRepository : JpaRepository<Round, UUID> {
    fun findAllByGameId(gameId: UUID): List<Round>
}