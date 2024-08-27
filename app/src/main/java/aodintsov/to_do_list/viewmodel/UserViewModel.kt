package aodintsov.to_do_list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aodintsov.to_do_list.model.User
import aodintsov.to_do_list.model.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    // Получение данных пользователя по userId
    fun getUser(userId: String, onSuccess: (User) -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            userRepository.getUser(userId, onSuccess, onFailure)
        }
    }

    // Обновление данных пользователя
    fun updateUser(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            userRepository.updateUser(user, onSuccess, onFailure)
        }
    }

    // Обновление очков пользователя
    fun updateUserPoints(userId: String, points: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            userRepository.updateUserPoints(userId, points, onSuccess, onFailure)
        }
    }

    // Обновление времени последнего входа
    fun updateLastLoginTime(userId: String, lastLoginTime: Long, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            userRepository.updateLastLoginTime(userId, lastLoginTime, onSuccess, onFailure)
        }
    }

    // Получение количества завершенных задач (если нужно отдельно хранить)
    fun getCompletedTaskCount(userId: String, onSuccess: (Int) -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            userRepository.getCompletedTaskCount(userId, onSuccess, onFailure)
        }
    }

    // Добавление нового пользователя
    fun addUser(newUser: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                userRepository.addUser(newUser, onSuccess = {
                    onSuccess()
                }, onFailure = { exception ->
                    onFailure(exception)
                })
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

}
