package aodintsov.to_do_list.domain.usecase.task
import aodintsov.to_do_list.domain.repository.TaskRepository
import javax.inject.Inject

class CheckAndActivateDeferredTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val activateDeferredTaskUseCase: ActivateDeferredTaskUseCase
) {
    suspend fun execute(userId: String, currentTime: Long, onFailure: (Exception) -> Unit) {
        taskRepository.getDeferredTasks(
            userId = userId,
            currentTime = currentTime,
            onSuccess = { deferredTasks ->
                deferredTasks.forEach { task ->
                    val activationTime = task.activationTime
                    if (activationTime != null && activationTime <= currentTime) {
                        activateDeferredTaskUseCase.execute(task.taskId, {}, onFailure)
                    }
                }
            },
            onFailure = { exception ->
                onFailure(exception)
            }
        )
    }
}