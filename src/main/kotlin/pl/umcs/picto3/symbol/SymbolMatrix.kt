package pl.umcs.picto3.symbol

import jakarta.persistence.*

@Entity
@Table(name = "symbol_matrices")
data class SymbolMatrix(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:Column(name = "row_size", nullable = false)
    val rowSize: Short = 3,

    @field:Column(name = "column_size", nullable = false)
    val columnSize: Short = 3,

    @field:OneToMany
    val symbols: List<Symbol> = emptyList()
)