package pl.umcs.picto3.game.communication

import org.springframework.web.socket.WebSocketSession
import java.util.UUID

fun WebSocketSession.setPlayerId(playerId: UUID) {
    this.attributes["playerId"] = playerId
}

fun WebSocketSession.getPlayerId(): UUID? {
    return this.attributes["playerId"] as? UUID
}

fun WebSocketSession.setAccessCode(accessCode: String) {
    this.attributes["accessCode"] = accessCode
}

fun WebSocketSession.getAccessCode(): String? {
    return this.attributes["accessCode"] as? String
}

fun WebSocketSession.setRoundId(roundId: UUID) {
    this.attributes["roundId"] = roundId
}

fun WebSocketSession.getRoundId(): UUID? {
    return this.attributes["roundId"] as? UUID
}
