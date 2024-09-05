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
    fun execute(email: String, password: String, onComplete: (Boolean, Exception?) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUserId = firebaseAuth.currentUser?.uid
                    if (currentUserId != null) {
                        // Fetch the user from the repository
                        userRepository.getUser(currentUserId, { user ->
                            // Update last login time
                            updateLastLoginTimeUseCase.execute(
                                userId = user.userId,
                                currentTime = System.currentTimeMillis(),
                                onSuccess = {
                                    onComplete(true, null)
                                },
                                onFailure = { exception ->
                                    onComplete(false, exception)
                                }
                            )
                        }, { exception ->
                            // If user doesn't exist, create a new one in Firestore
                            createUserInFirestoreUseCase.execute(currentUserId, email) { success ->
                                if (success) {
                                    onComplete(true, null)
                                } else {
                                    onComplete(false, Exception("Failed to create user in Firestore"))
                                }
                            }
                        })
                    } else {
                        onComplete(false, Exception("User ID is null after sign-in"))
                    }
                } else {
                    onComplete(false, task.exception)
                }
            }
    }

    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }
}
