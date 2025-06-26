package pl.umcs.picto3.image

import jakarta.persistence.*

@Entity
@Table(name = "images")
data class Image(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:Column(name = "file_path", nullable = false)
    val filePath: String
)