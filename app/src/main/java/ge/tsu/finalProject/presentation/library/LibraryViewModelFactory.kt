package ge.tsu.finalProject.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ge.tsu.finalProject.di.AppModule

class LibraryViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LibraryViewModel::class.java)) {
            return LibraryViewModel(
                getAnimeLibraryUseCase = AppModule.provideGetAnimeLibraryUseCase(),
                saveAnimeUseCase = AppModule.provideSaveAnimeUseCase()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
