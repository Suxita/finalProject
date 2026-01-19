package ge.tsu.finalProject.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ge.tsu.finalProject.R
import ge.tsu.finalProject.databinding.ItemSearchResultBinding
import ge.tsu.finalProject.domain.model.Anime

class SearchResultAdapter(
    private val onWatchedClick: (Anime) -> Unit,
    private val onPlanToWatchClick: (Anime) -> Unit
) : RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>() {

    private var animeList: List<Anime> = emptyList()

    fun updateList(newList: List<Anime>) {
        animeList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val binding = ItemSearchResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(animeList[position])
    }

    override fun getItemCount(): Int = animeList.size

    inner class SearchResultViewHolder(
        private val binding: ItemSearchResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(anime: Anime) {
            binding.apply {
                // Title
                tvAnimeTitle.text = anime.title

                // Score
                tvAnimeScore.text = if (anime.score != null) {
                    "⭐ ${anime.score}"
                } else {
                    "⭐ N/A"
                }

                // Episodes
                tvAnimeEpisodes.text = "${anime.episodes ?: "?"} ეპიზოდი"

                // Type & Year
                val typeYear = buildString {
                    anime.type?.let { append(it) }
                    anime.year?.let {
                        if (isNotEmpty()) append(" • ")
                        append(it)
                    }
                }
                tvAnimeType.text = typeYear

                // Genres
                if (anime.genres.isNotEmpty()) {
                    tvAnimeGenres.text = anime.genres.take(3).joinToString(", ")
                } else {
                    tvAnimeGenres.text = "No genres"
                }

                // Synopsis
                tvAnimeSynopsis.text = anime.synopsis ?: "No synopsis available"

                // Load Image with Coil
                ivAnimeImage.load(anime.imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder_anime)
                    error(R.drawable.ic_error_image)
                }

                // Watched Button
                btnAddWatched.setOnClickListener {
                    onWatchedClick(anime)
                }

                // Plan to Watch Button
                btnAddPlanToWatch.setOnClickListener {
                    onPlanToWatchClick(anime)
                }
            }
        }
    }
}