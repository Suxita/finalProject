package ge.tsu.finalProject.domain.usecase;

import ge.tsu.finalProject.domain.model.Anime
import ge.tsu.finalProject.domain.repository.AnimeRepository

class GetAnimeLibraryUseCase(
    private val repository: AnimeRepository
) {
    suspend operator fun invoke(page: Int): Result<List<Anime>> {
        return repository.getAnimeLibrary(page)
    }
}
