package ge.tsu.finalProject.presentation.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ge.tsu.finalProject.R
import ge.tsu.finalProject.databinding.ItemAnimeBinding
import ge.tsu.finalProject.domain.model.SavedAnime
import ge.tsu.finalProject.domain.model.WatchStatus

class AnimeAdapter(
    private val onItemClick: (SavedAnime) -> Unit,
    private val onWatchedClick: (SavedAnime) -> Unit,
    private val onPlanToWatchClick: (SavedAnime) -> Unit,
    private val onDeleteClick: (SavedAnime) -> Unit,
    private val onLikeClick: (SavedAnime) -> Unit
) : ListAdapter<SavedAnime, AnimeAdapter.AnimeViewHolder>(AnimeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val binding = ItemAnimeBinding.inflate(
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
        private val binding: ItemAnimeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(savedAnime: SavedAnime) {
            val anime = savedAnime.anime

            binding.apply {
                tvTitle.text = anime.title

                tvSynopsis.text = anime.synopsis?.take(150) ?: "აღწერა არ არის"

                ivCover.load(anime.imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder_anime)
                    error(R.drawable.ic_error_image)
                }

                tvGenres.text = if (anime.genres.isNotEmpty()) {
                    anime.genres.joinToString(", ")
                } else {
                    "ჟანრი არ არის"
                }

                tvScore.text = anime.score?.toString() ?: "N/A"

                tvEpisodes.text = if (anime.episodes != null && anime.episodes > 0) {
                    "${anime.episodes} ეპიზოდი"
                } else {
                    "N/A"
                }

                tvYear.text = anime.year?.toString() ?: "N/A"

                val isWatched = savedAnime.watchStatus == WatchStatus.WATCHED
                val isPlanToWatch = savedAnime.watchStatus == WatchStatus.PLAN_TO_WATCH

                btnWatched.isSelected = isWatched
                btnPlanToWatch.isSelected = isPlanToWatch

                when (savedAnime.isLiked) {
                    true -> {
                        btnLike.isSelected = true
                        btnLike.setImageResource(R.drawable.ic_favorite)
                    }
                    false -> {
                        btnLike.isSelected = false
                        btnLike.setImageResource(R.drawable.ic_dislike)
                    }
                    null -> {
                        btnLike.isSelected = false
                        btnLike.setImageResource(R.drawable.ic_favorite)
                    }
                }

                btnWatched.text = if (isWatched) "ნანახი ✓" else "ნანახი"
                btnPlanToWatch.text = if (isPlanToWatch) "სანახავი ✓" else "სანახავი"

                root.setOnClickListener {
                    onItemClick(savedAnime)
                }

                btnWatched.setOnClickListener {
                    onWatchedClick(savedAnime)
                }

                btnPlanToWatch.setOnClickListener {
                    onPlanToWatchClick(savedAnime)
                }

                btnDelete.setOnClickListener {
                    onDeleteClick(savedAnime)
                }

                btnLike.setOnClickListener {
                    onLikeClick(savedAnime)
                }
            }
        }
    }

    class AnimeDiffCallback : DiffUtil.ItemCallback<SavedAnime>() {
        override fun areItemsTheSame(oldItem: SavedAnime, newItem: SavedAnime): Boolean {
            return oldItem.anime.id == newItem.anime.id
        }

        override fun areContentsTheSame(oldItem: SavedAnime, newItem: SavedAnime): Boolean {
            return oldItem == newItem
        }
    }
}