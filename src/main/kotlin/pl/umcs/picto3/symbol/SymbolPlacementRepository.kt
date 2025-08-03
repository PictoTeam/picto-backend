package pl.umcs.picto3.symbol

import org.springframework.data.jpa.repository.JpaRepository

interface SymbolPlacementRepository : JpaRepository<SymbolPlacement, Long> {
}