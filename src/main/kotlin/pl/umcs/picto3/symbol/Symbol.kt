package pl.umcs.picto3.symbol

import jakarta.persistence.*

@Entity
@Table(name = "symbols")
data class Symbol(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @Column(name = "file_path")
    val filePath: String
)