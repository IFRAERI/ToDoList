package aodintsov.to_do_list.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

import android.util.Log
import aodintsov.to_do_list.model.User
import aodintsov.to_do_list.model.UserRepository
class AuthViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository  // Добавляем зависимость от UserRepository
) : ViewModel() {

    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    fun login(email: String, password: String, onComplete: (Boolean) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUserId = firebaseAuth.currentUser?.uid
                    if (currentUserId != null) {
                        // Проверяем, существует ли пользователь в Firestore
                        userRepository.getUser(currentUserId, { user ->
                            // Пользователь существует, обновляем время последнего входа
                            updateLastLoginTime(user)
                            onComplete(true)
                        }, {
                            // Пользователя нет, создаем нового
                            createUserInFirestore(currentUserId, email, onComplete)
                        })
                    } else {
                        onComplete(false)
                    }
                    Log.d("AuthViewModel", "Login successful for email: $email")
                } else {
                    Log.e("AuthViewModel", "Login failed for email: $email, error: ${task.exception}")
                    onComplete(false)
                }
            }
    }

    private fun createUserInFirestore(userId: String, email: String, onComplete: (Boolean) -> Unit) {
        val newUser = User(
            userId = userId,
            email = email,
            name = "", // Здесь можно запросить имя позже
            points = 0,
            lastLoginTime = System.currentTimeMillis(),
            completedTasksCount = 0
        )
        userRepository.addUser(newUser, {
            Log.d("AuthViewModel", "User created in Firestore: $userId")
            onComplete(true)
        }, { exception ->
            Log.e("AuthViewModel", "Failed to create user in Firestore: ${exception.message}")
            onComplete(false)
        })
    }

    private fun updateLastLoginTime(user: User) {
        user.lastLoginTime = System.currentTimeMillis()
        userRepository.updateUser(user, {
            Log.d("AuthViewModel", "Last login time updated for user: ${user.userId}")
        }, { exception ->
            Log.e("AuthViewModel", "Failed to update last login time: ${exception.message}")
        })
    }

    fun register(email: String, password: String, onComplete: (Boolean, Exception?) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = firebaseAuth.currentUser?.uid
                    if (userId != null) {
                        createUserInFirestore(userId, email) { success ->
                            onComplete(success, null)
                        }
                    } else {
                        onComplete(false, Exception("User ID is null after registration"))
                    }
                    Log.d("AuthViewModel", "Registration successful for email: $email")
                } else {
                    Log.e("AuthViewModel", "Registration failed for email: $email, error: ${task.exception}")
                    onComplete(false, task.exception)
                }
            }
    }

    fun signOut(onSuccess: () -> Unit) {
        firebaseAuth.signOut()
        Log.d("AuthViewModel", "User signed out")
        onSuccess()
    }

    fun sendPasswordResetEmail(email: String, onComplete: (Boolean, Exception?) -> Unit) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AuthViewModel", "Password reset email sent to: $email")
                    onComplete(true, null)
                } else {
                    Log.e("AuthViewModel", "Password reset email failed for: $email, error: ${task.exception}")
                    onComplete(false, task.exception)
                }
            }
    }
}
