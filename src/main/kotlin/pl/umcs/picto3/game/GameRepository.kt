package pl.umcs.picto3.game

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID


interface GameRepository : JpaRepository<Game, UUID> {
    fun getGameBySessionAccessCode(sessionAccessCode: String): Game //TODO trzeba sprawdzic czy to jest potrzebne
    fun existsBySessionAccessCode(sessionAccessCode: String): Boolean
}