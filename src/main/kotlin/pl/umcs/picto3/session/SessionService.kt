package pl.umcs.picto3.session

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class SessionService(
) {
    private val activeSessions = ConcurrentHashMap<String, Session>()

    fun createSession(): String {
        TODO("Implement session creation logic here")
    }

    private fun generateUniqueJoinCode(): String {
        var code: String
        do {
            code = (100000..999999).random().toString()
        } while (activeSessions.containsKey(code))

        return code
    }
}