package pl.umcs.picto3.session

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class SessionService(
    private val sessionMapper: SessionMapper
) {
    private val activeSessions = ConcurrentHashMap<String, Session>()

    fun createSession(dto: SessionConfigDto): String {
        //todo store session config separately in db
        val newCreatedSessionAccessCode = generateUniqueJoinCode()
        val newSession = sessionMapper.createSessionByConfig(dto, newCreatedSessionAccessCode)
        activeSessions[newCreatedSessionAccessCode] = newSession
        return newCreatedSessionAccessCode
    }

    private fun generateUniqueJoinCode(): String {
        var code: String
        do {
            code = (100000..999999).random().toString()
        } while (activeSessions.containsKey(code))

        return code
    }
}