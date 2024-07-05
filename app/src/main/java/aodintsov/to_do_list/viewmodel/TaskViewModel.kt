package aodintsov.to_do_list.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aodintsov.to_do_list.model.Task
import aodintsov.to_do_list.model.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: TaskRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks

    init {
        // Restore state
        _tasks.value = savedStateHandle.get("tasks") ?: emptyList()
    }

    fun fetchTasks(userId: String) {
        viewModelScope.launch {
            repository.getTasks(userId, onSuccess = { taskList ->
                _tasks.value = taskList
                Log.d("TaskViewModel", "Fetched tasks: ${taskList.map { it.taskId }}")
                savedStateHandle.set("tasks", taskList)
            }, onFailure = {
                _tasks.value = emptyList() // Set empty list on failure
                Log.d("TaskViewModel", "Failed to fetch tasks")
            })
        }
    }

    fun deleteAllTasks() {
        viewModelScope.launch {
            repository.deleteAllTasks(onSuccess = {
                _tasks.value = emptyList()
                savedStateHandle.set("tasks", emptyList<Task>())
            }, onFailure = {
                // Handle error
            })
        }
    }

    fun addTask(task: Task) {
        val taskId = System.currentTimeMillis().toString() // Use timestamp as taskId
        val newTask = task.copy(taskId = taskId)
        viewModelScope.launch {
            repository.addTask(newTask, onSuccess = {
                fetchTasks(task.userId)
            }, onFailure = {
                // Handle error
            })
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task, onSuccess = {
                fetchTasks(task.userId)
                Log.d("TaskViewModel", "Updated task with ID: ${task.taskId}")
            }, onFailure = {
                // Handle error
            })
        }
    }

    fun deleteTask(taskId: String, userId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId, onSuccess = {
                fetchTasks(userId)
                Log.d("TaskViewModel", "Deleted task with ID: $taskId")
            }, onFailure = {
                // Handle error
            })
        }
    }

    fun getTaskById(taskId: String): Task? {
        val task = _tasks.value?.find { it.taskId == taskId }
        Log.d("TaskViewModel", "getTaskById($taskId): $task")
        return task
    }
}
