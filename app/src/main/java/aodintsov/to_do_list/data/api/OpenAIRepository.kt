package aodintsov.to_do_list.data.api

import aodintsov.to_do_list.data.model.OpenAIRequest
import aodintsov.to_do_list.data.model.OpenAIResponse

interface OpenAIRepository {
    suspend fun getSubTasksForTask(request: OpenAIRequest): Result<OpenAIResponse>
}