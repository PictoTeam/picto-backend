package pl.umcs.picto3.symbol

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.UUID


@Entity
@Table(name = "symbol_matrices")
data class SymbolMatrix(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "row_size")
    val rowSize: Short = 3,

    @Column(name = "column_size")
    val columnSize: Short = 3,

    @OneToMany
    @JoinColumn(name = "symbol_matrix_id")
    val symbolPlacements: List<SymbolPlacement>
)