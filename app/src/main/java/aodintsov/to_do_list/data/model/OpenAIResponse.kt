package aodintsov.to_do_list.data.model
data class OpenAIResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)
