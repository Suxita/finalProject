package ge.tsu.finalProject.domain.model;
data class SavedAnime(
    val anime: Anime,
    val watchStatus: WatchStatus,
    val isLiked: Boolean? = null,
    val addedDate: Long
)
