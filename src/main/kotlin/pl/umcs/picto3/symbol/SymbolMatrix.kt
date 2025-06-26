package pl.umcs.picto3.symbol

import jakarta.persistence.*

@Entity
@Table(name = "symbol_matrices")
data class SymbolMatrix(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "row_size")
    val rowSize: Short = 3,

    @Column(name = "column_size")
    val columnSize: Short = 3,

    @OneToMany
    val symbolPlacements: Set<SymbolPlacement>
)