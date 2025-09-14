package pl.umcs.picto3.session

import pl.umcs.picto3.gameconfig.GameConfig

data class SessionCreatedEvent(
    val accessCode: String,
    val gameConfig: GameConfig
)