package ge.tsu.finalProject.domain.usecase;

import ge.tsu.finalProject.domain.model.WatchStatus
import ge.tsu.finalProject.domain.repository.AnimeRepository

class UpdateWatchStatusUseCase(
    private val repository: AnimeRepository
) {
    suspend operator fun invoke(animeId: Int, status: WatchStatus) {
        repository.updateWatchStatus(animeId, status)
    }
}
