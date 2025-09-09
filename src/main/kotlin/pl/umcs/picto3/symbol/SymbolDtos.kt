package pl.umcs.picto3.symbol

import java.util.UUID


data class SymbolMatrixConfigDto(
    val rowSize: Short,
    val columnSize: Short,
    val symbolPlacements: Set<SymbolPlacementConfigDto>
)

data class SymbolPlacementConfigDto(
    val rowIndex: Short,
    val columnIndex: Short,
    val symbolId: UUID
)

data class SymbolPlacementDto(
    val rowIndex: Short,
    val columnIndex: Short,
    val symbolPath: String,
)

data class SymbolMatrixDto(
    val rowSize: Short,
    val columnSize: Short,
    val symbols: List<SymbolPlacementDto>
)

data class SymbolDto(
    val id: UUID?,
    val symbolPath: String
)
