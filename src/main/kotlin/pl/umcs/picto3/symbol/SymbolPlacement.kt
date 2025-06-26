package pl.umcs.picto3.symbol

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "symbol_placements")
data class SymbolPlacement(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "row_index", nullable = false)
    val rowIndex: Short,

    @Column(name = "column_index", nullable = false)
    val columnIndex: Short,

    @ManyToOne(fetch = FetchType.LAZY)
    val symbol: Symbol
)