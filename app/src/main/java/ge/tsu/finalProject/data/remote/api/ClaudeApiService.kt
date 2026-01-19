package ge.tsu.finalProject.data.remote.api;

import ge.tsu.finalProject.data.remote.dto.ClaudeRequestDto
import ge.tsu.finalProject.data.remote.dto.ClaudeResponseDto
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ClaudeApiService {

    @POST("v1/messages")
    suspend fun sendMessage(
        @Header("x-api-key") apiKey: String,
        @Header("anthropic-version") version: String = "2023-06-01",
        @Header("content-type") contentType: String = "application/json",
        @Body request: ClaudeRequestDto
    ): ClaudeResponseDto
}
