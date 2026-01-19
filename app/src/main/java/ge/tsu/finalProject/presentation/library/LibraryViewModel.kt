package ge.tsu.finalProject.presentation.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ge.tsu.finalProject.domain.model.Anime
import ge.tsu.finalProject.domain.model.WatchStatus
import ge.tsu.finalProject.domain.usecase.GetAnimeLibraryUseCase
import ge.tsu.finalProject.domain.usecase.SaveAnimeUseCase
import ge.tsu.finalProject.presentation.common.ViewState
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val getAnimeLibraryUseCase: GetAnimeLibraryUseCase,
    private val saveAnimeUseCase: SaveAnimeUseCase
) : ViewModel() {

    private val _animeList = MutableLiveData<ViewState<List<Anime>>>(ViewState.Idle)
    val animeList: LiveData<ViewState<List<Anime>>> = _animeList

    private var currentPage = 1
    private var isLoading = false
    private val allAnimeList = mutableListOf<Anime>()

    init {
        loadAnimeLibrary()
    }

    fun loadAnimeLibrary() {
        if (isLoading) return

        viewModelScope.launch {
            isLoading = true

            if (currentPage == 1) {
                _animeList.value = ViewState.Loading
            }

            getAnimeLibraryUseCase(currentPage).fold(
                onSuccess = { animeList ->
                    if (currentPage == 1) {
                        allAnimeList.clear()
                    }
                    allAnimeList.addAll(animeList)

                    _animeList.value = ViewState.Success(allAnimeList.toList())
                    isLoading = false
                },
                onFailure = { error ->
                    _animeList.value = ViewState.Error(
                        error.message ?: "დაფიქსირდა შეცდომა"
                    )
                    isLoading = false
                }
            )
        }
    }

    fun loadNextPage() {
        if (!isLoading) {
            currentPage++
            loadAnimeLibrary()
        }
    }

    fun refresh() {
        currentPage = 1
        allAnimeList.clear()
        loadAnimeLibrary()
    }

    fun markAsWatched(anime: Anime) {
        viewModelScope.launch {
            saveAnimeUseCase(anime, WatchStatus.WATCHED)
        }
    }

    fun markAsPlanToWatch(anime: Anime) {
        viewModelScope.launch {
            saveAnimeUseCase(anime, WatchStatus.PLAN_TO_WATCH)
        }
    }
}
