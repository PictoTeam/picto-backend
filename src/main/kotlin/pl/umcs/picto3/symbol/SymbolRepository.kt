package pl.umcs.picto3.symbol

import org.springframework.data.jpa.repository.JpaRepository

interface SymbolRepository: JpaRepository<Symbol, Long> {
    fun findByFileHash(hash: String): Symbol?
    fun findByStoredFileName(filename: String): Symbol?
}
