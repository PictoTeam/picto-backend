package pl.umcs.picto3.game

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import pl.umcs.picto3.preEdit


@Document
data class Game(
    @Id var id: Long,
    var name: String
    )


