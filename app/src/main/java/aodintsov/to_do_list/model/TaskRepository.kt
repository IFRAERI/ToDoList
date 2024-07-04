package aodintsov.to_do_list.model

interface TaskRepository {
    fun getTasks(userId: String, onSuccess: (List<Task>) -> Unit, onFailure: (Exception) -> Unit)
    fun addTask(task: Task, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun updateTask(task: Task, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun deleteTask(taskId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun deleteAllTasks(onSuccess: () -> Unit, onFailure: (Throwable) -> Unit)

}
