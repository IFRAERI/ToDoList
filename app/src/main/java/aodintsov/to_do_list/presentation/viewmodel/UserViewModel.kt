package aodintsov.to_do_list.presentation.viewmodel


import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aodintsov.to_do_list.data.model.User
import aodintsov.to_do_list.domain.repository.UserRepository
import aodintsov.to_do_list.domain.usecase.user.GetUserUseCase
import aodintsov.to_do_list.domain.usecase.user.RegisterUserUseCase
import aodintsov.to_do_list.domain.usecase.user.UpdateLastLoginTimeUseCase
import aodintsov.to_do_list.domain.usecase.user.UpdateUserPointsUseCase
import aodintsov.to_do_list.domain.usecase.user.UpdateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import kotlinx.coroutines.launch

@HiltViewModel
class UserViewModel @Inject constructor(

    private val userRepository: UserRepository,
    private val registerUserUseCase: RegisterUserUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val updateLastLoginTimeUseCase: UpdateLastLoginTimeUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val updateUserPointsUseCase: UpdateUserPointsUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
) : ViewModel() {

    // Получение данных пользователя по userId
    fun getUser(userId: String) {
        Log.d("UserViewModel", "Fetching user data for userId: $userId")
        viewModelScope.launch {
            try {
                getUserUseCase.execute(
                    userId = userId,
                    onSuccess = { user ->
                        // Здесь можно работать с полученным пользователем
                        Log.d("UserViewModel", "User fetched successfully: ${user.userId}")
                        // Например, обновить LiveData или обработать результат
                    },
                    onFailure = { exception ->
                        Log.e("UserViewModel", "Failed to fetch user data: ${exception.message}", exception)
                    }
                )
            } catch (e: Exception) {
                Log.e("UserViewModel", "Exception during fetching user: ${e.message}", e)
            }
        }
    }


    // Обновление данных пользователя
    fun updateUser(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        Log.d("UserViewModel", "Updating user data for userId: ${user.userId}")
        viewModelScope.launch {
            try {
                updateUserUseCase.execute(user, onSuccess, onFailure)
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
                updateUserPointsUseCase.execute(userId, points, onSuccess, onFailure)
            } catch (e: Exception) {
                Log.e("UserViewModel", "Exception during updating points: ${e.message}", e)
                onFailure(e)
            }
        }
    }



    fun updateLastLoginTime(userId: String, lastLoginTime: Long, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        Log.d("UserViewModel", "Updating last login time for userId: $userId")

        viewModelScope.launch {
            updateLastLoginTimeUseCase.execute(
                userId = userId,
                currentTime = lastLoginTime,
                onSuccess = {
                    onSuccess()
                },
                onFailure = { exception ->
                    Log.e("UserViewModel", "Failed to update last login time: ${exception.message}", exception)
                    onFailure(exception)
                }
            )
        }
    }


    // Получение количества завершенных задач
//    fun getCompletedTaskCount(userId: String, onSuccess: (Int) -> Unit, onFailure: (Exception) -> Unit) {
//        Log.d("UserViewModel", "Fetching completed task count for userId: $userId")
//        viewModelScope.launch {
//            try {
//                userRepository.getCompletedTaskCount(userId, onSuccess, {
//                    Log.e("UserViewModel", "Failed to fetch completed task count: ${it.message}", it)
//                    onFailure(it)
//                })
//            } catch (e: Exception) {
//                Log.e("UserViewModel", "Exception during fetching completed task count: ${e.message}", e)
//                onFailure(e)
//            }
//        }
//    }

    // Добавление нового пользователя
//    fun addUser(email: String, password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
//        Log.d("UserViewModel", "Registering new user with email: $email")
//
//        viewModelScope.launch {
//            try {
//                registerUserUseCase.execute(email, password, { success, exception ->
//                    if (success) {
//                        Log.d("UserViewModel", "User registered and added successfully")
//                        onSuccess()
//                    } else {
//                        Log.e("UserViewModel", "Failed to register user: ${exception?.message}", exception)
//                        onFailure(exception ?: Exception("Unknown error"))
//                    }
//                })
//            } catch (e: Exception) {
//                Log.e("UserViewModel", "Exception during adding user: ${e.message}", e)
//                onFailure(e)
//            }
//        }
//    }
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