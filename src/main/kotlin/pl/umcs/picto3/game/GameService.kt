package pl.umcs.picto3.game

import org.springframework.stereotype.Service
import pl.umcs.picto3.image.Image
import pl.umcs.picto3.symbol.SymbolMatrix

@Service
class GameService(private val gameRepository: GameRepository) {

    fun getRandomImages(sessionAccessCode: String): List<Image> {
        val currentGame = gameRepository.getGameById(sessionAccessCode)
        return currentGame.gameConfig.images
            .shuffled()
            .take(currentGame.gameConfig.speakerImageCount.toInt())
    }

    fun getSymbolsForGame(sessionAccessCode: String): SymbolMatrix {
        val currentGame = gameRepository.getGameById(sessionAccessCode)
        return currentGame.gameConfig.symbols
    }
}