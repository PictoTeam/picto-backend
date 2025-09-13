package pl.umcs.picto3.game.communication

import java.util.UUID

data class SpeakerSymbolsPickedData(
    val roundId: UUID,
    val symbolsIds: List<UUID>,
    val speakerResponseTime: Int,
)
