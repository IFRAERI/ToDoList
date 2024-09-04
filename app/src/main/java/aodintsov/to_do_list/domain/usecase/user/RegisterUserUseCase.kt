package aodintsov.to_do_list.domain.usecase.user

import aodintsov.to_do_list.data.model.User
import aodintsov.to_do_list.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import jakarta.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository
) {
    fun execute(email: String, password: String, onComplete: (Boolean, Exception?) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = firebaseAuth.currentUser?.uid
                    if (userId != null) {
                        val user = User(userId, email, points = 0, lastLoginTime = System.currentTimeMillis())
                        userRepository.addUser(user, {
                            onComplete(true, null)
                        }, { exception ->
                            onComplete(false, exception)
                        })
                    } else {
                        onComplete(false, Exception("User ID is null after registration"))
                    }
                } else {
                    onComplete(false, task.exception)
                }
            }
    }
}
