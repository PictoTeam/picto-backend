package pl.umcs.picto3.symbol

import jakarta.persistence.*

@Entity
@Table(name = "symbols")
data class Symbol(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:Column(name = "file_path", nullable = false)
    val filePath: String
)