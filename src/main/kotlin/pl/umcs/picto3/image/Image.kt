package pl.umcs.picto3.image

import jakarta.persistence.*
import pl.umcs.picto3.round.Round

@Entity
@Table(name = "images")
data class Image(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @Column(name = "file_path")
    val filePath: String,

    @OneToMany
    val topics: List<Round>,

    @OneToMany
    val selectedImages: List<Round>
)