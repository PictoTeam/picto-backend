package pl.umcs.picto3.gameconfig

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import pl.umcs.picto3.symbol.SymbolMatrixConfigDto
import java.util.UUID

@WebMvcTest(GameConfigController::class)
class GameConfigControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var gameConfigService: GameConfigService
    
    companion object {
        private val testGameConfigId = UUID.fromString("11111111-1111-1111-1111-111111111111")
        private val testImageId1 = UUID.fromString("44444444-4444-4444-4444-444444444444")
        private val testImageId2 = UUID.fromString("55555555-5555-5555-5555-555555555555")
    }

    @Test
    fun `test create game config`() {
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
        
        `when`(gameConfigService.createGame(any())).thenReturn(gameConfigDto)
        
        mockMvc.perform(post("/game-configs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(gameConfigDto)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.speakerImageCount").value(6))
            .andExpect(jsonPath("$.listenerImageCount").value(8))
            .andExpect(jsonPath("$.speakerAnswerTime").value(10000))
            .andExpect(jsonPath("$.listenerAnswerTime").value(15000))
            .andExpect(jsonPath("$.correctAnswerPoints").value(2))
            .andExpect(jsonPath("$.wrongAnswerPoints").value(-2))
            .andExpect(jsonPath("$.resultScreenTime").value(5000))
        
        verify(gameConfigService).createGame(any())
    }
    
    @Test
    fun `test get all game configs`() {
        val symbolMatrixConfigDto = SymbolMatrixConfigDto(
            rowSize = 3.toShort(),
            columnSize = 3.toShort(),
            symbolPlacements = emptySet()
        )
        
        val gameConfigDto1 = GameConfigDto(
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
        
        val gameConfigDto2 = GameConfigDto(
            symbols = symbolMatrixConfigDto,
            imagesId = setOf(testImageId1, testImageId2),
            speakerImageCount = 4,
            listenerImageCount = 4,
            speakerAnswerTime = 5000,
            listenerAnswerTime = 5000,
            correctAnswerPoints = 1,
            wrongAnswerPoints = -1,
            resultScreenTime = 3000
        )
        
        `when`(gameConfigService.getGameConfigs()).thenReturn(listOf(gameConfigDto1, gameConfigDto2))
        
        mockMvc.perform(get("/game-configs"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].speakerImageCount").value(6))
            .andExpect(jsonPath("$[0].listenerImageCount").value(8))
            .andExpect(jsonPath("$[1].speakerImageCount").value(4))
            .andExpect(jsonPath("$[1].listenerImageCount").value(4))
        
        verify(gameConfigService).getGameConfigs()
    }
    
    @Test
    fun `test get game config by id`() {
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
        
        `when`(gameConfigService.getGameConfig(testGameConfigId)).thenReturn(gameConfigDto)
        
        mockMvc.perform(get("/game-configs/$testGameConfigId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.speakerImageCount").value(6))
            .andExpect(jsonPath("$.listenerImageCount").value(8))
            .andExpect(jsonPath("$.speakerAnswerTime").value(10000))
            .andExpect(jsonPath("$.listenerAnswerTime").value(15000))
            .andExpect(jsonPath("$.correctAnswerPoints").value(2))
            .andExpect(jsonPath("$.wrongAnswerPoints").value(-2))
            .andExpect(jsonPath("$.resultScreenTime").value(5000))
        
        verify(gameConfigService).getGameConfig(testGameConfigId)
    }
    
    @Test
    fun `test update game config`() {
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
        
        `when`(gameConfigService.updateGameConfig(eq(testGameConfigId), any())).thenReturn(gameConfigDto)
        
        mockMvc.perform(post("/game-configs/$testGameConfigId")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(gameConfigDto)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.speakerImageCount").value(6))
            .andExpect(jsonPath("$.listenerImageCount").value(8))
            .andExpect(jsonPath("$.speakerAnswerTime").value(10000))
            .andExpect(jsonPath("$.listenerAnswerTime").value(15000))
            .andExpect(jsonPath("$.correctAnswerPoints").value(2))
            .andExpect(jsonPath("$.wrongAnswerPoints").value(-2))
            .andExpect(jsonPath("$.resultScreenTime").value(5000))
        
        verify(gameConfigService).updateGameConfig(eq(testGameConfigId), any())
    }
    
    @Test
    fun `test delete game config`() {
        mockMvc.perform(delete("/game-configs/$testGameConfigId"))
            .andExpect(status().isNoContent)
        
        verify(gameConfigService).deleteGameConfig(testGameConfigId)
    }
}
