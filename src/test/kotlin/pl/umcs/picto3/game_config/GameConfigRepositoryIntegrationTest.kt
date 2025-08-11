package pl.umcs.picto3.game_config

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pl.umcs.picto3.image.Image
import pl.umcs.picto3.image.ImageRepository
import pl.umcs.picto3.symbol.SymbolMatrix
import pl.umcs.picto3.symbol.SymbolMatrixRepository
import pl.umcs.picto3.symbol.SymbolPlacement
import pl.umcs.picto3.symbol.SymbolPlacementRepository
import pl.umcs.picto3.symbol.Symbol
import pl.umcs.picto3.symbol.SymbolRepository

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GameConfigRepositoryIntegrationTest {

    @Autowired
    private lateinit var gameConfigRepository: GameConfigRepository

    @Autowired
    private lateinit var symbolMatrixRepository: SymbolMatrixRepository

    @Autowired
    private lateinit var symbolRepository: SymbolRepository

    @Autowired
    private lateinit var symbolPlacementRepository: SymbolPlacementRepository

    @Autowired
    private lateinit var imageRepository: ImageRepository

    @Test
    fun `test save and retrieve game config with relationships`() {
        val symbol = Symbol(
            id = null,
            filePath = "symbols/circle.png"
        )
        val savedSymbol = symbolRepository.save(symbol)

        val symbolPlacement = SymbolPlacement(
            id = null,
            rowIndex = 0,
            columnIndex = 0,
            symbol = savedSymbol
        )
        val savedSymbolPlacement = symbolPlacementRepository.save(symbolPlacement)

        val symbolMatrix = SymbolMatrix(
            id = null,
            rowSize = 3.toShort(),
            columnSize = 3.toShort(),
            symbolPlacements = setOf(savedSymbolPlacement)
        )
        val savedSymbolMatrix = symbolMatrixRepository.save(symbolMatrix)

        val image1 = Image(
            id = null,
            storedFileName = "test1.jpg",
            fileName = "Test Image 1",
            fileHash = "hash123"
        )
        val savedImage1 = imageRepository.save(image1)

        val image2 = Image(
            id = null,
            storedFileName = "test2.jpg",
            fileName = "Test Image 2",
            fileHash = "hash456"
        )
        val savedImage2 = imageRepository.save(image2)

        val gameConfig = GameConfig(
            id = null,
            symbols = savedSymbolMatrix,
            images = mutableSetOf(savedImage1, savedImage2),
            speakerImageCount = 6,
            listenerImageCount = 8,
            speakerAnswerTime = 10000,
            listenerAnswerTime = 15000,
            correctAnswerPoints = 2,
            wrongAnswerPoints = -2,
            resultScreenTime = 5000,
            createdAt = null
        )

        val savedGameConfig = gameConfigRepository.save(gameConfig)
        val retrievedGameConfig = gameConfigRepository.findById(savedGameConfig.id!!).orElse(null)

        assertNotNull(retrievedGameConfig)
        assertEquals(savedGameConfig.id, retrievedGameConfig.id)
        assertEquals(savedSymbolMatrix.id, retrievedGameConfig.symbols.id)
        assertEquals(2, retrievedGameConfig.images.size)
        assertTrue(retrievedGameConfig.images.any { it.id == savedImage1.id })
        assertTrue(retrievedGameConfig.images.any { it.id == savedImage2.id })
        assertEquals(6.toShort(), retrievedGameConfig.speakerImageCount)
        assertEquals(8.toShort(), retrievedGameConfig.listenerImageCount)
        assertEquals(10000, retrievedGameConfig.speakerAnswerTime)
        assertEquals(15000, retrievedGameConfig.listenerAnswerTime)
        assertEquals(2.toShort(), retrievedGameConfig.correctAnswerPoints)
        assertEquals((-2).toShort(), retrievedGameConfig.wrongAnswerPoints)
        assertEquals(5000, retrievedGameConfig.resultScreenTime)
        assertNotNull(retrievedGameConfig.createdAt)

        val retrievedSymbolMatrix = retrievedGameConfig.symbols
        assertEquals(3.toShort(), retrievedSymbolMatrix.rowSize)
        assertEquals(3.toShort(), retrievedSymbolMatrix.columnSize)
        assertEquals(1, retrievedSymbolMatrix.symbolPlacements.size)

        val retrievedSymbolPlacement = retrievedSymbolMatrix.symbolPlacements.first()
        assertEquals(0.toShort(), retrievedSymbolPlacement.rowIndex)
        assertEquals(0.toShort(), retrievedSymbolPlacement.columnIndex)
        assertEquals(savedSymbol.id, retrievedSymbolPlacement.symbol.id)
    }

    @Test
    fun `test update game config`() {
        val symbolMatrix = SymbolMatrix(
            id = null,
            rowSize = 3.toShort(),
            columnSize = 3.toShort(),
            symbolPlacements = emptySet()
        )
        val savedSymbolMatrix = symbolMatrixRepository.save(symbolMatrix)

        val image = Image(
            id = null,
            storedFileName = "test.jpg",
            fileName = "Test Image",
            fileHash = "hash123"
        )
        val savedImage = imageRepository.save(image)

        val gameConfig = GameConfig(
            id = null,
            symbols = savedSymbolMatrix,
            images = mutableSetOf(savedImage),
            speakerImageCount = 4,
            listenerImageCount = 4,
            speakerAnswerTime = 5000,
            listenerAnswerTime = 5000,
            correctAnswerPoints = 1,
            wrongAnswerPoints = -1,
            resultScreenTime = 3000,
            createdAt = null
        )
        val savedGameConfig = gameConfigRepository.save(gameConfig)

        val fetchedGameConfig = gameConfigRepository.findById(savedGameConfig.id!!).get()
        
        val updatedGameConfig = fetchedGameConfig.copy(
            speakerImageCount = 6,
            listenerImageCount = 8,
            speakerAnswerTime = 10000,
            listenerAnswerTime = 15000,
            correctAnswerPoints = 2,
            wrongAnswerPoints = -2,
            resultScreenTime = 5000
        )
        
        val savedUpdatedGameConfig = gameConfigRepository.save(updatedGameConfig)

        assertEquals(savedGameConfig.id, savedUpdatedGameConfig.id)
        assertEquals(6.toShort(), savedUpdatedGameConfig.speakerImageCount)
        assertEquals(8.toShort(), savedUpdatedGameConfig.listenerImageCount)
        assertEquals(10000, savedUpdatedGameConfig.speakerAnswerTime)
        assertEquals(15000, savedUpdatedGameConfig.listenerAnswerTime)
        assertEquals(2.toShort(), savedUpdatedGameConfig.correctAnswerPoints)
        assertEquals((-2).toShort(), savedUpdatedGameConfig.wrongAnswerPoints)
        assertEquals(5000, savedUpdatedGameConfig.resultScreenTime)
    }

    @Test
    fun `test delete game config`() {
        val symbolMatrix = SymbolMatrix(
            id = null,
            rowSize = 3.toShort(),
            columnSize = 3.toShort(),
            symbolPlacements = emptySet()
        )
        val savedSymbolMatrix = symbolMatrixRepository.save(symbolMatrix)

        val image = Image(
            id = null,
            storedFileName = "test.jpg",
            fileName = "Test Image",
            fileHash = "hash123"
        )
        val savedImage = imageRepository.save(image)

        val gameConfig = GameConfig(
            id = null,
            symbols = savedSymbolMatrix,
            images = mutableSetOf(savedImage),
            speakerImageCount = 4,
            listenerImageCount = 4,
            speakerAnswerTime = 5000,
            listenerAnswerTime = 5000,
            correctAnswerPoints = 1,
            wrongAnswerPoints = -1,
            resultScreenTime = 3000,
            createdAt = null
        )
        val savedGameConfig = gameConfigRepository.save(gameConfig)

        gameConfigRepository.deleteById(savedGameConfig.id!!)

        val deletedGameConfig = gameConfigRepository.findById(savedGameConfig.id!!).orElse(null)
        assertNull(deletedGameConfig)

        assertTrue(symbolMatrixRepository.existsById(savedSymbolMatrix.id!!))
        assertTrue(imageRepository.existsById(savedImage.id!!))
    }
}