package aodintsov.to_do_list.model

data class SubTask(
    var subTaskId: String = "",
    val title: String = "",
    val description: String = "",
    val completed: Boolean = false
)
