package aodintsov.to_do_list.domain.usecase.task


import aodintsov.to_do_list.domain.repository.TaskRepository
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    fun execute(taskId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        taskRepository.deleteTask(taskId, onSuccess, onFailure)
    }
}