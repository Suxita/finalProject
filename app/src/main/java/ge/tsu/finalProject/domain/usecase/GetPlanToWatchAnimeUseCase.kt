package ge.tsu.finalProject.domain.usecase

import ge.tsu.finalProject.domain.model.SavedAnime
import ge.tsu.finalProject.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.Flow

class GetPlanToWatchAnimeUseCase(
    private val repository: AnimeRepository
) {
    operator fun invoke(): Flow<List<SavedAnime>> {
        return repository.getPlanToWatchAnime()
    }
}
