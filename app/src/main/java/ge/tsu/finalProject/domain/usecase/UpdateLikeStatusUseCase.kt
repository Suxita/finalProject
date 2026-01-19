package ge.tsu.finalProject.domain.usecase;

import ge.tsu.finalProject.domain.repository.AnimeRepository

class UpdateLikeStatusUseCase(
    private val repository: AnimeRepository
) {
    suspend operator fun invoke(animeId: Int, isLiked: Boolean?) {
        repository.updateLikeStatus(animeId, isLiked)
    }
}
