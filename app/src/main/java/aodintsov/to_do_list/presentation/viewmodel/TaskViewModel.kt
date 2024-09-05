package aodintsov.to_do_list.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.*
import aodintsov.to_do_list.data.model.SubTask
import aodintsov.to_do_list.data.model.Task
import aodintsov.to_do_list.domain.repository.TaskRepository
import aodintsov.to_do_list.domain.usecase.task.ActivateDeferredTaskUseCase
//import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
//import kotlinx.coroutines.launch
import javax.inject.Inject
import aodintsov.to_do_list.domain.usecase.task.AddTaskUseCase
import aodintsov.to_do_list.domain.usecase.task.CheckAndActivateDeferredTasksUseCase
import aodintsov.to_do_list.domain.usecase.task.DeleteTaskUseCase
import aodintsov.to_do_list.domain.usecase.task.FetchCompletedTaskCountUseCase
import aodintsov.to_do_list.domain.usecase.task.FetchDeferredTasksUseCase
import aodintsov.to_do_list.domain.usecase.task.FetchTasksUseCase
import aodintsov.to_do_list.domain.usecase.task.SearchTasksUseCase
import aodintsov.to_do_list.domain.usecase.task.UpdateSubTaskUseCase
import aodintsov.to_do_list.domain.usecase.task.UpdateTaskUseCase

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val savedStateHandle: SavedStateHandle,
    private val fetchTasksUseCase: FetchTasksUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val searchTasksUseCase: SearchTasksUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val updateSubTaskUseCase: UpdateSubTaskUseCase,
    private val fetchDeferredTasksUseCase: FetchDeferredTasksUseCase,
    private val activateDeferredTaskUseCase: ActivateDeferredTaskUseCase,
    private val checkAndActivateDeferredTasksUseCase: CheckAndActivateDeferredTasksUseCase,
    private val fetchCompletedTaskCountUseCase: FetchCompletedTaskCountUseCase
) : ViewModel() {

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks
    private var allTasks: List<Task> = listOf()

    private val _isAscending = MutableLiveData(true)
    val isAscending: LiveData<Boolean> = _isAscending

    private val _completedTaskCount = MutableLiveData<Int>()
    val completedTaskCount: LiveData<Int> = _completedTaskCount

    private val _totalTaskCount = MutableLiveData<Int>()
    val totalTaskCount: LiveData<Int> = _totalTaskCount

    private var deferredTaskJob: Job? = null

    init {
        Log.d("TaskViewModel", "ViewModel initialized")
    }

    fun archiveTask(task: Task) {
        Log.d("TaskViewModel", "Archiving task: ${task.taskId}")
        val updatedTask = task.copy(archived = true)
        updateTask(updatedTask)
    }

    fun refreshTasks() {
        Log.d("TaskViewModel", "Refreshing tasks")
        _tasks.postValue(allTasks)
    }

    fun unarchiveTask(task: Task) {
        Log.d("TaskViewModel", "Unarchiving task: ${task.taskId}")
        val updatedTask = task.copy(archived = false)
        updateTask(updatedTask)
    }

    fun filterTasks(showArchived: Boolean) {
        Log.d("TaskViewModel", "Filtering tasks: showArchived = $showArchived")
        _tasks.postValue(
            if (showArchived) {
                allTasks.filter { it.archived }
            } else {
                allTasks.filter { !it.archived }
            }
        )
    }

    fun fetchTasks(userId: String) {
        Log.d("TaskViewModel", "Fetching tasks for user: $userId")
        if (userId.isNotEmpty()) {
            viewModelScope.launch {
                fetchTasksUseCase.execute(userId, onSuccess = { taskList ->
                    allTasks = taskList
                    _tasks.postValue(taskList)
                    _totalTaskCount.postValue(taskList.size)
                    fetchCompletedTaskCount(userId)
                }, onFailure = {
                    Log.e("TaskViewModel", "Failed to fetch tasks")
                    _tasks.postValue(emptyList())
                    _totalTaskCount.postValue(0)
                    _completedTaskCount.postValue(0)
                })
            }
        }
    }

    fun searchTasks(query: String) {
        Log.d("TaskViewModel", "Searching tasks with query: $query")
        val result = searchTasksUseCase.execute(query, allTasks)
        _tasks.postValue(result)
    }

    fun toggleSortOrder() {
        Log.d("TaskViewModel", "Toggling sort order")
        _isAscending.value = _isAscending.value?.not()
        sortTasks()
    }

    private fun sortTasks() {
        val ascending = _isAscending.value ?: true
        Log.d("TaskViewModel", "Sorting tasks: ascending = $ascending")
        _tasks.postValue(
            if (ascending) {
                _tasks.value?.sortedBy { it.createdAt }
            } else {
                _tasks.value?.sortedByDescending { it.createdAt }
            }
        )
    }

    fun addTask(task: Task) {
        addTaskUseCase.execute(task,
            onSuccess = {
                Log.d("TaskViewModel", "Task added successfully")
            },
            onFailure = { exception ->
                Log.e("TaskViewModel", "Failed to add task", exception)
            }
        )
    }



    fun updateTask(task: Task) {
        Log.d("TaskViewModel", "Updating task: ${task.taskId}")
        viewModelScope.launch {
            updateTaskUseCase.execute(
                task,
                onSuccess = {
                    fetchTasks(task.userId)
                    Log.d("TaskViewModel", "Task updated successfully")
                },
                onFailure = { exception ->
                    Log.e("TaskViewModel", "Failed to update task", exception)
                }
            )
        }
    }




    fun deleteTask(taskId: String, userId: String) {
        Log.d("TaskViewModel", "Deleting task: $taskId")
        viewModelScope.launch {
            deleteTaskUseCase.execute(
                taskId,
                onSuccess = {
                    fetchTasks(userId)
                },
                onFailure = {
                    Log.e("TaskViewModel", "Failed to delete task")
                }
            )
        }
    }

    fun getTaskById(taskId: String): Task? {
        return _tasks.value?.find { it.taskId == taskId }
    }

    fun updateSubTask(taskId: String, subTask: SubTask) {
        Log.d("TaskViewModel", "Updating sub-task for task: $taskId")
        viewModelScope.launch {
            updateSubTaskUseCase.execute(
                taskId,
                subTask,
                onSuccess = {
                    val task = getTaskById(taskId)
                    task?.let {
                        fetchTasks(it.userId)
                    }
                },
                onFailure = {
                    Log.e("TaskViewModel", "Failed to update sub-task")
                }
            )
        }
    }


    fun fetchDeferredTasks(currentTime: Long) {
        val userId = savedStateHandle.get<String>("userId")
        Log.d("TaskViewModel", "Fetching deferred tasks for user: $userId")
        if (userId != null) {
            viewModelScope.launch {
                fetchDeferredTasksUseCase.execute(
                    userId,
                    currentTime,
                    onSuccess = { taskList ->
                        allTasks = taskList
                        _tasks.postValue(taskList)
                    },
                    onFailure = {
                        Log.e("TaskViewModel", "Failed to fetch deferred tasks")
                        _tasks.postValue(emptyList())
                    }
                )
            }
        }
    }

    fun activateDeferredTask(taskId: String) {
        Log.d("TaskViewModel", "Activating deferred task: $taskId")
        viewModelScope.launch {
            activateDeferredTaskUseCase.execute(
                taskId,
                onSuccess = {
                    fetchTasks(savedStateHandle.get<String>("userId") ?: "")
                },
                onFailure = { exception ->
                    Log.e("TaskViewModel", "Failed to activate deferred task: $taskId", exception)
                }
            )
        }
    }

    suspend fun checkAndActivateDeferredTasks() {
        val currentTime = System.currentTimeMillis()
        Log.d("TaskViewModel", "Checking and activating deferred tasks")
        val userId = savedStateHandle.get<String>("userId") ?: return

        checkAndActivateDeferredTasksUseCase.execute(
            userId = userId,
            currentTime = currentTime,
            onFailure = { exception ->
                Log.e("TaskViewModel", "Failed to check and activate deferred tasks", exception)
            }
        )
    }

    fun fetchCompletedTaskCount(userId: String) {
        Log.d("TaskViewModel", "Fetching completed task count for user: $userId")
        fetchCompletedTaskCountUseCase.execute(
            userId = userId,
            onSuccess = { count ->
                _completedTaskCount.postValue(count)
            },
            onFailure = {
                Log.e("TaskViewModel", "Failed to fetch completed task count")
                _completedTaskCount.postValue(0)
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        deferredTaskJob?.cancel()
        Log.d("TaskViewModel", "ViewModel cleared")
    }
}