package aodintsov.to_do_list.model

data class User(
    var userId: String = "",
    val email: String = "",
    val name: String = "",
    var points: Int = 0, // Общее количество очков
    var lastLoginTime: Long = 0L, // Время последнего входа
    var completedTasksCount: Int = 0 // Количество выполненных задач
)