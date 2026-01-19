package ge.tsu.finalProject.domain.usecase;

import ge.tsu.finalProject.domain.model.SavedAnime
import ge.tsu.finalProject.domain.model.TasteAnalysis
import ge.tsu.finalProject.domain.repository.AnimeRepository

class GetUserTasteAnalysisUseCase(
    private val repository: AnimeRepository
) {
    suspend operator fun invoke(watchedAnime: List<SavedAnime>): Result<TasteAnalysis> {
        return repository.analyzeTaste(watchedAnime)
    }
}
