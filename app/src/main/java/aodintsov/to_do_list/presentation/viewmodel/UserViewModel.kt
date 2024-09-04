package aodintsov.to_do_list.presentation.viewmodel


import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aodintsov.to_do_list.data.model.User
import aodintsov.to_do_list.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import kotlinx.coroutines.launch

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Получение данных пользователя по userId
    fun getUser(userId: String, onSuccess: (User) -> Unit, onFailure: (Exception) -> Unit) {
        Log.d("UserViewModel", "Fetching user data for userId: $userId")
        viewModelScope.launch {
            try {
                userRepository.getUser(userId, onSuccess, {
                    Log.e("UserViewModel", "Failed to fetch user data: ${it.message}", it)
                    onFailure(it)
                })
            } catch (e: Exception) {
                Log.e("UserViewModel", "Exception during fetching user: ${e.message}", e)
                onFailure(e)
            }
        }
    }

    // Обновление данных пользователя
    fun updateUser(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        Log.d("UserViewModel", "Updating user data for userId: ${user.userId}")
        viewModelScope.launch {
            try {
                userRepository.updateUser(user, onSuccess, {
                    Log.e("UserViewModel", "Failed to update user: ${it.message}", it)
                    onFailure(it)
                })
            } catch (e: Exception) {
                Log.e("UserViewModel", "Exception during updating user: ${e.message}", e)
                onFailure(e)
            }
        }
    }

    // Обновление очков пользователя
    fun updateUserPoints(userId: String, points: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        Log.d("UserViewModel", "Updating points for userId: $userId to $points")
        viewModelScope.launch {
            try {
                userRepository.updateUserPoints(userId, points, onSuccess, {
                    Log.e("UserViewModel", "Failed to update points: ${it.message}", it)
                    onFailure(it)
                })
            } catch (e: Exception) {
                Log.e("UserViewModel", "Exception during updating points: ${e.message}", e)
                onFailure(e)
            }
        }
    }

    // Обновление времени последнего входа
    fun updateLastLoginTime(userId: String, lastLoginTime: Long, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        Log.d("UserViewModel", "Updating last login time for userId: $userId")
        viewModelScope.launch {
            try {
                userRepository.updateLastLoginTime(userId, lastLoginTime, onSuccess, {
                    Log.e("UserViewModel", "Failed to update last login time: ${it.message}", it)
                    onFailure(it)
                })
            } catch (e: Exception) {
                Log.e("UserViewModel", "Exception during updating last login time: ${e.message}", e)
                onFailure(e)
            }
        }
    }

    // Получение количества завершенных задач
    fun getCompletedTaskCount(userId: String, onSuccess: (Int) -> Unit, onFailure: (Exception) -> Unit) {
        Log.d("UserViewModel", "Fetching completed task count for userId: $userId")
        viewModelScope.launch {
            try {
                userRepository.getCompletedTaskCount(userId, onSuccess, {
                    Log.e("UserViewModel", "Failed to fetch completed task count: ${it.message}", it)
                    onFailure(it)
                })
            } catch (e: Exception) {
                Log.e("UserViewModel", "Exception during fetching completed task count: ${e.message}", e)
                onFailure(e)
            }
        }
    }

    // Добавление нового пользователя
    fun addUser(newUser: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        Log.d("UserViewModel", "Adding new user: ${newUser.userId}")
        viewModelScope.launch {
            try {
                userRepository.addUser(newUser, onSuccess = {
                    Log.d("UserViewModel", "User added successfully: ${newUser.userId}")
                    onSuccess()
                }, onFailure = { exception ->
                    Log.e("UserViewModel", "Failed to add user: ${exception.message}", exception)
                    onFailure(exception)
                })
            } catch (e: Exception) {
                Log.e("UserViewModel", "Exception during adding user: ${e.message}", e)
                onFailure(e)
            }
        }
    }
}