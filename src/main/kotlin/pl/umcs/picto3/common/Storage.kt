package pl.umcs.picto3.common

import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import pl.umcs.picto3.image.Image
import pl.umcs.picto3.image.ImageRepository
import pl.umcs.picto3.symbol.Symbol
import pl.umcs.picto3.symbol.SymbolRepository
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.MessageDigest
import java.util.*

@Component
class Storage {
    private val storagePath: Path = Paths.get("src/main/resources/static")

    private val allowedExtensions = setOf("jpg", "jpeg", "png")

    fun store(file: MultipartFile, folder: String = ""): String {
        try {
            if (file.isEmpty) {
                throw StorageException("Failed to store empty file.")
            }

            val originalFilename = file.originalFilename
                ?: throw StorageException("No filename provided")

            val extension = originalFilename.substringAfterLast('.', "").lowercase()
            if (extension !in allowedExtensions) {
                throw StorageException("File extension '$extension' not allowed")
            }

            val uuidFilename = "${UUID.randomUUID()}.$extension"
            val targetPath = if (folder.isNotEmpty()) storagePath.resolve(folder) else storagePath
            Files.createDirectories(targetPath)

            val destinationFile = targetPath.resolve(uuidFilename)

            file.inputStream.use { inputStream ->
                Files.copy(inputStream, destinationFile)
            }
            return if (folder.isNotEmpty()) "$folder/$uuidFilename" else uuidFilename

        } catch (e: IOException) {
            throw StorageException("Failed to store file: ${e.message}", e)
        }
    }
}

@Service
class StorageService(
    private val imageRepository: ImageRepository,
    private val symbolRepository: SymbolRepository,
    private val storage: Storage
) {

    @Transactional
    fun uploadBatchImages(files: List<MultipartFile>, names: List<String>) {
        if (files.size != names.size) {
            throw IllegalArgumentException("Files names' amount and files are different")
        }
        for (i in files.indices) {
            val file = files[i]
            val name = names[i]
            saveImage(file, name)
        }
    }

    fun saveImage(file: MultipartFile, fileName: String): Image {
        val fileToSaveHash = calculateFileHash(file)
        if (imageRepository.findByFileHash(fileToSaveHash) != null) {
            throw StorageException("file with same content already exists as $fileName")
        }
        if (imageRepository.findByStoredFileName(fileName) != null) {
            throw StorageException("file with given name: $fileName already exists")
        }
        val storedFileName = storage.store(file)
        val image = Image(null, storedFileName, fileName, fileToSaveHash)
        return imageRepository.save(image)
    }

    fun calculateFileHash(file: MultipartFile): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(file.bytes)
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            throw RuntimeException("Failed to calculate file hash", e)
        }
    }

    @Transactional
    fun uploadBatchSymbols(files: List<MultipartFile>, names: List<String>) {
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

}

class StorageException(message: String, cause: Throwable? = null) : RuntimeException(message, cause) {
    //TODO improve handling this Exception
}