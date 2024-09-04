package aodintsov.to_do_list.domain.usecase.user

import aodintsov.to_do_list.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val updateLastLoginTimeUseCase: UpdateLastLoginTimeUseCase,
    private val createUserInFirestoreUseCase: CreateUserInFirestoreUseCase
) {
    fun execute(email: String, password: String, onComplete: (Boolean) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUserId = firebaseAuth.currentUser?.uid
                    if (currentUserId != null) {
                        userRepository.getUser(currentUserId, { user ->
                            updateLastLoginTimeUseCase.execute(user)
                            onComplete(true)
                        }, {
                            createUserInFirestoreUseCase.execute(
                                currentUserId,
                                email,
                                onComplete
                            )
                        })
                    } else {
                        onComplete(false)
                    }
                } else {
                    onComplete(false)
                }
            }
    }
    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

}
