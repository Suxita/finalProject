package ge.tsu.finalProject.data.remote.dto;
import com.google.gson.annotations.SerializedName

data class AnimeResponseDto(
    @SerializedName("data") val data: List<AnimeDto>,
    @SerializedName("pagination") val pagination: PaginationDto?
)

data class AnimeDetailResponseDto(
    @SerializedName("data") val data: AnimeDto
)

data class AnimeDto(
    @SerializedName("mal_id") val malId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("title_english") val titleEnglish: String?,
    @SerializedName("images") val images: ImagesDto,
    @SerializedName("score") val score: Double?,
    @SerializedName("synopsis") val synopsis: String?,
    @SerializedName("episodes") val episodes: Int?,
    @SerializedName("type") val type: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("genres") val genres: List<GenreDto>?,
    @SerializedName("year") val year: Int?
)

data class ImagesDto(
    @SerializedName("jpg") val jpg: ImageUrlDto
)

data class ImageUrlDto(
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("large_image_url") val largeImageUrl: String
)

data class GenreDto(
    @SerializedName("mal_id") val malId: Int,
    @SerializedName("name") val name: String
)

data class PaginationDto(
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("has_next_page") val hasNextPage: Boolean,
    @SerializedName("last_visible_page") val lastVisiblePage: Int
)
