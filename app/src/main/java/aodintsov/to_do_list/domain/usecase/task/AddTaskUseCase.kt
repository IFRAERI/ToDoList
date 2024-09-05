package aodintsov.to_do_list.domain.usecase.task
import aodintsov.to_do_list.domain.repository.TaskRepository
import aodintsov.to_do_list.data.model.Task
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    fun execute(task: Task, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        taskRepository.addTask(task, onSuccess, onFailure)
    }
}