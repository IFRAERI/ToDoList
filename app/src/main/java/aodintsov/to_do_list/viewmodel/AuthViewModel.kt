package aodintsov.to_do_list.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

import android.util.Log

class AuthViewModel(private val firebaseAuth: FirebaseAuth) : ViewModel() {

    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    fun login(email: String, password: String, onComplete: (Boolean) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AuthViewModel", "Login successful for email: $email")
                } else {
                    Log.e("AuthViewModel", "Login failed for email: $email, error: ${task.exception}")
                }
                onComplete(task.isSuccessful)
            }
    }

    fun register(email: String, password: String, onComplete: (Boolean, Exception?) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AuthViewModel", "Registration successful for email: $email")
                    onComplete(true, null)
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
