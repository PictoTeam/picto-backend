package pl.umcs.picto3.symbol

import org.springframework.stereotype.Service

@Service
class SymbolService(
    private val symbolRepository: SymbolRepository,
    private val symbolMatrixRepository: SymbolMatrixRepository,
    private val symbolMapper: SymbolMapper
) {
    fun createSymbolMatrix(symbolMatrixConfigDto: SymbolMatrixConfigDto): SymbolMatrix {
        val symbolMatrix = symbolMapper.toSymbolMatrix(symbolMatrixConfigDto)
        return symbolMatrixRepository.save(symbolMatrix)
    }
}
