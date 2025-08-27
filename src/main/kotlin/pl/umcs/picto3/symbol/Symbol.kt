package pl.umcs.picto3.symbol

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "symbols")
data class Symbol(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column
    val storedFileName: String,

    @Column
    val fileName: String,

    @Column(name = "file_hash", unique = true, length = 64)
    val fileHash: String
)