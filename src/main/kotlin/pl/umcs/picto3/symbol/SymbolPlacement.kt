package pl.umcs.picto3.symbol

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID


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