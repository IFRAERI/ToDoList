package aodintsov.to_do_list.domain.usecase.user

import com.google.firebase.auth.FirebaseAuth
import jakarta.inject.Inject

class SendPasswordResetEmailUseCase @Inject constructor(private val firebaseAuth: FirebaseAuth) {

    // Функция для отправки письма сброса пароля
    fun execute(email: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    task.exception?.let { onFailure(it) }
                }
            }
    }
}
