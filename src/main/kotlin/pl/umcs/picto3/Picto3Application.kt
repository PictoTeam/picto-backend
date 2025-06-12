package pl.umcs.picto3

import io.netty.util.internal.SocketUtils
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Picto3Application

fun main(args: Array<String>) {
	runApplication<Picto3Application>(*args)
}
