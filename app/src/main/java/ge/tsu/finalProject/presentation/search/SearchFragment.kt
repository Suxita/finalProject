package ge.tsu.finalProject.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ge.tsu.finalProject.databinding.FragmentSearchBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var searchAdapter: SearchAnimeAdapter

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

        // Setup RecyclerView FIRST
        setupRecyclerView()

        // Setup SearchView
        setupSearchView()

        // Observe ViewModel
        observeViewModel()
    }

    private fun setupRecyclerView() {
        searchAdapter = SearchAnimeAdapter(
            onItemClick = { anime ->
                // Handle detail view
                Toast.makeText(requireContext(), anime.title, Toast.LENGTH_SHORT).show()
            },
            onAddToWatchedClick = { anime ->
                viewModel.addToWatched(anime)
                Toast.makeText(
                    requireContext(),
                    "${anime.title} დაემატა ნანახებში",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onAddToPlanToWatchClick = { anime ->
                viewModel.addToPlanToWatch(anime)
                Toast.makeText(
                    requireContext(),
                    "${anime.title} დაემატა სანახავებში",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        // Make sure this matches your fragment_search.xml
        binding.rvSearchResults.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotBlank()) {
                        viewModel.searchAnime(it)
                        // Hide keyboard
                        binding.searchView.clearFocus()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Optional: implement debounced search
                return true
            }
        })
    }

    private fun observeViewModel() {
        // Observe search results
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResults.collect { results ->
                searchAdapter.submitList(results)

                // Show/hide empty state
                if (results.isEmpty() && !viewModel.isLoading.value) {
                    binding.rvSearchResults.visibility = View.GONE
                    binding.tvEmptyState.visibility = View.VISIBLE
                } else {
                    binding.rvSearchResults.visibility = View.VISIBLE
                    binding.tvEmptyState.visibility = View.GONE
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