package aodintsov.to_do_list.domain.usecase.user

import aodintsov.to_do_list.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import jakarta.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val createUserInFirestoreUseCase: CreateUserInFirestoreUseCase // Здесь нужно использовать правильный use-case
) {
    fun execute(email: String, password: String, onComplete: (Boolean, Exception?) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = firebaseAuth.currentUser?.uid
                    if (userId != null) {
                        // Используем другой use-case для создания пользователя в Firestore
                        createUserInFirestoreUseCase.execute(userId, email) { success ->
                            if (success) {
                                onComplete(true, null)
                            } else {
                                onComplete(false, Exception("Failed to create user in Firestore"))
                            }
                        }
                    } else {
                        onComplete(false, Exception("User ID is null after registration"))
                    }
                } else {
                    onComplete(false, task.exception)
                }
            }
    }
}
