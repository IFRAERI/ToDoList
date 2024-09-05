package aodintsov.to_do_list.domain.usecase.task

import aodintsov.to_do_list.domain.repository.TaskRepository
import javax.inject.Inject

class FetchCompletedTaskCountUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    fun execute(userId: String, onSuccess: (Int) -> Unit, onFailure: (Exception) -> Unit) {
        if (userId.isNotEmpty()) {
            taskRepository.getCompletedTaskCount(
                userId = userId,
                onSuccess = { count ->
                    onSuccess(count)
                },
                onFailure = { exception ->
                    onFailure(exception)
                }
            )
        }
    }
}
