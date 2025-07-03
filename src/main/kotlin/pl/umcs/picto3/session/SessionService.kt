package pl.umcs.picto3.session

import org.springframework.stereotype.Service

@Service
class SessionService(
    private val sessionMapper: SessionMapper
) {
    fun createSession(dto: SessionConfigDto): Session = sessionMapper.createSessionByConfig(dto)

}