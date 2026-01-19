package ge.tsu.finalProject.presentation.aihub

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import ge.tsu.finalProject.domain.model.DailyRecommendation
import ge.tsu.finalProject.domain.model.SavedAnime
import ge.tsu.finalProject.domain.model.TasteAnalysis
import ge.tsu.finalProject.domain.usecase.GetAllSavedAnimeUseCase
import ge.tsu.finalProject.domain.usecase.GetDailyRecommendationUseCase
import ge.tsu.finalProject.domain.usecase.GetUserTasteAnalysisUseCase
import ge.tsu.finalProject.presentation.common.ViewState
import kotlinx.coroutines.launch

class AIHubViewModel(
    private val getAllSavedAnimeUseCase: GetAllSavedAnimeUseCase,
    private val getTasteAnalysisUseCase: GetUserTasteAnalysisUseCase,
    private val getDailyRecommendationUseCase: GetDailyRecommendationUseCase
) : ViewModel() {

    val allSavedAnime: LiveData<List<SavedAnime>> = getAllSavedAnimeUseCase().asLiveData()

    private val _tasteAnalysis = MutableLiveData<ViewState<TasteAnalysis>>(ViewState.Idle)
    val tasteAnalysis: LiveData<ViewState<TasteAnalysis>> = _tasteAnalysis

    private val _dailyRecommendation = MutableLiveData<ViewState<DailyRecommendation>>(ViewState.Idle)
    val dailyRecommendation: LiveData<ViewState<DailyRecommendation>> = _dailyRecommendation

    fun analyzeTaste() {
        val savedAnime = allSavedAnime.value ?: emptyList()

        if (savedAnime.isEmpty()) {
            _tasteAnalysis.value = ViewState.Error("ჯერ არ გაქვთ შენახული anime-ები!")
            return
        }

        val likedAnime = savedAnime.filter { it.isLiked == true }
        if (likedAnime.isEmpty()) {
            _tasteAnalysis.value = ViewState.Error("გთხოვთ მონიშნოთ რომელი anime მოგწონთ (❤️)")
            return
        }

        viewModelScope.launch {
            _tasteAnalysis.value = ViewState.Loading

            getTasteAnalysisUseCase(savedAnime).fold(
                onSuccess = { analysis ->
                    _tasteAnalysis.value = ViewState.Success(analysis)
                },
                onFailure = { error ->
                    _tasteAnalysis.value = ViewState.Error(
                        error.message ?: "შეფასება ვერ მოხერხდა"
                    )
                }
            )
        }
    }

    fun getDailyRecommendation() {
        val savedAnime = allSavedAnime.value ?: emptyList()

        if (savedAnime.isEmpty()) {
            _dailyRecommendation.value = ViewState.Error("ჯერ არ გაქვთ შენახული anime-ები!")
            return
        }

        val likedAnime = savedAnime.filter { it.isLiked == true }
        if (likedAnime.isEmpty()) {
            _dailyRecommendation.value = ViewState.Error("გთხოვთ მონიშნოთ რომელი anime მოგწონთ (❤️)")
            return
        }

        viewModelScope.launch {
            _dailyRecommendation.value = ViewState.Loading

            getDailyRecommendationUseCase(savedAnime).fold(
                onSuccess = { recommendation ->
                    _dailyRecommendation.value = ViewState.Success(recommendation)
                },
                onFailure = { error ->
                    _dailyRecommendation.value = ViewState.Error(
                        error.message ?: "რეკომენდაცია ვერ მოიძებნა"
                    )
                }
            )
        }
    }

    fun resetTasteAnalysis() {
        _tasteAnalysis.value = ViewState.Idle
    }

    fun resetDailyRecommendation() {
        _dailyRecommendation.value = ViewState.Idle
    }
}
