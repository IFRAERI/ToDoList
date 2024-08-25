package aodintsov.to_do_list.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import aodintsov.to_do_list.model.TaskRepository

class TaskViewModelFactory(
    private val repository: TaskRepository,
    private val authViewModel: AuthViewModel,
    private val savedStateHandle: SavedStateHandle
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository, authViewModel, savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


    fun getRepository(): TaskRepository {
        return repository
    }


    fun getAuthViewModel(): AuthViewModel {
        return authViewModel
    }
}
