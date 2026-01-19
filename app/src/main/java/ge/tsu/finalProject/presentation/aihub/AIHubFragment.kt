package ge.tsu.finalProject.presentation.aihub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import ge.tsu.finalProject.databinding.FragmentAiHubBinding
import ge.tsu.finalProject.domain.model.DailyRecommendation
import ge.tsu.finalProject.domain.model.TasteAnalysis
import ge.tsu.finalProject.presentation.common.ViewState
import ge.tsu.finalProject.util.gone
import ge.tsu.finalProject.util.visible

class AIHubFragment : Fragment() {

    private var _binding: FragmentAiHubBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AIHubViewModel by viewModels { AIHubViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAiHubBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        // Taste Analysis Button
        binding.btnAnalyzeTaste.setOnClickListener {
            viewModel.analyzeTaste()
        }

        // Daily Recommendation Button
        binding.btnDailyRecommendation.setOnClickListener {
            viewModel.getDailyRecommendation()
        }

        // Reset buttons
        binding.btnResetTaste.setOnClickListener {
            viewModel.resetTasteAnalysis()
            binding.tasteResultCard.gone()
        }

        binding.btnResetRecommendation.setOnClickListener {
            viewModel.resetDailyRecommendation()
            binding.recommendationCard.gone()
        }
    }

    private fun observeViewModel() {
        // Taste Analysis
        viewModel.tasteAnalysis.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ViewState.Idle -> {
                    binding.progressBarTaste.gone()
                    binding.tasteResultCard.gone()
                    binding.btnAnalyzeTaste.isEnabled = true
                }
                is ViewState.Loading -> {
                    binding.progressBarTaste.visible()
                    binding.tasteResultCard.gone()
                    binding.btnAnalyzeTaste.isEnabled = false
                }
                is ViewState.Success -> {
                    binding.progressBarTaste.gone()
                    binding.tasteResultCard.visible()
                    binding.btnAnalyzeTaste.isEnabled = true

                    displayTasteAnalysis(state.data)
                }
                is ViewState.Error -> {
                    binding.progressBarTaste.gone()
                    binding.btnAnalyzeTaste.isEnabled = true
                    showSnackbar(state.message)
                }
            }
        }

        // Daily Recommendation
        viewModel.dailyRecommendation.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ViewState.Idle -> {
                    binding.progressBarRecommendation.gone()
                    binding.recommendationCard.gone()
                    binding.btnDailyRecommendation.isEnabled = true
                }
                is ViewState.Loading -> {
                    binding.progressBarRecommendation.visible()
                    binding.recommendationCard.gone()
                    binding.btnDailyRecommendation.isEnabled = false
                }
                is ViewState.Success -> {
                    binding.progressBarRecommendation.gone()
                    binding.recommendationCard.visible()
                    binding.btnDailyRecommendation.isEnabled = true

                    displayRecommendation(state.data)
                }
                is ViewState.Error -> {
                    binding.progressBarRecommendation.gone()
                    binding.btnDailyRecommendation.isEnabled = true
                    showSnackbar(state.message)
                }
            }
        }

        // Saved Anime Count
        viewModel.allSavedAnime.observe(viewLifecycleOwner) { savedAnime ->
            val likedCount = savedAnime.count { it.isLiked == true }
            val totalCount = savedAnime.size

            binding.tvAnimeCount.text = "თქვენ გაქვთ $totalCount შენახული anime ($likedCount მოწონებული)"
        }
    }

    private fun displayTasteAnalysis(analysis: TasteAnalysis) {
        binding.apply {
            tvAnalysis.text = analysis.analysis
            tvFavoriteGenres.text = "საყვარელი ჟანრები: ${analysis.favoriteGenres.joinToString(", ")}"

            val recommendationsText = analysis.recommendations
                .mapIndexed { index, rec -> "${index + 1}. $rec" }
                .joinToString("\n\n")
            tvRecommendations.text = "რეკომენდაციები:\n\n$recommendationsText"
        }
    }

    private fun displayRecommendation(recommendation: DailyRecommendation) {
        binding.apply {
            tvAnimeTitle.text = recommendation.animeTitle
            tvReason.text = recommendation.reason
            tvMatchPercentage.text = "${recommendation.matchPercentage}% შესაბამისობა"
            progressMatch.progress = recommendation.matchPercentage
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
