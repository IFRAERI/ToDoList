package aodintsov.to_do_list.viewmodel

import android.util.Log
import androidx.lifecycle.*
import aodintsov.to_do_list.model.SubTask
import aodintsov.to_do_list.model.Task
import aodintsov.to_do_list.model.TaskRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: TaskRepository,
    private val authViewModel: AuthViewModel,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks
    private var allTasks: List<Task> = listOf()
    private val _isAscending = MutableLiveData<Boolean>(true)
    val isAscending: LiveData<Boolean> = _isAscending
    private var deferredTaskJob: Job? = null

    init {
        // Вместо setValue используем postValue
        _tasks.postValue(savedStateHandle.get("tasks") ?: emptyList())
        allTasks = _tasks.value ?: emptyList()
    }

    fun archiveTask(task: Task){
        val updatedTask = task.copy(archived = true)
        updateTask(updatedTask)
    }

    fun refreshTasks() {
        _tasks.postValue(allTasks)
    }

    fun unarchiveTask(task: Task){
        val updatedTask = task.copy(archived = false)
        updateTask(updatedTask)
    }

    fun filterTasks(showArchived: Boolean) {
        _tasks.postValue(
            if (showArchived) {
                allTasks.filter { it.archived }
            } else {
                allTasks.filter { !it.archived }
            }
        )
    }

    fun fetchTasks(userId: String) {
        if (userId.isNotEmpty()) {
            viewModelScope.launch {
                repository.getTasks(userId, onSuccess = { taskList ->
                    allTasks = taskList
                    _tasks.postValue(taskList)
                    savedStateHandle.set("tasks", taskList)
                }, onFailure = {
                    _tasks.postValue(emptyList())
                })
            }
        }
    }

    fun searchTasks(query: String) {
        _tasks.postValue(
            if (query.isEmpty()) {
                allTasks
            } else {
                allTasks.filter { task ->
                    task.title.contains(query, ignoreCase = true) ||
                            task.description.contains(query, ignoreCase = true)
                }
            }
        )
    }

    fun toggleSortOrder() {
        _isAscending.value = _isAscending.value?.not()
        sortTasks()
    }

    private fun sortTasks() {
        val ascending = _isAscending.value ?: true
        _tasks.postValue(
            if (ascending) {
                _tasks.value?.sortedBy { it.createdAt }
            } else {
                _tasks.value?.sortedByDescending { it.createdAt }
            }
        )
    }

    fun addTask(task: Task) {
        val userId = authViewModel.getCurrentUserId()
        if (userId != null) {
            val taskId = System.currentTimeMillis().toString()
            val newTask = task.copy(taskId = taskId, userId = userId)
            viewModelScope.launch {
                repository.addTask(newTask, onSuccess = {
                    fetchTasks(userId)
                }, onFailure = {
                    // Handle error
                })
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task, onSuccess = {
                fetchTasks(task.userId)
            }, onFailure = {
                // Handle error
            })
        }
    }

    fun deleteTask(taskId: String, userId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId, onSuccess = {
                fetchTasks(userId)
            }, onFailure = {
                // Handle error
            })
        }
    }

    fun getTaskById(taskId: String): Task? {
        return _tasks.value?.find { it.taskId == taskId }
    }

    fun updateSubTask(taskId: String, subTask: SubTask) {
        viewModelScope.launch {
            repository.updateSubTask(taskId, subTask, onSuccess = {
                val task = getTaskById(taskId)
                task?.let {
                    fetchTasks(it.userId)
                }
            }, onFailure = {
                // Handle error
            })
        }
    }

    fun fetchDeferredTasks(currentTime: Long) {
        val userId = authViewModel.getCurrentUserId()
        if (userId != null) {
            viewModelScope.launch {
                repository.getDeferredTasks(userId, currentTime, onSuccess = { taskList ->
                    allTasks = taskList
                    _tasks.postValue(taskList)
                }, onFailure = {
                    _tasks.postValue(emptyList())
                })
            }
        }
    }

    fun activateDeferredTask(taskId: String) {
        viewModelScope.launch {
            Log.d("TaskViewModel", "Начало активации отложенной задачи с ID: $taskId")
            repository.activateDeferredTask(taskId, onSuccess = {
                Log.d("TaskViewModel", "Задача успешно активирована: $taskId")
                fetchTasks(authViewModel.getCurrentUserId() ?: "")
            }, onFailure = { exception ->
                Log.e("TaskViewModel", "Ошибка активации задачи: $taskId", exception)
            })
        }
    }


//    fun startDeferredTaskChecker() {
//        if (deferredTaskJob == null || deferredTaskJob?.isActive == false) {
//            deferredTaskJob = viewModelScope.launch {
//                while (true) {
//                    checkAndActivateDeferredTasks()
//                    delay(3600000L)
//                }
//            }
//        }
//    }

    suspend fun checkAndActivateDeferredTasks() {
        val currentTime = System.currentTimeMillis()
     //   Log.d("TaskViewModel", "Начало проверки отложенных задач, текущее время: $currentTime")

        repository.getDeferredTasks(
            userId = authViewModel.getCurrentUserId() ?: "",
            currentTime = currentTime,
            onSuccess = { deferredTasks ->
               // Log.d("TaskViewModel", "Получено ${deferredTasks.size} отложенных задач для активации")

                deferredTasks.forEach { task ->
                    val activationTime = task.activationTime
                        //  Log.d("TaskViewModel", "Проверка задачи с ID: ${task.taskId}, activationTime: $activationTime")

                    if (activationTime != null && activationTime <= currentTime) {
                     //   Log.d("TaskViewModel", "Активация задачи с ID: ${task.taskId}")
                        activateDeferredTask(task.taskId)
                    } else {
                    //    Log.d("TaskViewModel", "Задача с ID: ${task.taskId} еще не готова к активации")
                    }
                }
            },
            onFailure = { exception ->
              //  Log.e("TaskViewModel", "Ошибка при получении отложенных задач: ${exception.message}")
            }
        )
    }



    override fun onCleared() {
        super.onCleared()
        deferredTaskJob?.cancel()
    }
}