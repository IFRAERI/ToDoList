package aodintsov.to_do_list.viewmodel

import android.util.Log
import androidx.lifecycle.*
import aodintsov.to_do_list.model.SubTask
import aodintsov.to_do_list.model.Task
import aodintsov.to_do_list.model.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: TaskRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks
    private var allTasks: List<Task> = listOf()
    private val _isAscending = MutableLiveData<Boolean>(true)
    val isAscending: LiveData<Boolean> = _isAscending

    init {
        // Restore state
        _tasks.value = savedStateHandle.get("tasks") ?: emptyList()
        allTasks = _tasks.value ?: emptyList()
        Log.d("TaskViewModel", "Initial allTasks: $allTasks")
    }

    fun fetchTasks(userId: String) {
        viewModelScope.launch {
            repository.getTasks(userId, onSuccess = { taskList ->
                allTasks = taskList
                _tasks.value = taskList
                Log.d("TaskViewModel", "Fetched tasks: ${taskList.map { it.taskId }}")
                Log.d("TaskViewModel", "All tasks after fetch: $allTasks")
                savedStateHandle.set("tasks", taskList)
            }, onFailure = {
                _tasks.value = emptyList() // Set empty list on failure
                Log.d("TaskViewModel", "Failed to fetch tasks")
            })
        }
    }

    fun searchTasks(query: String) {
        Log.d("TaskViewModel", "searchTasks called with query: $query")
        Log.d("TaskViewModel", "All tasks before filtering: $allTasks")

        val filteredTasks = if (query.isEmpty()) {
            allTasks
        } else {
            allTasks.filter { task ->
                task.title.contains(query, ignoreCase = true) ||
                        task.description.contains(query, ignoreCase = true)
            }
        }

        Log.d("TaskViewModel", "Filtered tasks before setting: $filteredTasks")
        _tasks.value = filteredTasks
        Log.d("TaskViewModel", "Filtered tasks after setting: ${_tasks.value}")
    }
    fun toggleSortOrder() {
        _isAscending.value = _isAscending.value?.not()
        sortTasks()
    }

    private fun sortTasks() {
        val ascending = _isAscending.value ?: true
        _tasks.value = if (ascending) {
            _tasks.value?.sortedBy { it.dueDate }
        } else {
            _tasks.value?.sortedByDescending { it.dueDate }
        }
    }

    fun loadTasks() {
        // Load tasks from repository
        // Example: _tasks.value = repository.getTasks()
        sortTasks()
    }

    fun deleteAllTasks(userId: String) {
        viewModelScope.launch {
            repository.deleteAllTasks(userId, onSuccess = {
                _tasks.value = emptyList()
                allTasks = emptyList()
                savedStateHandle.set("tasks", emptyList<Task>())
                Log.d("TaskViewModel", "All tasks after delete: $allTasks")
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
                Log.d("TaskViewModel", "Task added successfully: $newTask")
                fetchTasks(task.userId)
            }, onFailure = {
                Log.e("TaskViewModel", "Failed to add task", it)
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

    fun assignTaskToUser(taskId: String, assignedTo: String) {
        viewModelScope.launch {
            repository.assignTaskToUser(taskId, assignedTo, onSuccess = {
                // Refresh tasks to reflect assignment
                fetchTasks(assignedTo)
            }, onFailure = {
                // Handle error
            })
        }
    }

    fun updateSubTask(taskId: String, subTask: SubTask) {
        viewModelScope.launch {
            repository.updateSubTask(taskId, subTask, onSuccess = {
                // Refresh tasks to reflect sub-task update
                val task = getTaskById(taskId)
                task?.let {
                    fetchTasks(it.userId)
                }
            }, onFailure = {
                // Handle error
            })
        }
    }

    fun getAssignedTasks(userId: String) {
        viewModelScope.launch {
            repository.getAssignedTasks(userId, onSuccess = { taskList ->
                allTasks = taskList
                _tasks.value = taskList
                Log.d("TaskViewModel", "Fetched assigned tasks: ${taskList.map { it.taskId }}")
                savedStateHandle.set("tasks", taskList)
            }, onFailure = {
                _tasks.value = emptyList() // Set empty list on failure
                Log.d("TaskViewModel", "Failed to fetch assigned tasks")
            })
        }
    }
}
