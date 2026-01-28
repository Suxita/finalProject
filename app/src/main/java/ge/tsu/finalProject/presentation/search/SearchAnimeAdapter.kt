package ge.tsu.finalProject.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ge.tsu.finalProject.R
import ge.tsu.finalProject.databinding.ItemSearchAnimeBinding
import ge.tsu.finalProject.domain.model.Anime

class SearchAnimeAdapter(
    private val onItemClick: (Anime) -> Unit,
    private val onAddToWatchedClick: (Anime) -> Unit,
    private val onAddToPlanToWatchClick: (Anime) -> Unit
) : ListAdapter<Anime, SearchAnimeAdapter.SearchAnimeViewHolder>(SearchAnimeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAnimeViewHolder {
        val binding = ItemSearchAnimeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchAnimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchAnimeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SearchAnimeViewHolder(
        private val binding: ItemSearchAnimeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(anime: Anime) {
            binding.apply {
                // Set title
                tvTitle.text = anime.title

                // Set synopsis
                tvSynopsis.text = anime.synopsis?.take(150) ?: "აღწერა არ არის"

                // Load image
                ivCover.load(anime.largeImageUrl ?: anime.imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_error)
                }

                // Set genres
                tvGenres.text = anime.genres.takeIf { it.isNotEmpty() }
                    ?.joinToString(", ")
                    ?: "ჟანრი არ არის"

                // Set score
                tvScore.text = anime.score?.toString() ?: "N/A"

                // Set episodes
                tvEpisodes.text = if (anime.episodes != null && anime.episodes > 0) {
                    "${anime.episodes} ეპიზოდი"
                } else {
                    "N/A"
                }

                // Set year
                tvYear.text = anime.year?.toString() ?: "N/A"

                // Click listeners
                root.setOnClickListener {
                    onItemClick(anime)
                }

                btnAddWatched.setOnClickListener {
                    onAddToWatchedClick(anime)
                }

                btnAddPlanToWatch.setOnClickListener {
                    onAddToPlanToWatchClick(anime)
                }
            }
        }
    }

    class SearchAnimeDiffCallback : DiffUtil.ItemCallback<Anime>() {
        override fun areItemsTheSame(oldItem: Anime, newItem: Anime): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Anime, newItem: Anime): Boolean {
            return oldItem == newItem
        }
    }
}