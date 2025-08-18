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

    @Test
    fun `test GameConfigDto creation and validation`() {
        val symbolMatrixConfigDto = SymbolMatrixConfigDto(
            rowSize = 3.toShort(),
            columnSize = 3.toShort(),
            symbolPlacements = emptySet()
        )
        
        val gameConfigDto = GameConfigDto(
            symbols = symbolMatrixConfigDto,
            imagesId = setOf(1L, 2L),
            speakerImageCount = 6,
            listenerImageCount = 8,
            speakerAnswerTime = 10000,
            listenerAnswerTime = 15000,
            correctAnswerPoints = 2,
            wrongAnswerPoints = -2,
            resultScreenTime = 5000
        )
        
        assertEquals(symbolMatrixConfigDto, gameConfigDto.symbols)
        assertEquals(setOf(1L, 2L), gameConfigDto.imagesId)
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
            imagesId = setOf(1L, 2L),
            speakerImageCount = 6,
            listenerImageCount = 8,
            speakerAnswerTime = 10000,
            listenerAnswerTime = 15000,
            correctAnswerPoints = 2,
            wrongAnswerPoints = -2,
            resultScreenTime = 5000
        )
        
        val symbolMatrix = SymbolMatrix(
            id = 1L,
            rowSize = 3.toShort(),
            columnSize = 3.toShort(),
            symbolPlacements = emptySet()
        )
        
        val images = setOf(
            Image(id = 1L, storedFileName = "test1.jpg", fileName = "Test Image 1", fileHash = "hash123"),
            Image(id = 2L, storedFileName = "test2.jpg", fileName = "Test Image 2", fileHash = "hash456")
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
            id = 1L,
            rowSize = 3.toShort(),
            columnSize = 3.toShort(),
            symbolPlacements = emptySet()
        )
        
        val images = setOf(
            Image(id = 1L, storedFileName = "test1.jpg", fileName = "Test Image 1", fileHash = "hash123"),
            Image(id = 2L, storedFileName = "test2.jpg", fileName = "Test Image 2", fileHash = "hash456")
        )
        
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
        assertEquals(setOf(1L, 2L), gameConfigDto.imagesId)
        assertEquals(gameConfig.speakerImageCount, gameConfigDto.speakerImageCount)
        assertEquals(gameConfig.listenerImageCount, gameConfigDto.listenerImageCount)
        assertEquals(gameConfig.speakerAnswerTime, gameConfigDto.speakerAnswerTime)
        assertEquals(gameConfig.listenerAnswerTime, gameConfigDto.listenerAnswerTime)
        assertEquals(gameConfig.correctAnswerPoints, gameConfigDto.correctAnswerPoints)
        assertEquals(gameConfig.wrongAnswerPoints, gameConfigDto.wrongAnswerPoints)
        assertEquals(gameConfig.resultScreenTime, gameConfigDto.resultScreenTime)
    }
}