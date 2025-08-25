package pl.umcs.picto3.gameconfig

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface GameConfigRepository : JpaRepository<GameConfig, UUID> {
}