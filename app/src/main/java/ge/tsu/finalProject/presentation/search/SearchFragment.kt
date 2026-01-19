package ge.tsu.finalProject.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ge.tsu.finalProject.R
import ge.tsu.finalProject.databinding.FragmentSearchBinding
import ge.tsu.finalProject.domain.model.Anime
import ge.tsu.finalProject.domain.model.WatchStatus
import ge.tsu.finalProject.presentation.common.ViewState
import ge.tsu.finalProject.util.gone
import ge.tsu.finalProject.util.hideKeyboard
import ge.tsu.finalProject.util.visible

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels { SearchViewModelFactory() }

    private lateinit var searchAdapter: SearchResultAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        searchAdapter = SearchResultAdapter(
            onWatchedClick = { anime: Anime ->
                viewModel.saveAnime(anime, WatchStatus.WATCHED)
                showSnackbar("${anime.title} დამატა ნანახებში ✓")
            },
            onPlanToWatchClick = { anime: Anime ->
                viewModel.saveAnime(anime, WatchStatus.PLAN_TO_WATCH)
                showSnackbar("${anime.title} დამატა სანახავებში 📋")
            }
        )

        binding.rvSearchResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.searchAnime(it)
                    hideKeyboard()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    viewModel.clearSearch()
                } else if (newText.length >= 3) {
                    viewModel.searchAnime(newText)
                }
                return true
            }
        })

        binding.btnClearSearch.setOnClickListener {
            binding.searchView.setQuery("", false)
            binding.searchView.clearFocus()
            viewModel.clearSearch()
        }
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ViewState.Idle -> {
                    binding.progressBar.gone()
                    binding.rvSearchResults.gone()
                    binding.emptyStateLayout.visible()
                    binding.tvEmptyState.text = getString(R.string.search_hint)
                }
                is ViewState.Loading -> {
                    binding.progressBar.visible()
                    binding.rvSearchResults.gone()
                    binding.emptyStateLayout.gone()
                }
                is ViewState.Success -> {
                    binding.progressBar.gone()

                    if (state.data.isEmpty()) {
                        binding.rvSearchResults.gone()
                        binding.emptyStateLayout.visible()
                        binding.tvEmptyState.text = getString(R.string.no_results)
                    } else {
                        binding.rvSearchResults.visible()
                        binding.emptyStateLayout.gone()
                        searchAdapter.updateList(state.data)
                    }
                }
                is ViewState.Error -> {
                    binding.progressBar.gone()
                    binding.rvSearchResults.gone()
                    binding.emptyStateLayout.visible()
                    binding.tvEmptyState.text = state.message
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