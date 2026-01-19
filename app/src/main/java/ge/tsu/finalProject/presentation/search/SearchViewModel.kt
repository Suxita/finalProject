package ge.tsu.finalProject.presentation.search;
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ge.tsu.finalProject.domain.model.Anime
import ge.tsu.finalProject.domain.model.WatchStatus
import ge.tsu.finalProject.domain.usecase.SaveAnimeUseCase
import ge.tsu.finalProject.domain.usecase.SearchAnimeUseCase
import ge.tsu.finalProject.presentation.common.ViewState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchAnimeUseCase: SearchAnimeUseCase,
    private val saveAnimeUseCase: SaveAnimeUseCase
) : ViewModel() {

    private val _searchResults = MutableLiveData<ViewState<List<Anime>>>()
    val searchResults: LiveData<ViewState<List<Anime>>> = _searchResults

    private var searchJob: Job? = null

    fun searchAnime(query: String) {
        searchJob?.cancel()

        if (query.isBlank()) {
            _searchResults.value = ViewState.Idle
            return
        }

        searchJob = viewModelScope.launch {
            delay(500) // Debounce

            _searchResults.value = ViewState.Loading

            searchAnimeUseCase(query).fold(
                onSuccess = { results ->
                    _searchResults.value = ViewState.Success(results)
                },
                onFailure = { error ->
                    _searchResults.value = ViewState.Error(
                        error.message ?: "ძიება ვერ მოხერხდა"
                    )
                }
            )
        }
    }

    fun saveAnime(anime: Anime, status: WatchStatus) {
        viewModelScope.launch {
            saveAnimeUseCase(anime, status)
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        _searchResults.value = ViewState.Idle
    }
}
