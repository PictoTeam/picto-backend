package pl.umcs.picto3.symbol

import jakarta.persistence.*

@Entity
@Table(name = "symbol_matrices")
data class SymbolMatrix(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "row_size", nullable = false)
    val rowSize: Short = 3,

    @Column(name = "column_size", nullable = false)
    val columnSize: Short = 3,

    @OneToMany
    val symbols: List<Symbol> = emptyList()
)