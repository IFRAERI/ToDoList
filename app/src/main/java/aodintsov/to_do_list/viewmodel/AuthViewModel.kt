package aodintsov.to_do_list.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel(private val firebaseAuth: FirebaseAuth) : ViewModel() {

    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }
}
