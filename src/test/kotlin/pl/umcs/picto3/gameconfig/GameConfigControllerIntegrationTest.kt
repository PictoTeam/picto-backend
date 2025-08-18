package pl.umcs.picto3.gameconfig

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import pl.umcs.picto3.image.Image
import pl.umcs.picto3.image.ImageRepository
import pl.umcs.picto3.symbol.Symbol
import pl.umcs.picto3.symbol.SymbolMapper
import pl.umcs.picto3.symbol.SymbolMatrix
import pl.umcs.picto3.symbol.SymbolMatrixConfigDto
import pl.umcs.picto3.symbol.SymbolMatrixRepository
import pl.umcs.picto3.symbol.SymbolPlacementConfigDto
import pl.umcs.picto3.symbol.SymbolPlacementRepository
import pl.umcs.picto3.symbol.SymbolRepository

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class GameConfigControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

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

    @Autowired
    private lateinit var symbolMapper: SymbolMapper

    @Autowired
    private lateinit var gameConfigMapper: GameConfigMapper

    @Test
    fun `test get all game configs`() {
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
        gameConfigRepository.save(gameConfig)

        mockMvc.perform(get("/game-configs"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].speakerImageCount").value(4))
            .andExpect(jsonPath("$[0].listenerImageCount").value(4))
            .andExpect(jsonPath("$[0].speakerAnswerTime").value(5000))
            .andExpect(jsonPath("$[0].listenerAnswerTime").value(5000))
            .andExpect(jsonPath("$[0].correctAnswerPoints").value(1))
            .andExpect(jsonPath("$[0].wrongAnswerPoints").value(-1))
            .andExpect(jsonPath("$[0].resultScreenTime").value(3000))
    }

    @Test
    fun `test get game config by id`() {
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

        mockMvc.perform(get("/game-configs/${savedGameConfig.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.speakerImageCount").value(4))
            .andExpect(jsonPath("$.listenerImageCount").value(4))
            .andExpect(jsonPath("$.speakerAnswerTime").value(5000))
            .andExpect(jsonPath("$.listenerAnswerTime").value(5000))
            .andExpect(jsonPath("$.correctAnswerPoints").value(1))
            .andExpect(jsonPath("$.wrongAnswerPoints").value(-1))
            .andExpect(jsonPath("$.resultScreenTime").value(3000))
    }

    @Test
    fun `test create game config`() {
        val symbol = Symbol(
            id = null,
            filePath = "symbols/circle.png"
        )
        val savedSymbol = symbolRepository.save(symbol)

        val image = Image(
            id = null,
            storedFileName = "test.jpg",
            fileName = "Test Image",
            fileHash = "hash123"
        )
        val savedImage = imageRepository.save(image)

        val symbolPlacementConfigDto = SymbolPlacementConfigDto(
            rowIndex = 0.toShort(),
            columnIndex = 0.toShort(),
            symbolId = savedSymbol.id!!
        )

        val symbolMatrixConfigDto = SymbolMatrixConfigDto(
            rowSize = 3.toShort(),
            columnSize = 3.toShort(),
            symbolPlacements = mutableSetOf(symbolPlacementConfigDto)
        )

        val gameConfigDto = GameConfigDto(
            symbols = symbolMatrixConfigDto,
            imagesId = mutableSetOf(savedImage.id!!),
            speakerImageCount = 6,
            listenerImageCount = 8,
            speakerAnswerTime = 10000,
            listenerAnswerTime = 15000,
            correctAnswerPoints = 2,
            wrongAnswerPoints = -2,
            resultScreenTime = 5000
        )

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

        val symbolMatrixConfigDto = symbolMapper.toSymbolMatrixConfigDto(savedSymbolMatrix)
        val gameConfigDto = GameConfigDto(
            symbols = symbolMatrixConfigDto,
            imagesId = mutableSetOf(savedImage.id!!),
            speakerImageCount = 6,
            listenerImageCount = 8,
            speakerAnswerTime = 10000,
            listenerAnswerTime = 15000,
            correctAnswerPoints = 2,
            wrongAnswerPoints = -2,
            resultScreenTime = 5000
        )

        mockMvc.perform(post("/game-configs/${savedGameConfig.id}")
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

        mockMvc.perform(delete("/game-configs/${savedGameConfig.id}"))
            .andExpect(status().isNoContent)

        assertFalse(gameConfigRepository.existsById(savedGameConfig.id!!))
    }
}