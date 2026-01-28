package ge.tsu.finalProject.presentation.myspace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import ge.tsu.finalProject.databinding.FragmentMySpaceBinding
import ge.tsu.finalProject.util.gone
import ge.tsu.finalProject.util.visible
@AndroidEntryPoint
class MySpaceFragment : Fragment() {

    private var _binding: FragmentMySpaceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MySpaceViewModel by viewModels()

    private lateinit var watchedAdapter: SavedAnimeAdapter
    private lateinit var planToWatchAdapter: SavedAnimeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMySpaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTabs()
        setupRecyclerViews()
        observeViewModel()
    }

    private fun setupTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("ნანახი"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("აპირებს ყურებას"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewModel.selectTab(tab.position)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setupRecyclerViews() {
        watchedAdapter = SavedAnimeAdapter(
            onLikeClick = { anime ->
                viewModel.likeAnime(anime.anime.id)
            },
            onDislikeClick = { anime ->
                viewModel.dislikeAnime(anime.anime.id)
            },
            onRemoveLikeClick = { anime ->
                viewModel.removeLike(anime.anime.id)
            },
            onDeleteClick = { anime ->
                viewModel.deleteAnime(anime.anime.id, anime.anime.title)
            },
            showLikeButtons = true
        )

        binding.rvWatched.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = watchedAdapter
        }

        planToWatchAdapter = SavedAnimeAdapter(
            onLikeClick = { anime ->
                viewModel.moveToWatched(anime.anime.id)
            },
            onDislikeClick = null,
            onRemoveLikeClick = null,
            onDeleteClick = { anime ->
                viewModel.deleteAnime(anime.anime.id, anime.anime.title)
            },
            showLikeButtons = false,
            showMoveButton = true
        )

        binding.rvPlanToWatch.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = planToWatchAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.watchedAnime.observe(viewLifecycleOwner) { animeList ->
            watchedAdapter.submitList(animeList)

            if (animeList.isEmpty() && viewModel.selectedTab.value == 0) {
                binding.emptyStateLayout.visible()
                binding.tvEmptyState.text = "ჯერ არ გაქვთ ნანახი anime"
            } else if (viewModel.selectedTab.value == 0) {
                binding.emptyStateLayout.gone()
            }
        }

        viewModel.planToWatchAnime.observe(viewLifecycleOwner) { animeList ->
            planToWatchAdapter.submitList(animeList)

            if (animeList.isEmpty() && viewModel.selectedTab.value == 1) {
                binding.emptyStateLayout.visible()
                binding.tvEmptyState.text = "ჯერ არ გაქვთ დაგეგმილი anime"
            } else if (viewModel.selectedTab.value == 1) {
                binding.emptyStateLayout.gone()
            }
        }

        viewModel.selectedTab.observe(viewLifecycleOwner) { tabIndex ->
            binding.rvWatched.visibility = if (tabIndex == 0) View.VISIBLE else View.GONE
            binding.rvPlanToWatch.visibility = if (tabIndex == 1) View.VISIBLE else View.GONE

            val currentList = if (tabIndex == 0) {
                viewModel.watchedAnime.value
            } else {
                viewModel.planToWatchAnime.value
            }

            if (currentList.isNullOrEmpty()) {
                binding.emptyStateLayout.visible()
                binding.tvEmptyState.text = if (tabIndex == 0) {
                    "ჯერ არ გაქვთ ნანახი anime"
                } else {
                    "ჯერ არ გაქვთ დაგეგმილი anime"
                }
            } else {
                binding.emptyStateLayout.gone()
            }
        }

        viewModel.actionMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                showSnackbar(it)
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
