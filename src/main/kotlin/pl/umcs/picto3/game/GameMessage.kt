package pl.umcs.picto3.game

enum class GameMessage(val type: String) {
    // Player events
    PLAYER_JOINED("PLAYER_JOINED"),
    PLAYER_LEFT("PLAYER_LEFT"),

    // Admin events
    ADMIN_CONNECTED("ADMIN_CONNECTED"),

    GAME_STARTED("GAME_STARTED"),
    ERROR("ERROR"),
}