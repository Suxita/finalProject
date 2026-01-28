package ge.tsu.finalProject.presentation.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ge.tsu.finalProject.data.local.entity.SavedAnimeEntity
import ge.tsu.finalProject.domain.model.Anime
import ge.tsu.finalProject.domain.model.WatchStatus
import ge.tsu.finalProject.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val animeRepository: AnimeRepository
) : ViewModel() {

    private val TAG = "SearchViewModel"

    // Search results
    private val _searchResults = MutableStateFlow<List<Anime>>(emptyList())
    val searchResults: StateFlow<List<Anime>> = _searchResults.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun searchAnime(query: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Log.d(TAG, "Searching for: $query")

                val result = animeRepository.searchAnime(query)

                result.onSuccess { animeList ->
                    _searchResults.value = animeList
                    Log.d(TAG, "Found ${animeList.size} results")
                }.onFailure { exception ->
                    Log.e(TAG, "Search error", exception)
                    _error.value = exception.message ?: "ძიების შეცდომა"
                    _searchResults.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Search error", e)
                _error.value = "ძიების შეცდომა: ${e.message}"
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToWatched(anime: Anime) {
        viewModelScope.launch {
            try {
                animeRepository.saveAnime(anime, WatchStatus.WATCHED)
                Log.d(TAG, "Added to watched: ${anime.title}")
            } catch (e: Exception) {
                Log.e(TAG, "Error adding to watched", e)
                _error.value = "შეცდომა დამატებისას: ${e.message}"
            }
        }
    }

    fun addToPlanToWatch(anime: Anime) {
        viewModelScope.launch {
            try {
                animeRepository.saveAnime(anime, WatchStatus.PLAN_TO_WATCH)
                Log.d(TAG, "Added to plan to watch: ${anime.title}")
            } catch (e: Exception) {
                Log.e(TAG, "Error adding to plan to watch", e)
                _error.value = "შეცდომა დამატებისას: ${e.message}"
            }
        }
    }
}