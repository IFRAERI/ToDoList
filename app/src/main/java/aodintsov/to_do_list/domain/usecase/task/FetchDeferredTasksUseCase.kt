package aodintsov.to_do_list.domain.usecase.task

import aodintsov.to_do_list.data.model.Task
import aodintsov.to_do_list.domain.repository.TaskRepository
import javax.inject.Inject

class FetchDeferredTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    fun execute(userId: String, currentTime: Long, onSuccess: (List<Task>) -> Unit, onFailure: (Exception) -> Unit) {
        taskRepository.getDeferredTasks(userId, currentTime, onSuccess, onFailure)
    }
}