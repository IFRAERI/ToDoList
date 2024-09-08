package aodintsov.to_do_list.domain.usecase.api

import aodintsov.to_do_list.data.api.OpenAIRepository
import aodintsov.to_do_list.data.model.OpenAIRequest
import aodintsov.to_do_list.data.model.OpenAIResponse
import javax.inject.Inject


class GetSubTasksUseCase @Inject constructor (private val openAIRepository: OpenAIRepository) {

    suspend operator fun invoke(request: OpenAIRequest): Result<OpenAIResponse> {
        return openAIRepository.getSubTasksForTask(request)
    }
}