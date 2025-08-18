package pl.umcs.picto3.gameconfig

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import pl.umcs.picto3.image.Image
import pl.umcs.picto3.symbol.SymbolMatrix
import java.time.LocalDateTime

class GameConfigTest {

    @Test
    fun `test GameConfig creation with default values`() {
        val symbolMatrix = SymbolMatrix(id = 1L, rowSize = 3, columnSize = 3, symbolPlacements = emptySet())
        val images = setOf(Image(id = 1L, storedFileName = "test.jpg", fileName = "Test Image", fileHash = "hash123"))
        
        val gameConfig = GameConfig(
            id = null,
            symbols = symbolMatrix,
            images = images,
            createdAt = null
        )
        
        assertNull(gameConfig.id)
        assertEquals(symbolMatrix, gameConfig.symbols)
        assertEquals(images, gameConfig.images)
        assertEquals(4.toShort(), gameConfig.speakerImageCount)
        assertEquals(4.toShort(), gameConfig.listenerImageCount)
        assertEquals(-1, gameConfig.speakerAnswerTime)
        assertEquals(-1, gameConfig.listenerAnswerTime)
        assertEquals(1.toShort(), gameConfig.correctAnswerPoints)
        assertEquals((-1).toShort(), gameConfig.wrongAnswerPoints)
        assertEquals(3000, gameConfig.resultScreenTime)
        assertNull(gameConfig.createdAt)
    }
    
    @Test
    fun `test GameConfig creation with custom values`() {
        val symbolMatrix = SymbolMatrix(id = 1L, rowSize = 3, columnSize = 3, symbolPlacements = emptySet())
        val images = setOf(Image(id = 1L, storedFileName = "test.jpg", fileName = "Test Image", fileHash = "hash123"))
        val createdAt = LocalDateTime.now()
        
        val gameConfig = GameConfig(
            id = 1L,
            symbols = symbolMatrix,
            images = images,
            speakerImageCount = 6,
            listenerImageCount = 8,
            speakerAnswerTime = 10000,
            listenerAnswerTime = 15000,
            correctAnswerPoints = 2,
            wrongAnswerPoints = -2,
            resultScreenTime = 5000,
            createdAt = createdAt
        )
        
        assertEquals(1L, gameConfig.id)
        assertEquals(symbolMatrix, gameConfig.symbols)
        assertEquals(images, gameConfig.images)
        assertEquals(6.toShort(), gameConfig.speakerImageCount)
        assertEquals(8.toShort(), gameConfig.listenerImageCount)
        assertEquals(10000, gameConfig.speakerAnswerTime)
        assertEquals(15000, gameConfig.listenerAnswerTime)
        assertEquals(2.toShort(), gameConfig.correctAnswerPoints)
        assertEquals((-2).toShort(), gameConfig.wrongAnswerPoints)
        assertEquals(5000, gameConfig.resultScreenTime)
        assertEquals(createdAt, gameConfig.createdAt)
    }
    
    @Test
    fun `test GameConfig data class equality`() {
        val symbolMatrix = SymbolMatrix(id = 1L, rowSize = 3, columnSize = 3, symbolPlacements = emptySet())
        val images = setOf(Image(id = 1L, storedFileName = "test.jpg", fileName = "Test Image", fileHash = "hash123"))
        val createdAt = LocalDateTime.now()
        
        val gameConfig1 = GameConfig(
            id = 1L,
            symbols = symbolMatrix,
            images = images,
            speakerImageCount = 6,
            listenerImageCount = 8,
            speakerAnswerTime = 10000,
            listenerAnswerTime = 15000,
            correctAnswerPoints = 2,
            wrongAnswerPoints = -2,
            resultScreenTime = 5000,
            createdAt = createdAt
        )
        
        val gameConfig2 = GameConfig(
            id = 1L,
            symbols = symbolMatrix,
            images = images,
            speakerImageCount = 6,
            listenerImageCount = 8,
            speakerAnswerTime = 10000,
            listenerAnswerTime = 15000,
            correctAnswerPoints = 2,
            wrongAnswerPoints = -2,
            resultScreenTime = 5000,
            createdAt = createdAt
        )
        
        val gameConfig3 = GameConfig(
            id = 2L,
            symbols = symbolMatrix,
            images = images,
            speakerImageCount = 6,
            listenerImageCount = 8,
            speakerAnswerTime = 10000,
            listenerAnswerTime = 15000,
            correctAnswerPoints = 2,
            wrongAnswerPoints = -2,
            resultScreenTime = 5000,
            createdAt = createdAt
        )
        
        assertEquals(gameConfig1, gameConfig2)
        assertNotEquals(gameConfig1, gameConfig3)
    }
}