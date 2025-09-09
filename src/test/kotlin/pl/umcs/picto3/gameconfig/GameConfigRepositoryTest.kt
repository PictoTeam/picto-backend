package pl.umcs.picto3.gameconfig

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import pl.umcs.picto3.image.Image
import pl.umcs.picto3.symbol.SymbolMatrix

@DataJpaTest
@ActiveProfiles("test")
class GameConfigRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var gameConfigRepository: GameConfigRepository

    @Test
    fun `test save and find game config`() {
        val symbolMatrix = SymbolMatrix(
            id = null,
            rowSize = 3.toShort(),
            columnSize = 3.toShort(),
            symbolPlacements = emptyList()
        )
        entityManager.persist(symbolMatrix)
        
        val image1 = Image(
            id = null,
            storedFileName = "test1.jpg",
            fileName = "Test Image 1",
            fileHash = "hash123"
        )
        entityManager.persist(image1)
        
        val image2 = Image(
            id = null,
            storedFileName = "test2.jpg",
            fileName = "Test Image 2",
            fileHash = "hash456"
        )
        entityManager.persist(image2)
        
        val gameConfig = GameConfig(
            id = null,
            symbols = symbolMatrix,
            images = setOf(image1, image2),
            speakerImageCount = 6,
            listenerImageCount = 8,
            speakerAnswerTime = 10000,
            listenerAnswerTime = 15000,
            correctAnswerPoints = 2,
            wrongAnswerPoints = -2,
            resultScreenTime = 5000
        )
        
        val savedGameConfig = gameConfigRepository.save(gameConfig)
        entityManager.flush()
        entityManager.clear()
        
        val foundGameConfig = gameConfigRepository.findById(savedGameConfig.id!!).orElse(null)
        
        assertNotNull(foundGameConfig)
        assertEquals(savedGameConfig.id, foundGameConfig.id)
        assertEquals(symbolMatrix.id, foundGameConfig.symbols.id)
        assertEquals(2, foundGameConfig.images.size)
        assertTrue(foundGameConfig.images.any { it.id == image1.id })
        assertTrue(foundGameConfig.images.any { it.id == image2.id })
        assertEquals(6.toShort(), foundGameConfig.speakerImageCount)
        assertEquals(8.toShort(), foundGameConfig.listenerImageCount)
        assertEquals(10000, foundGameConfig.speakerAnswerTime)
        assertEquals(15000, foundGameConfig.listenerAnswerTime)
        assertEquals(2.toShort(), foundGameConfig.correctAnswerPoints)
        assertEquals((-2).toShort(), foundGameConfig.wrongAnswerPoints)
        assertEquals(5000, foundGameConfig.resultScreenTime)
        assertNotNull(foundGameConfig.createdAt)
    }
    
    @Test
    fun `test find all game configs`() {
        val symbolMatrix1 = SymbolMatrix(
            id = null,
            rowSize = 3.toShort(),
            columnSize = 3.toShort(),
            symbolPlacements = emptyList()
        )
        entityManager.persist(symbolMatrix1)
        
        val symbolMatrix2 = SymbolMatrix(
            id = null,
            rowSize = 4.toShort(),
            columnSize = 4.toShort(),
            symbolPlacements = emptyList()
        )
        entityManager.persist(symbolMatrix2)
        
        val image = Image(
            id = null,
            storedFileName = "test.jpg",
            fileName = "Test Image",
            fileHash = "hash123"
        )
        entityManager.persist(image)
        
        val gameConfig1 = GameConfig(
            id = null,
            symbols = symbolMatrix1,
            images = setOf(image),
            speakerImageCount = 6,
            listenerImageCount = 8,
            speakerAnswerTime = 10000,
            listenerAnswerTime = 15000,
            correctAnswerPoints = 2,
            wrongAnswerPoints = -2,
            resultScreenTime = 5000
        )
        
        val gameConfig2 = GameConfig(
            id = null,
            symbols = symbolMatrix2,
            images = setOf(image),
            speakerImageCount = 4,
            listenerImageCount = 4,
            speakerAnswerTime = 5000,
            listenerAnswerTime = 5000,
            correctAnswerPoints = 1,
            wrongAnswerPoints = -1,
            resultScreenTime = 3000
        )
        
        gameConfigRepository.save(gameConfig1)
        gameConfigRepository.save(gameConfig2)
        entityManager.flush()
        entityManager.clear()
        
        val allGameConfigs = gameConfigRepository.findAll()
        
        assertEquals(2, allGameConfigs.size)
    }
    
    @Test
    fun `test delete game config`() {
        val symbolMatrix = SymbolMatrix(
            id = null,
            rowSize = 3.toShort(),
            columnSize = 3.toShort(),
            symbolPlacements = emptyList()
        )
        entityManager.persist(symbolMatrix)
        
        val image = Image(
            id = null,
            storedFileName = "test.jpg",
            fileName = "Test Image",
            fileHash = "hash123"
        )
        entityManager.persist(image)
        
        val gameConfig = GameConfig(
            id = null,
            symbols = symbolMatrix,
            images = setOf(image),
            speakerImageCount = 6,
            listenerImageCount = 8,
            speakerAnswerTime = 10000,
            listenerAnswerTime = 15000,
            correctAnswerPoints = 2,
            wrongAnswerPoints = -2,
            resultScreenTime = 5000
        )
        
        val savedGameConfig = gameConfigRepository.save(gameConfig)
        entityManager.flush()
        
        gameConfigRepository.deleteById(savedGameConfig.id!!)
        entityManager.flush()
        entityManager.clear()
        
        val foundGameConfig = gameConfigRepository.findById(savedGameConfig.id!!).orElse(null)
        
        assertNull(foundGameConfig)
    }
}