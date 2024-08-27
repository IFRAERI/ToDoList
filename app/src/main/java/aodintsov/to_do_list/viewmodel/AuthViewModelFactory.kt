package aodintsov.to_do_list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import aodintsov.to_do_list.model.UserRepository

class AuthViewModelFactory(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository // Добавляем зависимость от UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(firebaseAuth, userRepository) as T // Передаем userRepository в AuthViewModel
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
