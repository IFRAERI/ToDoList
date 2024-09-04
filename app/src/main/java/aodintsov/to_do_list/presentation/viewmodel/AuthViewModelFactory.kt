package aodintsov.to_do_list.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import aodintsov.to_do_list.domain.usecase.user.LoginUseCase
import aodintsov.to_do_list.domain.usecase.user.RegisterUserUseCase
import aodintsov.to_do_list.domain.usecase.user.SendPasswordResetEmailUseCase
import aodintsov.to_do_list.domain.usecase.user.SignOutUseCase

class AuthViewModelFactory(
    private val loginUseCase: LoginUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(
                loginUseCase,
                registerUserUseCase,
                signOutUseCase,
                sendPasswordResetEmailUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
