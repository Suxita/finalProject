package ge.tsu.finalProject.presentation.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ge.tsu.finalProject.R
import ge.tsu.finalProject.databinding.ItemAnimeLibraryBinding
import ge.tsu.finalProject.domain.model.Anime

class AnimeLibraryAdapter(
    private val onWatchedClick: (Anime) -> Unit,
    private val onPlanToWatchClick: (Anime) -> Unit
) : ListAdapter<Anime, AnimeLibraryAdapter.AnimeViewHolder>(AnimeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val binding = ItemAnimeLibraryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AnimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AnimeViewHolder(
        private val binding: ItemAnimeLibraryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(anime: Anime) {
            binding.apply {
                // Title
                tvTitle.text = anime.title

                // Score
                tvScore.text = if (anime.score != null) {
                    "⭐ ${anime.score}"
                } else {
                    "⭐ N/A"
                }

                // Episodes
                tvEpisodes.text = "${anime.episodes ?: "?"} ეპიზოდი"

                // Type
                tvType.text = anime.type ?: "TV"

                // Load Image with Coil
                ivPoster.load(anime.largeImageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder_anime)
                    error(R.drawable.ic_error_image)
                }

                // Watched Button
                btnWatched.setOnClickListener {
                    onWatchedClick(anime)
                }

                // Plan to Watch Button
                btnPlanToWatch.setOnClickListener {
                    onPlanToWatchClick(anime)
                }

                // Card Click (optional - for details)
                root.setOnClickListener {
                }
            }
        }
    }

    private class AnimeDiffCallback : DiffUtil.ItemCallback<Anime>() {
        override fun areItemsTheSame(oldItem: Anime, newItem: Anime): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Anime, newItem: Anime): Boolean {
            return oldItem == newItem
        }
    }
}
