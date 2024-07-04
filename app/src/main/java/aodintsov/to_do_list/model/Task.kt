package aodintsov.to_do_list.model

data class Task(
    var taskId: String = "",
    val title: String = "",
    val description: String = "",
    val priority: String = "",
    val dueDate: String = "",
    val completed: Boolean = false,
    val userId: String = ""
)
