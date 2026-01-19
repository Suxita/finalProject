package ge.tsu.finalProject.data.remote.dto;
import com.google.gson.annotations.SerializedName

data class ClaudeRequestDto(
    @SerializedName("model") val model: String = "claude-sonnet-4-20250514",
    @SerializedName("max_tokens") val maxTokens: Int = 2048,
    @SerializedName("messages") val messages: List<MessageDto>
)

data class MessageDto(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String
)

data class ClaudeResponseDto(
    @SerializedName("content") val content: List<ContentDto>,
    @SerializedName("model") val model: String,
    @SerializedName("stop_reason") val stopReason: String,
    @SerializedName("usage") val usage: UsageDto?
)

data class ContentDto(
    @SerializedName("type") val type: String,
    @SerializedName("text") val text: String
)

data class UsageDto(
    @SerializedName("input_tokens") val inputTokens: Int,
    @SerializedName("output_tokens") val outputTokens: Int
)
