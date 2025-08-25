package pl.umcs.picto3.symbol

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SymbolPlacementRepository : JpaRepository<SymbolPlacement, UUID> {
    fun findByRowIndexAndColumnIndexAndSymbolId(rowIndex: Short, columnIndex: Short, symbolId: UUID): SymbolPlacement?
}