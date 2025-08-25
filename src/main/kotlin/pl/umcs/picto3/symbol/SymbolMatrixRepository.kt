package pl.umcs.picto3.symbol

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SymbolMatrixRepository : JpaRepository<SymbolMatrix, UUID> {
    fun findByRowSizeAndColumnSize(rowSize: Short, columnSize: Short): List<SymbolMatrix>
}