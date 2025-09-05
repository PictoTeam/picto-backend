package pl.umcs.picto3.config


import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import pl.umcs.picto3.game.GameWebSocketHandler


@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val gameHandler: GameWebSocketHandler
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry
            .addHandler(gameHandler, "/ws/games/{gameAccessCode}/{role}")
            .setAllowedOrigins("*") // TODO: only for dev reasons must be changed in future
    }
}