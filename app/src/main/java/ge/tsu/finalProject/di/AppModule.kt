package ge.tsu.finalProject.di

import android.content.Context
import ge.tsu.finalProject.data.local.AnimeDatabase
import ge.tsu.finalProject.data.remote.RetrofitClient
import ge.tsu.finalProject.data.repository.AnimeRepositoryImpl
import ge.tsu.finalProject.domain.repository.AnimeRepository
import ge.tsu.finalProject.domain.usecase.*

object AppModule {

    private lateinit var database: AnimeDatabase
    private lateinit var repository: AnimeRepository
    private lateinit var appContext: Context

    fun initialize(context: Context, claudeApiKey: String) {
        appContext = context.applicationContext
        database = AnimeDatabase.getDatabase(appContext)

        repository = AnimeRepositoryImpl(
            jikanApi = RetrofitClient.jikanApi,
            claudeApi = RetrofitClient.claudeApi,
            animeDao = database.animeDao(),
            claudeApiKey = claudeApiKey,
            context = appContext
        )
    }

    // Repository
    fun provideAnimeRepository(): AnimeRepository = repository

    // Use Cases
    fun provideGetAnimeLibraryUseCase() = GetAnimeLibraryUseCase(repository)

    fun provideSearchAnimeUseCase() = SearchAnimeUseCase(repository)

    fun provideSaveAnimeUseCase() = SaveAnimeUseCase(repository)

    fun provideUpdateWatchStatusUseCase() = UpdateWatchStatusUseCase(repository)

    fun provideUpdateLikeStatusUseCase() = UpdateLikeStatusUseCase(repository)

    fun provideDeleteAnimeUseCase() = DeleteAnimeUseCase(repository)

    fun provideGetWatchedAnimeUseCase() = GetWatchedAnimeUseCase(repository)

    fun provideGetPlanToWatchAnimeUseCase() = GetPlanToWatchAnimeUseCase(repository)

    fun provideGetAllSavedAnimeUseCase() = GetAllSavedAnimeUseCase(repository)

    fun provideGetUserTasteAnalysisUseCase() = GetUserTasteAnalysisUseCase(repository)

    fun provideGetDailyRecommendationUseCase() = GetDailyRecommendationUseCase(repository)
}