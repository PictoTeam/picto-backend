package pl.umcs.picto3.symbol

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.util.UUID


@Entity
@Table(name = "symbol_matrices")
data class SymbolMatrix(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "row_size")
    val rowSize: Short,

    @Column(name = "column_size")
    val columnSize: Short,

    @ManyToMany(fetch = FetchType.EAGER)
    val symbolPlacements: List<SymbolPlacement>
)