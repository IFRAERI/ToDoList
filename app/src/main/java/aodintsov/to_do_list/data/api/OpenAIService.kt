package aodintsov.to_do_list.data.api
import aodintsov.to_do_list.data.model.OpenAIRequest
import aodintsov.to_do_list.data.model.OpenAIResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIService {

    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun getSubTasksForTask(
        @Header("Authorization") apiKey: String, // Передаем API-ключ
        @Body request: OpenAIRequest
    ): OpenAIResponse
}