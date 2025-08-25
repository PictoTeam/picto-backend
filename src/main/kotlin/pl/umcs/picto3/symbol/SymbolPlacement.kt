package pl.umcs.picto3.symbol

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "symbol_placements")
data class SymbolPlacement(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "row_index")
    val rowIndex: Short,

    @Column(name = "column_index")
    val columnIndex: Short,

    @ManyToOne
    @JoinColumn(name = "symbol_id")
    val symbol: Symbol
)