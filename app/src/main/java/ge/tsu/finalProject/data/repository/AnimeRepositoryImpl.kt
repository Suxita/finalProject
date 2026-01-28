package ge.tsu.finalProject.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import ge.tsu.finalProject.data.local.dao.AnimeDao
import ge.tsu.finalProject.data.mapper.AnimeMapper
import ge.tsu.finalProject.data.remote.api.ClaudeApiService
import ge.tsu.finalProject.data.remote.api.JikanApiService
import ge.tsu.finalProject.data.remote.dto.ClaudeRequestDto
import ge.tsu.finalProject.data.remote.dto.MessageDto
import ge.tsu.finalProject.domain.model.*
import ge.tsu.finalProject.domain.repository.AnimeRepository
import ge.tsu.finalProject.util.NetworkHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AnimeRepositoryImpl(
    private val jikanApi: JikanApiService,
    private val claudeApi: ClaudeApiService,
    private val animeDao: AnimeDao,
    private val claudeApiKey: String,
    private val context: Context
) : AnimeRepository {

    // ==================== JIKAN API ====================

    override suspend fun getAnimeLibrary(page: Int): Result<List<Anime>> {
        return try {
            if (!NetworkHelper.isNetworkAvailable(context)) {
                return Result.failure(Exception("ინტერნეტ კავშირი არ არის ხელმისაწვდომი"))
            }

            val response = jikanApi.getTopAnime(page = page)
            Result.success(response.data.map { AnimeMapper.toDomain(it) })
        } catch (e: Exception) {
            Result.failure(Exception("დაფიქსირდა შეცდომა: ${e.message ?: "უცნობი შეცდომა"}"))
        }
    }

    override suspend fun searchAnime(query: String): Result<List<Anime>> {
        return try {
            if (!NetworkHelper.isNetworkAvailable(context)) {
                return Result.failure(Exception("ინტერნეტ კავშირი არ არის ხელმისაწვდომი"))
            }

            val response = jikanApi.searchAnime(query)
            Result.success(response.data.map { AnimeMapper.toDomain(it) })
        } catch (e: Exception) {
            Result.failure(Exception("ძიება ვერ მოხერხდა: ${e.message ?: "უცნობი შეცდომა"}"))
        }
    }

    override suspend fun getAnimeDetails(id: Int): Result<Anime> {
        return try {
            if (!NetworkHelper.isNetworkAvailable(context)) {
                return Result.failure(Exception("ინტერნეტ კავშირი არ არის ხელმისაწვდომი"))
            }

            val response = jikanApi.getAnimeById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(AnimeMapper.toDomain(response.body()!!.data))
            } else {
                Result.failure(Exception("ვერ მოიძებნა anime დეტალები"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("შეცდომა დეტალების ჩატვირთვისას: ${e.message}"))
        }
    }

    // ==================== LOCAL DATABASE ====================

    override suspend fun saveAnime(anime: Anime, status: WatchStatus) {
        val entity = AnimeMapper.toEntity(anime, status)
        animeDao.insertAnime(entity)
    }

    override suspend fun updateWatchStatus(animeId: Int, status: WatchStatus) {
        animeDao.updateWatchStatus(animeId, status.name)
    }

    override suspend fun updateLikeStatus(animeId: Int, isLiked: Boolean?) {
        animeDao.updateLikeStatus(animeId, isLiked)
    }

    override suspend fun deleteAnime(animeId: Int) {
        animeDao.deleteAnime(animeId)
    }

    override fun getWatchedAnime(): Flow<List<SavedAnime>> =
        animeDao.getWatchedAnime().map { entities ->
            entities.map { AnimeMapper.toDomain(it) }
        }

    override fun getPlanToWatchAnime(): Flow<List<SavedAnime>> =
        animeDao.getPlanToWatchAnime().map { entities ->
            entities.map { AnimeMapper.toDomain(it) }
        }

    override fun getAllSavedAnime(): Flow<List<SavedAnime>> =
        animeDao.getAllSavedAnime().map { entities ->
            entities.map { AnimeMapper.toDomain(it) }
        }

    // ==================== CLAUDE AI ====================

    override suspend fun analyzeTaste(watchedAnime: List<SavedAnime>): Result<TasteAnalysis> {
        return try {
            if (!NetworkHelper.isNetworkAvailable(context)) {
                return Result.failure(Exception("ინტერნეტ კავშირი არ არის ხელმისაწვდომი"))
            }

            val likedAnime = watchedAnime.filter { it.isLiked == true }
            val dislikedAnime = watchedAnime.filter { it.isLiked == false }

            if (likedAnime.isEmpty() && dislikedAnime.isEmpty()) {
                Result.failure(Exception("გთხოვთ მონიშნოთ რამელი anime მოგწონთ/არ მოგწონთ"))
            } else {
                val prompt = buildTasteAnalysisPrompt(likedAnime, dislikedAnime)
                val response = claudeApi.sendMessage(
                    apiKey = claudeApiKey,
                    request = ClaudeRequestDto(messages = listOf(MessageDto(role = "user", content = prompt)))
                )
                val analysisText = response.content.firstOrNull()?.text
                    ?: throw Exception("Claude API-მ ცარიელი პასუხი დააბრუნა")
                Result.success(parseTasteAnalysis(analysisText))
            }
        } catch (e: JsonSyntaxException) {
            Result.failure(Exception("AI პასუხის დამუშავება ვერ მოხერხდა"))
        } catch (e: Exception) {
            Result.failure(Exception("შეფასება ვერ მოხერხდა: ${e.message ?: "უცნობი შეცდომა"}"))
        }
    }

    override suspend fun getDailyRecommendation(watchedAnime: List<SavedAnime>): Result<DailyRecommendation> {
        return try {
            if (!NetworkHelper.isNetworkAvailable(context)) {
                return Result.failure(Exception("ინტერნეტ კავშირი არ არის ხელმისაწვდომი"))
            }

            val likedAnime = watchedAnime.filter { it.isLiked == true }
            if (likedAnime.isEmpty()) {
                Result.failure(Exception("გთხოვთ მონიშნოთ რამელი anime მოგწონთ"))
            } else {
                val prompt = buildRecommendationPrompt(likedAnime)
                val response = claudeApi.sendMessage(
                    apiKey = claudeApiKey,
                    request = ClaudeRequestDto(messages = listOf(MessageDto(role = "user", content = prompt)))
                )
                val recommendationText = response.content.firstOrNull()?.text
                    ?: throw Exception("Claude API-მ ცარიელი პასუხი დააბრუნა")
                Result.success(parseDailyRecommendation(recommendationText))
            }
        } catch (e: JsonSyntaxException) {
            Result.failure(Exception("AI პასუხის დამუშავება ვერ მოხერხდა"))
        } catch (e: Exception) {
            Result.failure(Exception("რეკომენდაცია ვერ მოიძებნა: ${e.message ?: "უცნობი შეცდომა"}"))
        }
    }

    // ==================== HELPERS ====================
    private fun buildTasteAnalysisPrompt(liked: List<SavedAnime>, disliked: List<SavedAnime>): String {
        val likedTitles = liked.joinToString("\n") { "- ${it.anime.title} (${it.anime.genres.joinToString()})" }
        val dislikedTitles = disliked.joinToString("\n") { "- ${it.anime.title}" }

        return """
        მოდი გავაანალიზოთ ეს "უნიკალური" გემოვნება 💀
        
        აი რა არჩია ამ ადამიანმა (უკვე საეჭვოა):
        $likedTitles
        
        და აი რაც არ მოეწონა (ალბათ კარგი მიზეზი აქვს... ან არა):
        $dislikedTitles
        
        შენი ამოცანაა:
        1) გააანალიზე ეს გემოვნება Reddit-style roast-ით - თუ კლასიკაა იუმორით შეაქე, თუ mainstream-ია დაცინე basic taste-ზე, თუ ნიშე-ცხოვრებაა respect-ი გამოუცხადე
        2) გაითამაშე asociations - "ეს anime მოგწონს? მაშინ ალბათ..." style
        3) თუ Reddit-ზე ამ taste-ს იჩხუბებდნენ - აუცილებლად აღნიშნე 😂
        4) მაგრამ ბოლოს მაინც გულწრფელად დაეხმარე და მიეცი კარგი რეკომენდაციები
        
        გამოიყენე ქართული ჟარგონი, ინტერნეტ სლენგი, და იყავი კომედიური მაგრამ არა უხეში.
        
        პასუხი დააბრუნე ქართულად JSON ფორმატში:
        {
            "roast": "იუმორით სავსე analysis - აქ დააჩხუბე გემოვნებას, მაგრამ სიყვარულით ❤️",
            "analysis": "რეალური ანალიზი - რა პატერნები ჩანს, რა არის საერთო",
            "favoriteGenres": ["ჟანრი1", "ჟანრი2"],
            "redditWouldSay": "რას იტყოდნენ r/anime-ზე ამის შესახებ",
            "vibe": "ერთი წინადადებით - რა vibe-ია ეს გემოვნება",
            "recommendations": [
                "anime სახელი 1 - რატომ",
                "anime სახელი 2 - რატომ"
            ]
        }
        
        მაგალითები როგორ უნდა იყოს roast:
        - "Attack on Titan მოგწონს? ძმაო ყველას მოსწონს, ეს არ არის personality trait 💀"
        - "Death Note fan? კლასიკური 'ვფიქრობ რომ ჭკვიანი ვარ' starter pack"
        - "Monster შენი ფავორიტია? respect bro, ნამდვილი taste გაქვს 🔥"
        - "One Piece არ გიყვარს? 1000+ episode-ს შეშინდი ხო? ვესმი, commitment issues გაქვს"
        
        იყავი კრეატიული, იუმორული და მეგობრული!
    """.trimIndent()
    }

    private fun buildRecommendationPrompt(liked: List<SavedAnime>): String {
        val titles = liked.joinToString("\n") { "- ${it.anime.title} (${it.anime.genres.joinToString()})" }

        return """
        დღის რეკომენდაცია incoming... 🎯
        
        ეს არის მოწონებული anime-ები:
        $titles
        
        შენი მისია:
        1) იპოვე ერთი anime რომელიც ᲘᲓᲔᲐᲚᲣᲠᲐᲓ ემთხვევა ამ taste-ს
        2) იყავი ორიგინალური - არა "ნაუტოს მოგწონს? უყურე ბლიჩს" basic რეკომენდაცია
        3) ახსენი რატომ სწორედ ეს anime იუმორით და ასოციაციებით
        4) თუ რეკომენდაცია controversial-ია - აუცილებლად აღნიშნე და დაასაბუთე
        5) გამოიყენე ინტერნეტ სლენგი და იყავი personality სავსე
        
        პასუხი JSON ფორმატში:
        {
            "animeTitle": "anime-ს სახელი",
            "reason": "გრძელი და იუმორით სავსე ახსნა - რატომ სწორედ ეს? რა ასოციაცია გაქვს? roast-ი user-ის თუ anime-ს? 😄",
            "matchPercentage": 85,
            "vibe": "ერთი წინადადებით - რა vibe გადმოსცემს ეს anime",
            "hotTake": "controversial მოსაზრება ამ რეკომენდაციაზე",
            "redditThread": "წარმოიდგინე r/anime-ზე thread-ის სათაური ამ რეკომენდაციაზე"
        }
        
        მაგალითები tone-ისთვის:
        - "სტილი: Monster-ის fan ხარ? მაშინ Steins;Gate-ც გეგულება - ორივეში ტვინი მუშაობს ხოლმე (იშვიათობაა anime-ში)"
        - "Jujutsu Kaisen-ი გიყვარს მაგრამ Bleach-ს არ უყურებ? ძმაო, მაშ საიდან იცი საიდან მოდის ეს animation style? 💀 გადადი წყაროზე"
        - "Cowboy Bebop არის შენი #1? კლასიკური 'მე განსხვავებული ვარ' taste, მაგრამ respect - სტილი გაქვს fr fr"
        
        იყავი კრეატიული, დამაჯერებელი და უყურო რომ მართლა დაეხმარო!
    """.trimIndent()
    }

    private fun parseTasteAnalysis(json: String): TasteAnalysis {
        try {
            val cleanJson = json.removeSurrounding("```json", "```").trim()
            val jsonObject = Gson().fromJson(cleanJson, JsonObject::class.java)

            return TasteAnalysis(
                analysis = jsonObject.get("analysis")?.asString ?: "შეფასება ვერ მოხერხდა",
                favoriteGenres = jsonObject.getAsJsonArray("favoriteGenres")?.map { it.asString } ?: emptyList(),
                recommendations = jsonObject.getAsJsonArray("recommendations")?.map { it.asString } ?: emptyList()
            )
        } catch (e: Exception) {
            throw JsonSyntaxException("Invalid JSON response from Claude API")
        }
    }

    private fun parseDailyRecommendation(json: String): DailyRecommendation {
        try {
            val cleanJson = json.removeSurrounding("```json", "```").trim()
            val jsonObject = Gson().fromJson(cleanJson, JsonObject::class.java)

            return DailyRecommendation(
                animeTitle = jsonObject.get("animeTitle")?.asString ?: "უცნობი",
                reason = jsonObject.get("reason")?.asString ?: "მიზეზი არ არის მითითებული",
                matchPercentage = jsonObject.get("matchPercentage")?.asInt ?: 0
            )
        } catch (e: Exception) {
            throw JsonSyntaxException("Invalid JSON response from Claude API")
        }
    }
}