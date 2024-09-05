package aodintsov.to_do_list.domain.usecase.task
import aodintsov.to_do_list.data.model.Task
import javax.inject.Inject

class SearchTasksUseCase @Inject constructor() {
    fun execute(query: String, allTasks: List<Task>): List<Task> {
        return if (query.isEmpty()) {
            allTasks
        } else {
            allTasks.filter { task ->
                task.title.contains(query, ignoreCase = true) ||
                        task.description.contains(query, ignoreCase = true)
            }
        }
    }
}