package pl.umcs.picto3.storage

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
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