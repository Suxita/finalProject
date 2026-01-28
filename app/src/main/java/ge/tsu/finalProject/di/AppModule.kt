package ge.tsu.finalProject.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ge.tsu.finalProject.BuildConfig
import ge.tsu.finalProject.data.local.AnimeDatabase
import ge.tsu.finalProject.data.remote.RetrofitClient
import ge.tsu.finalProject.data.repository.AnimeRepositoryImpl
import ge.tsu.finalProject.domain.repository.AnimeRepository
import ge.tsu.finalProject.domain.usecase.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAnimeDatabase(@ApplicationContext context: Context): AnimeDatabase {
        return AnimeDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideAnimeRepository(
        @ApplicationContext context: Context,
        database: AnimeDatabase
    ): AnimeRepository {
        return AnimeRepositoryImpl(
            jikanApi = RetrofitClient.jikanApi,
            claudeApi = RetrofitClient.claudeApi,
            animeDao = database.animeDao(),
            claudeApiKey = BuildConfig.CLAUDE_API_KEY,
            context = context
        )
    }

    // Use Cases
    @Provides
    @Singleton
    fun provideGetAnimeLibraryUseCase(repository: AnimeRepository) =
        GetAnimeLibraryUseCase(repository)

    @Provides
    @Singleton
    fun provideSearchAnimeUseCase(repository: AnimeRepository) =
        SearchAnimeUseCase(repository)

    @Provides
    @Singleton
    fun provideSaveAnimeUseCase(repository: AnimeRepository) =
        SaveAnimeUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateWatchStatusUseCase(repository: AnimeRepository) =
        UpdateWatchStatusUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateLikeStatusUseCase(repository: AnimeRepository) =
        UpdateLikeStatusUseCase(repository)

    @Provides
    @Singleton
    fun provideDeleteAnimeUseCase(repository: AnimeRepository) =
        DeleteAnimeUseCase(repository)

    @Provides
    @Singleton
    fun provideGetWatchedAnimeUseCase(repository: AnimeRepository) =
        GetWatchedAnimeUseCase(repository)

    @Provides
    @Singleton
    fun provideGetPlanToWatchAnimeUseCase(repository: AnimeRepository) =
        GetPlanToWatchAnimeUseCase(repository)

    @Provides
    @Singleton
    fun provideGetAllSavedAnimeUseCase(repository: AnimeRepository) =
        GetAllSavedAnimeUseCase(repository)

    @Provides
    @Singleton
    fun provideGetUserTasteAnalysisUseCase(repository: AnimeRepository) =
        GetUserTasteAnalysisUseCase(repository)

    @Provides
    @Singleton
    fun provideGetDailyRecommendationUseCase(repository: AnimeRepository) =
        GetDailyRecommendationUseCase(repository)
}