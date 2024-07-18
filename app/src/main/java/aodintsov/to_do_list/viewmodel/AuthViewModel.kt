package aodintsov.to_do_list.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel(private val firebaseAuth: FirebaseAuth) : ViewModel() {

    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    fun login(email: String, password: String, onComplete: (Boolean) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun register(email: String, password: String, onComplete: (Boolean) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }



    fun signOut(onSuccess: () -> Unit) {
        firebaseAuth.signOut()
        onSuccess()
    }

    fun sendPasswordResetEmail(email: String, onComplete: (Boolean, Exception?) -> Unit) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception)
                }
            }
    }
}