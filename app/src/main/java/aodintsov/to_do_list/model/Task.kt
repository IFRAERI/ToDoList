package aodintsov.to_do_list.model

data class Task(
    var taskId: String = "",
    val title: String = "",
    val description: String = "",
    val priority: Boolean = false,
    val dueDate: Long? = null, // Изменено на Long для хранения timestamp
    val completed: Boolean = false,
    val userId: String = "",
    val reminderTime: Long? = null,
    var assignedTo: String = "", // Добавлено поле для делегирования задач
    var delegatedBy: String = "", // Добавлено поле для хранения информации о том, от кого задача была делегирована
    var subTasks: List<SubTask> = listOf(), // Добавлено поле для подзадач
    val createdAt: Long = System.currentTimeMillis(), // Добавлено поле для хранения даты создания задачи
    var completionDate: Long? = null,
    var archived: Boolean = false // Добавлено поле для записи задачи в архив
   // val createdAt: Long = System.currentTimeMillis()
)
