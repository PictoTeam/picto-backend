package pl.umcs.picto3.image

import jakarta.persistence.*

@Entity
@Table(name = "images")
data class Image(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "file_path", nullable = false)
    val filePath: String
)