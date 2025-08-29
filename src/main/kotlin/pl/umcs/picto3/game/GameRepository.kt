package pl.umcs.picto3.game

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface GameRepository : JpaRepository<Game, UUID> {
    fun getGameBySessionAccessCode(sessionAccessCode: String): Game
}