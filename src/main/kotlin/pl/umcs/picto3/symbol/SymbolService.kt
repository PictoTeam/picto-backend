package pl.umcs.picto3.symbol

class SymbolService(
    private val symbolRepository: SymbolRepository,
    private val symbolMapper: SymbolMapper
) {
    fun createSymbolMatrix(symbolMatrixConfigDto: SymbolMatrixConfigDto): SymbolMatrix {
        val symbolMatrix = symbolMapper.toSymbolMatrix(symbolMatrixConfigDto)
        return symbolRepository.save(symbolMatrix)
    }
}