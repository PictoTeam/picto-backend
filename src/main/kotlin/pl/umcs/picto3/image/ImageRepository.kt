package pl.umcs.picto3.image

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ImageRepository : JpaRepository<Image, UUID> {
    fun findByFileHash(hash: String): Image?
    fun findByStoredFileName(filename: String): Image?
}