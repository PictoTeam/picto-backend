package pl.umcs.picto3.symbol

import org.springframework.stereotype.Service

@Service
class SymbolService(
    private val symbolMatrixRepository: SymbolMatrixRepository,
    private val symbolPlacementRepository: SymbolPlacementRepository,
    private val symbolMapper: SymbolMapper
) {
    fun toSymbolMatrix(symbolMatrixConfigDto: SymbolMatrixConfigDto): SymbolMatrix {
        val symbolPlacements = symbolMatrixConfigDto.symbolPlacements
            .map { placementDto: SymbolPlacementConfigDto ->
                val existingPlacement = symbolPlacementRepository.findByRowIndexAndColumnIndexAndSymbolId(
                    placementDto.rowIndex,
                    placementDto.columnIndex,
                    placementDto.symbolId
                )

                existingPlacement ?: symbolPlacementRepository.save(symbolMapper.toSymbolPlacement(placementDto))
            }.toSet()

        val existingMatrices = symbolMatrixRepository.findByRowSizeAndColumnSize(
            symbolMatrixConfigDto.rowSize,
            symbolMatrixConfigDto.columnSize
        )

        val existingMatrix = existingMatrices.find { matrix ->
            if (matrix.symbolPlacements.size != symbolPlacements.size) {
                return@find false
            }

            matrix.symbolPlacements.all { matrixPlacement ->
                symbolPlacements.any { placement ->
                    placement.rowIndex == matrixPlacement.rowIndex &&
                            placement.columnIndex == matrixPlacement.columnIndex &&
                            placement.symbol.id == matrixPlacement.symbol.id
                }
            }
        }

        return existingMatrix ?: symbolMatrixRepository.save(
            SymbolMatrix(
                id = null,
                rowSize = symbolMatrixConfigDto.rowSize,
                columnSize = symbolMatrixConfigDto.columnSize,
                symbolPlacements = symbolPlacements
            )
        )
    }
}
