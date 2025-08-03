package pl.umcs.picto3.symbol

import org.springframework.stereotype.Component

data class SymbolMatrixConfigDto(
    val rowSize: Short,
    val columnSize: Short,
    val symbolPlacements: Set<SymbolPlacementConfigDto>
)

data class SymbolPlacementConfigDto(
    val rowIndex: Short,
    val columnIndex: Short,
    val symbolId: Long
)

@Component
class SymbolMapper(
    private val symbolMatrixRepository: SymbolMatrixRepository,
    private val symbolPlacementRepository: SymbolPlacementRepository,
    private val symbolRepository: SymbolRepository
) {
    fun toSymbolMatrix(dto: SymbolMatrixConfigDto): SymbolMatrix {
        val mappedSymbolPlacements = dto.symbolPlacements
            .map { placementDto -> toSymbolPlacement(placementDto) }.toSet()

        return SymbolMatrix(
            id = null,
            rowSize = dto.rowSize,
            columnSize = dto.columnSize,
            symbolPlacements = mappedSymbolPlacements
        )
    }

    fun toSymbolPlacement(dto: SymbolPlacementConfigDto): SymbolPlacement {
        val symbol = symbolRepository.getReferenceById(dto.symbolId)

        return SymbolPlacement(
            id = null,
            rowIndex = dto.rowIndex,
            columnIndex = dto.columnIndex,
            symbol = symbol
        )
    }

    fun toSymbolMatrixConfigDto(symbolMatrix: SymbolMatrix): SymbolMatrixConfigDto {
        val mappedSymbolPlacements = symbolMatrix.symbolPlacements
            .map { placement -> toSymbolPlacementConfigDto(placement) }.toSet()

        return SymbolMatrixConfigDto(
            rowSize = symbolMatrix.rowSize,
            columnSize = symbolMatrix.columnSize,
            symbolPlacements = mappedSymbolPlacements
        )
    }

    fun toSymbolPlacementConfigDto(symbolPlacement: SymbolPlacement): SymbolPlacementConfigDto {
        return SymbolPlacementConfigDto(
            rowIndex = symbolPlacement.rowIndex,
            columnIndex = symbolPlacement.columnIndex,
            symbolId = symbolPlacement.symbol.id!!
        )
    }
}
