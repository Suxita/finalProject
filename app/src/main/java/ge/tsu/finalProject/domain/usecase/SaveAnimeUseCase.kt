package ge.tsu.finalProject.domain.usecase;

import ge.tsu.finalProject.domain.model.Anime
import ge.tsu.finalProject.domain.model.WatchStatus
import ge.tsu.finalProject.domain.repository.AnimeRepository

class SaveAnimeUseCase(
    private val repository: AnimeRepository
) {
    suspend operator fun invoke(anime: Anime, status: WatchStatus) {
        repository.saveAnime(anime, status)
    }
}
