package aodintsov.to_do_list.data.model

data class OpenAIRequest(val model: String, // Модель для работы с GPT, например, gpt-3.5-turbo
                         val messages: List<Message>, // Сообщения, содержащие запрос
                         val max_tokens: Int = 500, // Максимальное количество токенов для ответа
                         val temperature: Float = 0.7f // Температура генерации (для вариации ответов)
)

