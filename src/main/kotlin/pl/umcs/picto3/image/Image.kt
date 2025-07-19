package pl.umcs.picto3.image

import jakarta.persistence.*

@Entity
@Table(name = "images")
data class Image(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @Column
    val storedFileName: String,

    @Column
    val fileName: String,

    @Column(name = "file_hash", unique = true, length = 64)
    val fileHash: String
)