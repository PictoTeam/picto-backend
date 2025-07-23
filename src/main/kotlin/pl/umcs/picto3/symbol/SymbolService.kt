package pl.umcs.picto3.symbol

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import pl.umcs.picto3.common.Storage
import pl.umcs.picto3.common.StorageException
import java.security.MessageDigest

@Service
class SymbolService(
    private val symbolRepository: SymbolRepository,
    private val storage: Storage
) {

    @Transactional
    fun uploadBatch(files: List<MultipartFile>, names: List<String>) {
        if (files.size != names.size) {
            throw IllegalArgumentException("Files names' amount and files are different")
        }
        for (i in files.indices) {
            val file = files[i]
            val name = names[i]
            saveSymbol(file, name)
        }
    }

    fun saveSymbol(file: MultipartFile, fileName: String): Symbol {
        val fileToSaveHash = calculateFileHash(file)
        if (symbolRepository.findByFileHash(fileToSaveHash) != null) {
            throw StorageException("file with same content already exists as $fileName")
        }
        if (symbolRepository.findByStoredFileName(fileName) != null) {
            throw StorageException("file with given name: $fileName already exists")
        }
        val storedFileName = storage.store(file, "symbols")
        val symbol = Symbol(null, storedFileName, fileName, fileToSaveHash)
        return symbolRepository.save(symbol)
    }

    private fun calculateFileHash(file: MultipartFile): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(file.bytes)
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            throw RuntimeException("Failed to calculate file hash", e)
        }
    }
}