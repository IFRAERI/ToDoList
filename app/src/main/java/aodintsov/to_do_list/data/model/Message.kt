package aodintsov.to_do_list.data.model


data class Message(
    val role: String, // Роль в диалоге (user, system, assistant)
    val content: String // Содержание сообщения
)

