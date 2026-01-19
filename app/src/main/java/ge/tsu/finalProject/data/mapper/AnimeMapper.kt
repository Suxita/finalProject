package ge.tsu.finalProject.data.mapper;
import ge.tsu.finalProject.data.local.entity.SavedAnimeEntity
import ge.tsu.finalProject.data.remote.dto.AnimeDto
import ge.tsu.finalProject.domain.model.Anime
import ge.tsu.finalProject.domain.model.SavedAnime
import ge.tsu.finalProject.domain.model.WatchStatus

object AnimeMapper {

    // DTO to Domain
    fun toDomain(dto: AnimeDto): Anime {
        return Anime(
            id = dto.malId,
            title = dto.title,
            titleEnglish = dto.titleEnglish,
            imageUrl = dto.images.jpg.imageUrl,
            largeImageUrl = dto.images.jpg.largeImageUrl,
            score = dto.score,
            synopsis = dto.synopsis,
            episodes = dto.episodes,
            type = dto.type,
            status = dto.status,
            genres = dto.genres?.map { it.name } ?: emptyList(),
            year = dto.year
        )
    }

    // Domain to Entity
    fun toEntity(
        anime: Anime,
        watchStatus: WatchStatus,
        isLiked: Boolean? = null,
        addedDate: Long = System.currentTimeMillis()
    ): SavedAnimeEntity {
        return SavedAnimeEntity(
            id = anime.id,
            title = anime.title,
            titleEnglish = anime.titleEnglish,
            imageUrl = anime.imageUrl,
            largeImageUrl = anime.largeImageUrl,
            score = anime.score,
            synopsis = anime.synopsis,
            episodes = anime.episodes,
            type = anime.type,
            status = anime.status,
            genres = anime.genres.joinToString(","),
            year = anime.year,
            watchStatus = watchStatus.name,
            isLiked = isLiked,
            addedDate = addedDate
        )
    }

    // Entity to Domain
    fun toDomain(entity: SavedAnimeEntity): SavedAnime {
        return SavedAnime(
            anime = Anime(
                id = entity.id,
                title = entity.title,
                titleEnglish = entity.titleEnglish,
                imageUrl = entity.imageUrl,
                largeImageUrl = entity.largeImageUrl,
                score = entity.score,
                synopsis = entity.synopsis,
                episodes = entity.episodes,
                type = entity.type,
                status = entity.status,
                genres = entity.genres.split(",").filter { it.isNotBlank() },
                year = entity.year
            ),
            watchStatus = WatchStatus.valueOf(entity.watchStatus),
            isLiked = entity.isLiked,
            addedDate = entity.addedDate
        )
    }
}
