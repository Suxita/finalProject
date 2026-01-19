package ge.tsu.finalProject.data.local.entity;
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_anime")
data class SavedAnimeEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val titleEnglish: String?,
    val imageUrl: String,
    val largeImageUrl: String,
    val score: Double?,
    val synopsis: String?,
    val episodes: Int?,
    val type: String?,
    val status: String?,
    val genres: String, // JSON string or comma-separated
    val year: Int?,
    val watchStatus: String, // WATCHED or PLAN_TO_WATCH
    val isLiked: Boolean?,
    val addedDate: Long
)
