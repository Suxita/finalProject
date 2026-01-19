package ge.tsu.finalProject.data.local.dao;
import androidx.room.*
import androidx.room.Dao
import androidx.room.OnConflictStrategy
import ge.tsu.finalProject.data.local.entity.SavedAnimeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {

    @Query("SELECT * FROM saved_anime ORDER BY addedDate DESC")
    fun getAllSavedAnime(): Flow<List<SavedAnimeEntity>>

    @Query("SELECT * FROM saved_anime WHERE watchStatus = 'WATCHED' ORDER BY addedDate DESC")
    fun getWatchedAnime(): Flow<List<SavedAnimeEntity>>

    @Query("SELECT * FROM saved_anime WHERE watchStatus = 'PLAN_TO_WATCH' ORDER BY addedDate DESC")
    fun getPlanToWatchAnime(): Flow<List<SavedAnimeEntity>>

    @Query("SELECT * FROM saved_anime WHERE id = :id")
    suspend fun getAnimeById(id: Int): SavedAnimeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnime(anime: SavedAnimeEntity)

    @Update
    suspend fun updateAnime(anime: SavedAnimeEntity)

    @Query("UPDATE saved_anime SET watchStatus = :status WHERE id = :animeId")
    suspend fun updateWatchStatus(animeId: Int, status: String)

    @Query("UPDATE saved_anime SET isLiked = :isLiked WHERE id = :animeId")
    suspend fun updateLikeStatus(animeId: Int, isLiked: Boolean?)

    @Query("DELETE FROM saved_anime WHERE id = :animeId")
    suspend fun deleteAnime(animeId: Int)

    @Query("DELETE FROM saved_anime")
    suspend fun deleteAllAnime()

    @Query("SELECT COUNT(*) FROM saved_anime WHERE watchStatus = 'WATCHED'")
    suspend fun getWatchedCount(): Int

    @Query("SELECT COUNT(*) FROM saved_anime WHERE watchStatus = 'PLAN_TO_WATCH'")
    suspend fun getPlanToWatchCount(): Int
}
