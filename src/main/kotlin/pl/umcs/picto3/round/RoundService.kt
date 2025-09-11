package pl.umcs.picto3.round

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RoundService(
    private val roundRepository: RoundRepository,
    private val roundMapper: RoundMapper
) {
    @Transactional
    fun saveNewRound(dto: InMemoryRound) {
        roundRepository.save(roundMapper.toEntity(dto))
    }
}