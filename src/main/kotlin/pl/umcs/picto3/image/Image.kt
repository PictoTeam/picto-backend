package pl.umcs.picto3.image

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID


@Entity
@Table(name = "images")
data class Image(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column
    val storedFileName: String,

    @Column
    val fileName: String,

    @Column(name = "file_hash", unique = true, length = 64)
    val fileHash: String
)
