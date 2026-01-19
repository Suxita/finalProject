package ge.tsu.finalProject.presentation.aihub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ge.tsu.finalProject.di.AppModule

class AIHubViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AIHubViewModel::class.java)) {
            return AIHubViewModel(
                getAllSavedAnimeUseCase = AppModule.provideGetAllSavedAnimeUseCase(),
                getTasteAnalysisUseCase = AppModule.provideGetUserTasteAnalysisUseCase(),
                getDailyRecommendationUseCase = AppModule.provideGetDailyRecommendationUseCase()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
