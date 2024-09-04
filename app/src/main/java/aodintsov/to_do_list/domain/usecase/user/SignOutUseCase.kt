package aodintsov.to_do_list.domain.usecase.user

import com.google.firebase.auth.FirebaseAuth
import jakarta.inject.Inject

class SignOutUseCase @Inject constructor (private val firebaseAuth: FirebaseAuth) {

    // Функция для выполнения выхода пользователя из аккаунта
    fun execute(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        try {
            firebaseAuth.signOut()
            onSuccess()
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}
