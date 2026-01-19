package ge.tsu.finalProject.presentation.myspace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ge.tsu.finalProject.di.AppModule

class MySpaceViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MySpaceViewModel::class.java)) {
            return MySpaceViewModel(
                getWatchedAnimeUseCase = AppModule.provideGetWatchedAnimeUseCase(),
                getPlanToWatchAnimeUseCase = AppModule.provideGetPlanToWatchAnimeUseCase(),
                updateLikeStatusUseCase = AppModule.provideUpdateLikeStatusUseCase(),
                updateWatchStatusUseCase = AppModule.provideUpdateWatchStatusUseCase(),
                deleteAnimeUseCase = AppModule.provideDeleteAnimeUseCase()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
