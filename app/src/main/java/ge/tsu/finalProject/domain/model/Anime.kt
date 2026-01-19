package ge.tsu.finalProject.domain.model;
data class Anime(
    val id: Int,
    val title: String,
    val titleEnglish: String?,
    val imageUrl: String,
    val largeImageUrl: String,
    val score: Double?,
    val synopsis: String?,
    val episodes: Int?,
    val type: String?,
    val status: String?,
    val genres: List<String>,
    val year: Int?
)
