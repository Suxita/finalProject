package ge.tsu.finalProject.data.remote.api;
import ge.tsu.finalProject.data.remote.dto.AnimeDetailResponseDto
import ge.tsu.finalProject.data.remote.dto.AnimeResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JikanApiService {

    @GET("v4/top/anime")
    suspend fun getTopAnime(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 25
    ): AnimeResponseDto

    @GET("v4/anime")
    suspend fun searchAnime(
        @Query("q") query: String,
        @Query("limit") limit: Int = 25,
        @Query("page") page: Int = 1
    ): AnimeResponseDto

    @GET("v4/anime/{id}")
    suspend fun getAnimeById(
        @Path("id") id: Int
    ): Response<AnimeDetailResponseDto>

}
