package pl.umcs.picto3.game

import java.util.UUID


data class GameFinishedEvent(val accessCode: String, val gameId: UUID)
