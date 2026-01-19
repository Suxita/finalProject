package ge.tsu.finalProject.domain.usecase;

import ge.tsu.finalProject.domain.model.Anime
import ge.tsu.finalProject.domain.repository.AnimeRepository

class SearchAnimeUseCase(
    private val repository: AnimeRepository
) {
    suspend operator fun invoke(query: String): Result<List<Anime>> {
        return repository.searchAnime(query)
    }
}
