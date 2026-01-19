package ge.tsu.finalProject.presentation.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ge.tsu.finalProject.databinding.FragmentLibraryBinding
import ge.tsu.finalProject.presentation.common.ViewState
import ge.tsu.finalProject.util.gone
import ge.tsu.finalProject.util.visible

class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LibraryViewModel by viewModels { LibraryViewModelFactory() }

    private lateinit var animeAdapter: AnimeLibraryAdapter

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

        setupRecyclerView()
        setupSwipeRefresh()
        setupRetryButton()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        animeAdapter = AnimeLibraryAdapter(
            onWatchedClick = { anime ->
                viewModel.markAsWatched(anime)
                showSnackbar("${anime.title} დამატა ნანახებში ✓")
            },
            onPlanToWatchClick = { anime ->
                viewModel.markAsPlanToWatch(anime)
                showSnackbar("${anime.title} დამატა სანახავებში 📋")
            }
        )

        binding.rvAnimeLibrary.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = animeAdapter
            setHasFixedSize(true)

            // Infinite Scroll Listener
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    val totalItemCount = layoutManager.itemCount

                    // Load more when reaching 5 items before the end
                    if (lastVisibleItemPosition >= totalItemCount - 5 && dy > 0) {
                        viewModel.loadNextPage()
                    }
                }
            })
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun setupRetryButton() {
        binding.btnRetry.setOnClickListener {
            viewModel.refresh()
        }
    }

    private fun observeViewModel() {
        viewModel.animeList.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ViewState.Loading -> {
                    if (animeAdapter.itemCount == 0) {
                        binding.progressBar.visible()
                        binding.rvAnimeLibrary.gone()
                        binding.errorLayout.gone()
                    }
                }
                is ViewState.Success -> {
                    binding.progressBar.gone()
                    binding.rvAnimeLibrary.visible()
                    binding.errorLayout.gone()
                    binding.swipeRefreshLayout.isRefreshing = false

                    animeAdapter.submitList(state.data)

                    // Show empty state if needed
                    if (state.data.isEmpty()) {
                        binding.errorLayout.visible()
                        binding.rvAnimeLibrary.gone()
                        binding.tvError.text = "ვერ მოიძებნა anime"
                        binding.btnRetry.visible()
                    }
                }
                is ViewState.Error -> {
                    binding.progressBar.gone()
                    binding.swipeRefreshLayout.isRefreshing = false

                    if (animeAdapter.itemCount == 0) {
                        binding.rvAnimeLibrary.gone()
                        binding.errorLayout.visible()
                        binding.tvError.text = state.message
                        binding.btnRetry.visible()
                    } else {
                        showSnackbar(state.message)
                    }
                }
                is ViewState.Idle -> {
                    // Do nothing
                }
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
