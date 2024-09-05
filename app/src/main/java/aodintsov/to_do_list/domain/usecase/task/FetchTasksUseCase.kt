package aodintsov.to_do_list.domain.usecase.task

import aodintsov.to_do_list.data.model.Task
import aodintsov.to_do_list.domain.repository.TaskRepository
import javax.inject.Inject

class FetchTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    fun execute(userId: String, onSuccess: (List<Task>) -> Unit, onFailure: (Exception) -> Unit) {
        taskRepository.getTasks(userId, onSuccess, onFailure)
    }
}