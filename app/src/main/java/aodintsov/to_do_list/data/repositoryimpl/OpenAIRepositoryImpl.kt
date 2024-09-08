package aodintsov.to_do_list.data.repositoryimpl


import android.util.Log
import aodintsov.to_do_list.data.api.OpenAIRepository
import aodintsov.to_do_list.data.model.OpenAIRequest
import aodintsov.to_do_list.data.model.OpenAIResponse
import aodintsov.to_do_list.data.api.OpenAIService
import retrofit2.HttpException

class OpenAIRepositoryImpl(
    private val openAIService: OpenAIService,
    private val apiKey: String
) : OpenAIRepository {

    override suspend fun getSubTasksForTask(request: OpenAIRequest): Result<OpenAIResponse> {
        return try {
            Log.d(
                "OpenAIRepositoryImpl",
                "Отправляем запрос с API ключом: $apiKey"
            ) // Логируем API-ключ
            val response = openAIService.getSubTasksForTask("Bearer $apiKey", request)
            Result.success(response)
        } catch (e: HttpException) {
            Log.e("OpenAIRepositoryImpl", "HTTP ошибка: ${e.code()} - ${e.message()}")
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("OpenAIRepositoryImpl", "Ошибка: ${e.message}")
            Result.failure(e)
        }
    }
}