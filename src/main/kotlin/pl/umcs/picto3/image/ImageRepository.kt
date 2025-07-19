package pl.umcs.picto3.image

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ImageRepository : JpaRepository<Image, Long> {
    fun findByFileHash(hash: String): Image?
    fun findByStoredFileName(filename: String): Image?
}