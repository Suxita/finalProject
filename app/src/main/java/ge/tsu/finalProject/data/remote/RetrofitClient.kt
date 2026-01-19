package ge.tsu.finalProject.data.remote

import ge.tsu.finalProject.data.remote.api.ClaudeApiService
import ge.tsu.finalProject.data.remote.api.JikanApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val JIKAN_BASE_URL = "https://api.jikan.moe/"
    private const val CLAUDE_BASE_URL = "https://api.anthropic.com/"

    private const val DEBUG = true

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val jikanApi: JikanApiService by lazy {
        createRetrofit(JIKAN_BASE_URL).create(JikanApiService::class.java)
    }

    val claudeApi: ClaudeApiService by lazy {
        createRetrofit(CLAUDE_BASE_URL).create(ClaudeApiService::class.java)
    }
}
