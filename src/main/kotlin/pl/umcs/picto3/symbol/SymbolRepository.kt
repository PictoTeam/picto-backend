package pl.umcs.picto3.symbol

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SymbolRepository: JpaRepository<Symbol, UUID> {
    fun findByFileHash(hash: String): Symbol?
    fun findByStoredFileName(filename: String): Symbol?
}
