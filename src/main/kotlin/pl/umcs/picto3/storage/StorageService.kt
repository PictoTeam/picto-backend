package pl.umcs.picto3.storage

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import pl.umcs.picto3.game.GameService
import pl.umcs.picto3.image.Image
import pl.umcs.picto3.image.ImageDto
import pl.umcs.picto3.image.ImageMapper
import pl.umcs.picto3.image.ImageRepository
import pl.umcs.picto3.symbol.Symbol
import pl.umcs.picto3.symbol.SymbolMapper
import pl.umcs.picto3.symbol.SymbolMatrixDto
import pl.umcs.picto3.symbol.SymbolRepository
import java.security.MessageDigest

@Service
class StorageService(
    private val imageRepository: ImageRepository,
    private val symbolRepository: SymbolRepository,
    private val gameService: GameService,
    private val storage: Storage,
    private val imageMapper: ImageMapper,
    private val symbolMapper: SymbolMapper
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

    fun getAllImages(): List<ImageDto> {
        return imageRepository.findAll().map { image -> imageMapper.toNotMainDto(image) }
    }

    fun getImagesForRoundWithSessionAccessCode(sessionAccessCode: String): List<ImageDto> {
        val imagesForRound =
            gameService.getRandomImages(sessionAccessCode).toList().map { image -> imageMapper.toNotMainDto(image) }
        val randomIndex = (0 until imagesForRound.size).random()
        return imagesForRound.mapIndexed { index, dto ->
            if (index == randomIndex) dto.copy(isTopic = true) else dto.copy(isTopic = false)
        }
    }

    fun getSymbolsForGameWithSessionAccessCode(sessionAccessCode: String): SymbolMatrixDto {
        val symbolsForGame = gameService.getSymbolsForGame(sessionAccessCode)
        return symbolMapper.toSymbolMatrixDto(symbolsForGame)
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