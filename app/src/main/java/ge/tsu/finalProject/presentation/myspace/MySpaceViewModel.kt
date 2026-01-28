package ge.tsu.finalProject.presentation.myspace

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ge.tsu.finalProject.domain.model.SavedAnime
import ge.tsu.finalProject.domain.model.WatchStatus
import ge.tsu.finalProject.domain.usecase.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MySpaceViewModel @Inject constructor(
    private val getAllSavedAnimeUseCase: GetAllSavedAnimeUseCase,
    private val getWatchedAnimeUseCase: GetWatchedAnimeUseCase,
    private val getPlanToWatchAnimeUseCase: GetPlanToWatchAnimeUseCase,
    private val updateLikeStatusUseCase: UpdateLikeStatusUseCase,  // Added!
    private val updateWatchStatusUseCase: UpdateWatchStatusUseCase,  // Added!
    private val deleteAnimeUseCase: DeleteAnimeUseCase  // Added!
) : ViewModel() {

    val watchedAnime: LiveData<List<SavedAnime>> = getWatchedAnimeUseCase().asLiveData()
    val planToWatchAnime: LiveData<List<SavedAnime>> = getPlanToWatchAnimeUseCase().asLiveData()

    private val _selectedTab = MutableLiveData(0)
    val selectedTab: LiveData<Int> = _selectedTab

    private val _actionMessage = MutableLiveData<String?>()
    val actionMessage: LiveData<String?> = _actionMessage

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    fun likeAnime(animeId: Int) {
        viewModelScope.launch {
            updateLikeStatusUseCase(animeId, true)
            _actionMessage.value = "მოწონებულია ✓"
            _actionMessage.value = null
        }
    }

    fun dislikeAnime(animeId: Int) {
        viewModelScope.launch {
            updateLikeStatusUseCase(animeId, false)
            _actionMessage.value = "არ მოგწონთ"
            _actionMessage.value = null
        }
    }

    fun removeLike(animeId: Int) {
        viewModelScope.launch {
            updateLikeStatusUseCase(animeId, null)
            _actionMessage.value = "შეფასება წაშლილია"
            _actionMessage.value = null
        }
    }

    fun moveToWatched(animeId: Int) {
        viewModelScope.launch {
            updateWatchStatusUseCase(animeId, WatchStatus.WATCHED)
            _actionMessage.value = "გადატანილია ნანახებში"
            _actionMessage.value = null
        }
    }

    fun moveToPlanToWatch(animeId: Int) {
        viewModelScope.launch {
            updateWatchStatusUseCase(animeId, WatchStatus.PLAN_TO_WATCH)
            _actionMessage.value = "გადატანილია სანახავებში"
            _actionMessage.value = null
        }
    }

    fun deleteAnime(animeId: Int, title: String) {
        viewModelScope.launch {
            deleteAnimeUseCase(animeId)
            _actionMessage.value = "$title წაშლილია"
            _actionMessage.value = null
        }
    }
}