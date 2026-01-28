package ge.tsu.finalProject.presentation.myspace

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ge.tsu.finalProject.R
import ge.tsu.finalProject.databinding.ItemSavedAnimeBinding
import ge.tsu.finalProject.domain.model.SavedAnime
import ge.tsu.finalProject.util.gone
import ge.tsu.finalProject.util.visible

class SavedAnimeAdapter(
    private val onLikeClick: ((SavedAnime) -> Unit)? = null,
    private val onDislikeClick: ((SavedAnime) -> Unit)? = null,
    private val onRemoveLikeClick: ((SavedAnime) -> Unit)? = null,
    private val onDeleteClick: ((SavedAnime) -> Unit)? = null,
    private val showLikeButtons: Boolean = true,
    private val showMoveButton: Boolean = false
) : ListAdapter<SavedAnime, SavedAnimeAdapter.SavedAnimeViewHolder>(SavedAnimeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedAnimeViewHolder {
        val binding = ItemSavedAnimeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SavedAnimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedAnimeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SavedAnimeViewHolder(
        private val binding: ItemSavedAnimeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(savedAnime: SavedAnime) {
            val anime = savedAnime.anime

            binding.apply {
                tvTitle.text = anime.title

                tvScore.text = if (anime.score != null) {
                    "⭐ ${anime.score}"
                } else {
                    "⭐ N/A"
                }

                val genresText = anime.genres.take(3).joinToString(", ")
                tvGenres.text = genresText

                tvEpisodes.text = "${anime.episodes ?: "?"} ეპიზოდი"

                tvType.text = anime.type ?: "TV"

                ivPoster.load(anime.imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder_anime)
                    error(R.drawable.ic_error_image)
                }

                if (showLikeButtons) {
                    likeButtonsLayout.visible()
                    updateLikeButtons(savedAnime.isLiked)

                    btnLike.setOnClickListener {
                        if (savedAnime.isLiked == true) {
                            onRemoveLikeClick?.invoke(savedAnime)
                        } else {
                            onLikeClick?.invoke(savedAnime)
                        }
                    }

                    btnDislike.setOnClickListener {
                        if (savedAnime.isLiked == false) {
                            onRemoveLikeClick?.invoke(savedAnime)
                        } else {
                            onDislikeClick?.invoke(savedAnime)
                        }
                    }
                } else {
                    likeButtonsLayout.gone()
                }

                if (showMoveButton) {
                    btnMoveToWatched.visible()
                    btnMoveToWatched.setOnClickListener {
                        onLikeClick?.invoke(savedAnime)
                    }
                } else {
                    btnMoveToWatched.gone()
                }

                btnDelete.setOnClickListener {
                    showDeleteConfirmationDialog(savedAnime)
                }
            }
        }

        private fun updateLikeButtons(isLiked: Boolean?) {
            binding.apply {
                when (isLiked) {
                    true -> {
                        btnLike.setColorFilter(Color.parseColor("#4CAF50")) // Green
                        btnDislike.setColorFilter(Color.GRAY)
                        btnLike.alpha = 1.0f
                        btnDislike.alpha = 0.5f
                    }
                    false -> {
                        btnLike.setColorFilter(Color.GRAY)
                        btnDislike.setColorFilter(Color.parseColor("#F44336")) // Red
                        btnLike.alpha = 0.5f
                        btnDislike.alpha = 1.0f
                    }
                    null -> {
                        btnLike.setColorFilter(Color.GRAY)
                        btnDislike.setColorFilter(Color.GRAY)
                        btnLike.alpha = 0.5f
                        btnDislike.alpha = 0.5f
                    }
                }
            }
        }

        private fun showDeleteConfirmationDialog(savedAnime: SavedAnime) {
            AlertDialog.Builder(binding.root.context)
                .setTitle("წაშლა")
                .setMessage("დარწმუნებული ხართ რომ გსურთ ${savedAnime.anime.title}-ის წაშლა?")
                .setPositiveButton("დიახ") { _, _ ->
                    onDeleteClick?.invoke(savedAnime)
                }
                .setNegativeButton("არა", null)
                .show()
        }
    }

    private class SavedAnimeDiffCallback : DiffUtil.ItemCallback<SavedAnime>() {
        override fun areItemsTheSame(oldItem: SavedAnime, newItem: SavedAnime): Boolean {
            return oldItem.anime.id == newItem.anime.id
        }

        override fun areContentsTheSame(oldItem: SavedAnime, newItem: SavedAnime): Boolean {
            return oldItem == newItem
        }
    }
}
