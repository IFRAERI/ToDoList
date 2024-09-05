package aodintsov.to_do_list.presentation.viewmodel

import androidx.lifecycle.ViewModel
import android.util.Log
import aodintsov.to_do_list.domain.usecase.user.LoginUseCase
import aodintsov.to_do_list.domain.usecase.user.RegisterUserUseCase
import aodintsov.to_do_list.domain.usecase.user.SendPasswordResetEmailUseCase
import aodintsov.to_do_list.domain.usecase.user.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase
) : ViewModel() {

    fun getCurrentUserId(): String? {
        return loginUseCase.getCurrentUserId()
    }

    fun login(email: String, password: String, onComplete: (Boolean, Exception?) -> Unit) {
        loginUseCase.execute(email, password) { success, exception ->
            if (success) {
                Log.d("AuthViewModel", "Login successful for email: $email")
                onComplete(true, null)
            } else {
                Log.e("AuthViewModel", "Login failed for email: $email", exception)
                onComplete(false, exception)
            }
        }
    }


    fun register(email: String, password: String, onComplete: (Boolean, Exception?) -> Unit) {
        registerUserUseCase.execute(email, password) { success, exception ->
            if (success) {
                Log.d("AuthViewModel", "Registration successful for email: $email")
                onComplete(true, null)
            } else {
                Log.e("AuthViewModel", "Registration failed for email: $email, error: ${exception?.message}")
                onComplete(false, exception)
            }
        }
    }

    fun signOut(onSuccess: () -> Unit) {
        signOutUseCase.execute(onSuccess, { exception ->
            Log.e("AuthViewModel", "Sign out failed: ${exception.message}")
        })
    }

    fun sendPasswordResetEmail(email: String, onComplete: (Boolean, Exception?) -> Unit) {
        sendPasswordResetEmailUseCase.execute(email, {
            Log.d("AuthViewModel", "Password reset email sent to: $email")
            onComplete(true, null)
        }, { exception ->
            Log.e("AuthViewModel", "Password reset email failed for: $email, error: ${exception.message}")
            onComplete(false, exception)
        })
    }
}
