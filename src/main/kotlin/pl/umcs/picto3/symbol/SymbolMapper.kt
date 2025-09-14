package pl.umcs.picto3.symbol

import org.springframework.stereotype.Component

@Component
class SymbolMapper(
    private val symbolRepository: SymbolRepository
) {

    fun toSymbolMatrixDto(entity: SymbolMatrix): SymbolMatrixDto {
        return SymbolMatrixDto(
            entity.rowSize,
            entity.columnSize,
            entity.symbolPlacements.map { toSymbolPlacementDto(it) }
        )
    }

    fun toSymbolPlacementDto(entity: SymbolPlacement): SymbolPlacementDto {
        return SymbolPlacementDto(
            entity.rowIndex,
            entity.columnIndex,
            "/static/symbols/" + entity.symbol.storedFileName,
            symbolId = entity.symbol.id!!
        )
    }


    fun toSymbolMatrix(dto: SymbolMatrixConfigDto): SymbolMatrix {
        val mappedSymbolPlacements = dto.symbolPlacements
            .map { placementDto -> toSymbolPlacement(placementDto) }.toList()

        return SymbolMatrix(
            id = null,
            rowSize = dto.rowSize,
            columnSize = dto.columnSize,
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

    fun toSymbolMatrixConfigDto(symbolMatrix: SymbolMatrix): SymbolMatrixConfigDto {
        val mappedSymbolPlacements = symbolMatrix.symbolPlacements
            .map { placement -> toSymbolPlacementConfigDto(placement) }.toSet()

        return SymbolMatrixConfigDto(
            rowSize = symbolMatrix.rowSize,
            columnSize = symbolMatrix.columnSize,
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

    fun toDto(symbol: Symbol): SymbolDto {
        return SymbolDto(
            id = symbol.id!!,
            symbolPath = "/static/symbols/" + symbol.storedFileName
        )
    }
}