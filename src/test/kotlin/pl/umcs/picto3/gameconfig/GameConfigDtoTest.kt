package pl.umcs.picto3.gameconfig

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import pl.umcs.picto3.image.Image
import pl.umcs.picto3.image.ImageRepository
import pl.umcs.picto3.symbol.SymbolMapper
import pl.umcs.picto3.symbol.SymbolMatrix
import pl.umcs.picto3.symbol.SymbolMatrixConfigDto
import pl.umcs.picto3.symbol.SymbolService
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class GameConfigDtoTest {

    @Mock
    private lateinit var imageRepository: ImageRepository

    @Mock
    private lateinit var symbolMapper: SymbolMapper
    
    @Mock
    private lateinit var symbolService: SymbolService

    @InjectMocks
    private lateinit var gameConfigMapper: GameConfigMapper
    
    companion object {
        private val testGameConfigId = UUID.fromString("11111111-1111-1111-1111-111111111111")
        private val testSymbolMatrixId = UUID.fromString("22222222-2222-2222-2222-222222222222")
        private val testImageId1 = UUID.fromString("44444444-4444-4444-4444-444444444444")
        private val testImageId2 = UUID.fromString("55555555-5555-5555-5555-555555555555")
    }

    @Test
    fun `test GameConfigDto creation and validation`() {
        val symbolMatrixConfigDto = SymbolMatrixConfigDto(
            rowSize = 3.toShort(),
            columnSize = 3.toShort(),
            symbolPlacements = emptySet()
        )
        
        val gameConfigDto = GameConfigDto(
            symbols = symbolMatrixConfigDto,
            imagesId = setOf(testImageId1, testImageId2),
            speakerImageCount = 6,
            listenerImageCount = 8,
            speakerAnswerTime = 10000,
            listenerAnswerTime = 15000,
            correctAnswerPoints = 2,
            wrongAnswerPoints = -2,
            resultScreenTime = 5000
        )
        
        assertEquals(symbolMatrixConfigDto, gameConfigDto.symbols)
        assertEquals(setOf(testImageId1, testImageId2), gameConfigDto.imagesId)
        assertEquals(6.toShort(), gameConfigDto.speakerImageCount)
        assertEquals(8.toShort(), gameConfigDto.listenerImageCount)
        assertEquals(10000, gameConfigDto.speakerAnswerTime)
        assertEquals(15000, gameConfigDto.listenerAnswerTime)
        assertEquals(2.toShort(), gameConfigDto.correctAnswerPoints)
        assertEquals((-2).toShort(), gameConfigDto.wrongAnswerPoints)
        assertEquals(5000, gameConfigDto.resultScreenTime)
    }
    
    @Test
    fun `test fromDto conversion`() {
        val symbolMatrixConfigDto = SymbolMatrixConfigDto(
            rowSize = 3.toShort(),
            columnSize = 3.toShort(),
            symbolPlacements = emptySet()
        )
        
        val gameConfigDto = GameConfigDto(
            symbols = symbolMatrixConfigDto,
            imagesId = setOf(testImageId1, testImageId2),
            speakerImageCount = 6,
            listenerImageCount = 8,
            speakerAnswerTime = 10000,
            listenerAnswerTime = 15000,
            correctAnswerPoints = 2,
            wrongAnswerPoints = -2,
            resultScreenTime = 5000
        )
        
        val symbolMatrix = SymbolMatrix(
            id = testSymbolMatrixId,
            rowSize = 3.toShort(),
            columnSize = 3.toShort(),
            symbolPlacements = emptyList()
        )
        
        val images = setOf(
            Image(id = testImageId1, storedFileName = "test1.jpg", fileName = "Test Image 1", fileHash = "hash123"),
            Image(id = testImageId2, storedFileName = "test2.jpg", fileName = "Test Image 2", fileHash = "hash456")
        )
        
        `when`(symbolService.toSymbolMatrix(symbolMatrixConfigDto)).thenReturn(symbolMatrix)
        `when`(imageRepository.findAllById(gameConfigDto.imagesId)).thenReturn(images.toList())
        
        val gameConfig = gameConfigMapper.toGameConfig(gameConfigDto)
        
        assertNull(gameConfig.id)
        assertEquals(symbolMatrix, gameConfig.symbols)
        assertEquals(images, gameConfig.images)
        assertEquals(gameConfigDto.speakerImageCount, gameConfig.speakerImageCount)
        assertEquals(gameConfigDto.listenerImageCount, gameConfig.listenerImageCount)
        assertEquals(gameConfigDto.speakerAnswerTime, gameConfig.speakerAnswerTime)
        assertEquals(gameConfigDto.listenerAnswerTime, gameConfig.listenerAnswerTime)
        assertEquals(gameConfigDto.correctAnswerPoints, gameConfig.correctAnswerPoints)
        assertEquals(gameConfigDto.wrongAnswerPoints, gameConfig.wrongAnswerPoints)
        assertEquals(gameConfigDto.resultScreenTime, gameConfig.resultScreenTime)
        assertNull(gameConfig.createdAt)
    }
    
    @Test
    fun `test toDto conversion`() {
        val symbolMatrix = SymbolMatrix(
            id = testSymbolMatrixId,
            rowSize = 3.toShort(),
            columnSize = 3.toShort(),
            symbolPlacements = emptyList()
        )
        
        val images = setOf(
            Image(id = testImageId1, storedFileName = "test1.jpg", fileName = "Test Image 1", fileHash = "hash123"),
            Image(id = testImageId2, storedFileName = "test2.jpg", fileName = "Test Image 2", fileHash = "hash456")
        )
        
        val gameConfig = GameConfig(
            id = testGameConfigId,
            symbols = symbolMatrix,
            images = images,
            speakerImageCount = 6,
            listenerImageCount = 8,
            speakerAnswerTime = 10000,
            listenerAnswerTime = 15000,
            correctAnswerPoints = 2,
            wrongAnswerPoints = -2,
            resultScreenTime = 5000,
            createdAt = null
        )
        
        val symbolMatrixConfigDto = SymbolMatrixConfigDto(
            rowSize = 3.toShort(),
            columnSize = 3.toShort(),
            symbolPlacements = emptySet()
        )
        
        `when`(symbolMapper.toSymbolMatrixConfigDto(symbolMatrix)).thenReturn(symbolMatrixConfigDto)
        
        val gameConfigDto = gameConfigMapper.toDto(gameConfig)
        
        assertEquals(symbolMatrixConfigDto, gameConfigDto.symbols)
        assertEquals(setOf(testImageId1, testImageId2), gameConfigDto.imagesId)
        assertEquals(gameConfig.speakerImageCount, gameConfigDto.speakerImageCount)
        assertEquals(gameConfig.listenerImageCount, gameConfigDto.listenerImageCount)
        assertEquals(gameConfig.speakerAnswerTime, gameConfigDto.speakerAnswerTime)
        assertEquals(gameConfig.listenerAnswerTime, gameConfigDto.listenerAnswerTime)
        assertEquals(gameConfig.correctAnswerPoints, gameConfigDto.correctAnswerPoints)
        assertEquals(gameConfig.wrongAnswerPoints, gameConfigDto.wrongAnswerPoints)
        assertEquals(gameConfig.resultScreenTime, gameConfigDto.resultScreenTime)
    }
}