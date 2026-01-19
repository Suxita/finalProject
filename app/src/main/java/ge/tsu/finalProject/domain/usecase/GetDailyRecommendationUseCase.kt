package ge.tsu.finalProject.domain.usecase;

import ge.tsu.finalProject.domain.model.DailyRecommendation
import ge.tsu.finalProject.domain.model.SavedAnime
import ge.tsu.finalProject.domain.repository.AnimeRepository

class GetDailyRecommendationUseCase(
    private val repository: AnimeRepository
) {
    suspend operator fun invoke(watchedAnime: List<SavedAnime>): Result<DailyRecommendation> {
        return repository.getDailyRecommendation(watchedAnime)
    }
}
