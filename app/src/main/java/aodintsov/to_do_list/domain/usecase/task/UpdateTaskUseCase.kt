package aodintsov.to_do_list.domain.usecase.task

import aodintsov.to_do_list.data.model.Task
import aodintsov.to_do_list.domain.repository.TaskRepository
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    fun execute(task: Task, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        taskRepository.updateTask(task, onSuccess, onFailure)
    }
}