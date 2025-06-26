package pl.umcs.picto3.symbol

import jakarta.persistence.*

@Entity
@Table(name = "symbol_placements")
data class SymbolPlacement(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "row_index")
    val rowIndex: Short,

    @Column(name = "column_index")
    val columnIndex: Short,

    @ManyToOne
    @JoinColumn(name = "symbol_id")
    val symbol: Symbol
)