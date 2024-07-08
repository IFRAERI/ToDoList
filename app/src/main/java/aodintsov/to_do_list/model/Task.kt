package aodintsov.to_do_list.model

data class Task(
    var taskId: String = "",
    val title: String = "",
    val description: String = "",
    val priority: String = "",
    val dueDate: Long? = null, // Изменено на Long для хранения timestamp
    val completed: Boolean = false,
    val userId: String = "",
    var assignedTo: String = "", // Добавлено поле для делегирования задач
    var delegatedBy: String = "", // Добавлено поле для хранения информации о том, от кого задача была делегирована
    var subTasks: List<SubTask> = listOf() // Добавлено поле для подзадач
)
