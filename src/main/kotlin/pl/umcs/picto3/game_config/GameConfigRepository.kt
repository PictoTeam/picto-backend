package pl.umcs.picto3.game_config

import org.springframework.data.jpa.repository.JpaRepository

interface GameConfigRepository : JpaRepository<GameConfig, Long> {
}