package aodintsov.to_do_list.domain.repository

import aodintsov.to_do_list.data.model.SubTask
import aodintsov.to_do_list.data.model.Task

interface TaskRepository {
    fun getTasks(userId: String, onSuccess: (List<Task>) -> Unit, onFailure: (Exception) -> Unit)
    fun addTask(task: Task, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun updateTask(task: Task, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun deleteTask(taskId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
   // fun deleteAllTasks(userId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

    // Дополнительные методы для работы с подзадачами и делегированием задач
    fun getAssignedTasks(userId: String, onSuccess: (List<Task>) -> Unit, onFailure: (Exception) -> Unit)
    fun updateSubTask(taskId: String, subTask: SubTask, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun assignTaskToUser(taskId: String, assignedTo: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun getDeferredTasks(userId: String, currentTime: Long, onSuccess: (List<Task>) -> Unit, onFailure: (Exception) -> Unit)
    fun activateDeferredTask(taskId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

       // Метод для получения количества завершённых задач
    fun getCompletedTaskCount(userId: String, onSuccess: (Int) -> Unit, onFailure: (Exception) -> Unit)
}
