package ge.tsu.finalProject.presentation.library

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ge.tsu.finalProject.domain.model.SavedAnime
import ge.tsu.finalProject.domain.model.WatchStatus
import ge.tsu.finalProject.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class FilterType {
    ALL, WATCHED, PLAN_TO_WATCH
}

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val animeRepository: AnimeRepository
) : ViewModel() {

    private val tag = "LibraryViewModel"

    // Current filter
    private val _currentFilter = MutableStateFlow(FilterType.ALL)
    val currentFilter: StateFlow<FilterType> = _currentFilter.asStateFlow()

    // All anime from database
    private val _allAnimeList = animeRepository.getAllSavedAnime()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Filtered anime list based on current filter
    val filteredAnimeList: StateFlow<List<SavedAnime>> = combine(
        _allAnimeList,
        _currentFilter
    ) { animeList, filter ->
        Log.d(tag, "Filtering ${animeList.size} anime with filter: $filter")
        when (filter) {
            FilterType.ALL -> animeList
            FilterType.WATCHED -> animeList.filter { it.watchStatus == WatchStatus.WATCHED }
            FilterType.PLAN_TO_WATCH -> animeList.filter { it.watchStatus == WatchStatus.PLAN_TO_WATCH }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        // Debug log
        viewModelScope.launch {
            _allAnimeList.collect { list ->
                Log.d(tag, "Anime list updated: ${list.size} items")
            }
        }
    }

    fun setFilter(filter: FilterType) {
        Log.d(tag, "Setting filter to: $filter")
        _currentFilter.value = filter
    }

    fun toggleWatched(anime: SavedAnime) {
        viewModelScope.launch {
            try {
                val newStatus = if (anime.watchStatus == WatchStatus.WATCHED) {
                    WatchStatus.PLAN_TO_WATCH
                } else {
                    WatchStatus.WATCHED
                }
                animeRepository.updateWatchStatus(anime.anime.id, newStatus)
                Log.d(tag, "Toggled watched for: ${anime.anime.title}")
            } catch (e: Exception) {
                Log.e(tag, "Error toggling watched", e)
                _error.value = "შეცდომა: ${e.message}"
            }
        }
    }

    fun togglePlanToWatch(anime: SavedAnime) {
        viewModelScope.launch {
            try {
                val newStatus = if (anime.watchStatus == WatchStatus.PLAN_TO_WATCH) {
                    WatchStatus.WATCHED
                } else {
                    WatchStatus.PLAN_TO_WATCH
                }
                animeRepository.updateWatchStatus(anime.anime.id, newStatus)
                Log.d(tag, "Toggled plan to watch for: ${anime.anime.title}")
            } catch (e: Exception) {
                Log.e(tag, "Error toggling plan to watch", e)
                _error.value = "შეცდომა: ${e.message}"
            }
        }
    }

    fun toggleLike(anime: SavedAnime) {
        viewModelScope.launch {
            try {
                val newLikedState = when (anime.isLiked) {
                    true -> false
                    false -> null
                    null -> true
                }
                animeRepository.updateLikeStatus(anime.anime.id, newLikedState)
                Log.d(tag, "Toggled like for: ${anime.anime.title}")
            } catch (e: Exception) {
                Log.e(tag, "Error toggling like", e)
                _error.value = "შეცდომა: ${e.message}"
            }
        }
    }

    fun deleteAnime(anime: SavedAnime) {
        viewModelScope.launch {
            try {
                animeRepository.deleteAnime(anime.anime.id)
                Log.d(tag, "Deleted anime: ${anime.anime.title}")
            } catch (e: Exception) {
                Log.e(tag, "Error deleting anime", e)
                _error.value = "შეცდომა წაშლისას: ${e.message}"
            }
        }
    }
}