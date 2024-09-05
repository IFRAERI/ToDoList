package aodintsov.to_do_list.domain.usecase.task
import aodintsov.to_do_list.data.model.SubTask
import aodintsov.to_do_list.domain.repository.TaskRepository
import javax.inject.Inject

class UpdateSubTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    fun execute(taskId: String, subTask: SubTask, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        taskRepository.updateSubTask(taskId, subTask, onSuccess, onFailure)
    }
}