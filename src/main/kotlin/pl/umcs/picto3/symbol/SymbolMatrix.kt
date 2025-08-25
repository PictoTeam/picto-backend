package pl.umcs.picto3.symbol

import jakarta.persistence.*
import java.util.*

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
    val symbolPlacements: Set<SymbolPlacement>
)