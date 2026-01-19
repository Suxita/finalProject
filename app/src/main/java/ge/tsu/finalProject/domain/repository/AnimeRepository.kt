package ge.tsu.finalProject.domain.repository;
import ge.tsu.finalProject.domain.model.*
import kotlinx.coroutines.flow.Flow

interface AnimeRepository {
    // Jikan API
    suspend fun getAnimeLibrary(page: Int): Result<List<Anime>>
    suspend fun searchAnime(query: String): Result<List<Anime>>
    suspend fun getAnimeDetails(id: Int): Result<Anime>

    // Local Database
    suspend fun saveAnime(anime: Anime, status: WatchStatus)
    suspend fun updateWatchStatus(animeId: Int, status: WatchStatus)
    suspend fun updateLikeStatus(animeId: Int, isLiked: Boolean?)
    suspend fun deleteAnime(animeId: Int)
    fun getWatchedAnime(): Flow<List<SavedAnime>>
    fun getPlanToWatchAnime(): Flow<List<SavedAnime>>
    fun getAllSavedAnime(): Flow<List<SavedAnime>>

    // Claude AI
    suspend fun analyzeTaste(watchedAnime: List<SavedAnime>): Result<TasteAnalysis>
    suspend fun getDailyRecommendation(watchedAnime: List<SavedAnime>): Result<DailyRecommendation>
}
