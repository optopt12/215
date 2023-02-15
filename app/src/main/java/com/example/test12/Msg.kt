package com.example.test12
import com.google.gson.annotations.SerializedName
val SPEECH_REQUEST_CODE = 0
val requestcode = 0
//定义消息的实体类
const val MY_API_KEY ="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"
data class Msg(val content: String, val type: Int) {
    //定义静态成员
    companion object {
        const val RIGHT = 0
        const val LEFT = 1
    }
}
data class CompletionResponse(
    @SerializedName("id") val id: String,
    @SerializedName("choices") val choices: List<Choices>
)
data class CompletionRequest(
    val model: String,
    val prompt: String,
    val temperature: Double,
    val max_tokens: Int,
    val top_p: Int,
    val frequency_penalty: Double,
    val presence_penalty: Double,
    val stop: List<String>
)
data class Choices(
    @SerializedName("text") val text: String,
    @SerializedName("index") val index: Int,
    @SerializedName("logprobs") val logprobs: String,
    @SerializedName("finish_reason") val finishReason: String
)
