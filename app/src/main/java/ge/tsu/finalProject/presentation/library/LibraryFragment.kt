package ge.tsu.finalProject.presentation.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ge.tsu.finalProject.databinding.FragmentLibraryBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LibraryViewModel by viewModels()
    private lateinit var animeAdapter: AnimeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView FIRST - before observing data
        setupRecyclerView()

        // Setup filter chips
        setupFilterChips()

        // Then observe ViewModel
        observeViewModel()
    }

    private fun setupRecyclerView() {
        // Initialize adapter with callbacks
        animeAdapter = AnimeAdapter(
            onItemClick = { anime ->
                // Handle item click - navigate to detail screen
                Toast.makeText(requireContext(), anime.anime.title, Toast.LENGTH_SHORT).show()
            },
            onWatchedClick = { anime ->
                viewModel.toggleWatched(anime)
            },
            onPlanToWatchClick = { anime ->
                viewModel.togglePlanToWatch(anime)
            },
            onDeleteClick = { anime ->
                viewModel.deleteAnime(anime)
                Toast.makeText(
                    requireContext(),
                    "${anime.anime.title} წაიშალა",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onLikeClick = { anime ->
                viewModel.toggleLike(anime)
            }
        )

        // Set adapter and layout manager - using correct ID from XML
        binding.recyclerView.apply {
            adapter = animeAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun setupFilterChips() {
        binding.chipAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.setFilter(FilterType.ALL)
            }
        }

        binding.chipWatched.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.setFilter(FilterType.WATCHED)
            }
        }

        binding.chipPlanToWatch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.setFilter(FilterType.PLAN_TO_WATCH)
            }
        }
    }

    private fun observeViewModel() {
        // Observe filtered anime list
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filteredAnimeList.collect { animeList ->
                // Update adapter with new data
                animeAdapter.submitList(animeList)

                // Show/hide empty state - using correct ID from XML
                if (animeList.isEmpty()) {
                    binding.recyclerView.visibility = View.GONE
                    binding.emptyStateLayout.visibility = View.VISIBLE
                } else {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.emptyStateLayout.visibility = View.GONE
                }
            }
        }

        // Observe loading state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        // Observe errors
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collect { errorMessage ->
                errorMessage?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}