package ge.tsu.finalProject.domain.model;
data class TasteAnalysis(
    val analysis: String,
    val favoriteGenres: List<String>,
    val recommendations: List<String>
)

data class DailyRecommendation(
    val animeTitle: String,
    val reason: String,
    val matchPercentage: Int
)
