package ge.tsu.finalProject.domain.usecase;

import ge.tsu.finalProject.domain.repository.AnimeRepository

class DeleteAnimeUseCase(
    private val repository: AnimeRepository
) {
    suspend operator fun invoke(animeId: Int) {
        repository.deleteAnime(animeId)
    }
}
