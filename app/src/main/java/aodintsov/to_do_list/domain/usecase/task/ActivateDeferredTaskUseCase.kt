package aodintsov.to_do_list.domain.usecase.task


import aodintsov.to_do_list.domain.repository.TaskRepository
import javax.inject.Inject

class ActivateDeferredTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    fun execute(taskId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        taskRepository.activateDeferredTask(taskId, onSuccess, onFailure)
    }
}