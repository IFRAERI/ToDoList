package aodintsov.to_do_list.view

//import android.util.Log
//import androidx.annotation.OptIn
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.media3.common.util.Log
//import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import aodintsov.to_do_list.R
import aodintsov.to_do_list.data.model.User
import aodintsov.to_do_list.presentation.viewmodel.AuthViewModel
import aodintsov.to_do_list.presentation.viewmodel.AuthViewModelFactory
import aodintsov.to_do_list.presentation.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import android.util.Log
import androidx.hilt.navigation.compose.hiltViewModel

//@OptIn(UnstableApi::class)
@Composable
fun RegisterScreen(

    navController: NavController,

    modifier: Modifier = Modifier
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()

    val coroutineScope = rememberCoroutineScope()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val emptyFieldsError = stringResource(R.string.empty_fields_error)
    val passwordLengthError = stringResource(R.string.password_length_error)
    val passwordMismatchError = stringResource(R.string.password_mismatch_error)
    val registrationFailedError = stringResource(R.string.registration_failed_error)
    val userCreationFailedError = stringResource(R.string.user_creation_failed_error)
    val unknownError = R.string.unknown_error
    Log.e("RegisterScreen", "Register screen opened")

    Box(modifier = modifier.fillMaxSize()) {
       // Log.d("RegisterScreen", "Register screen opened")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.registration_title),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.email_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password_label)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text(stringResource(R.string.confirm_password_label)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    when {
                        email.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                            Log.d("RegisterScreen", "Validation failed: empty fields")
                            showSnackbar(coroutineScope, snackbarHostState, emptyFieldsError)
                        }
                        password.length < 8 -> {
                            Log.d("RegisterScreen", "Validation failed: password too short")
                            showSnackbar(coroutineScope, snackbarHostState, passwordLengthError)
                        }
                        password != confirmPassword -> {
                            Log.d("RegisterScreen", "Validation failed: passwords do not match")
                            showSnackbar(coroutineScope, snackbarHostState, passwordMismatchError)
                        }
                        else -> {
                            isLoading = true
                            Log.d("RegisterScreen", "Starting registration for email: $email")
                            authViewModel.register(email.trim(), password) { success, exception: Exception? ->
                                if (success) {
                                    Log.d("RegisterScreen", "Registration successful for email: $email")
                                    val currentUserId = authViewModel.getCurrentUserId()
                                    if (currentUserId != null) {
                                        Log.d("RegisterScreen", "Got currentUserId: $currentUserId")
                                        val newUser = User(
                                            userId = currentUserId,
                                            email = email.trim(),
                                            name = "",
                                            points = 0 // Инициализируем очки пользователя
                                        )
                                        userViewModel.addUser(newUser,
                                            onSuccess = {
                                                Log.d("RegisterScreen", "User added to Firestore: $currentUserId")
                                                isLoading = false
                                                navController.navigate("taskList") {
                                                    popUpTo("register") { inclusive = true }
                                                }
                                            },
                                            onFailure = { userException: Exception ->
                                                Log.e("RegisterScreen", "Failed to add user to Firestore: ${userException.message}")
                                                isLoading = false
                                                showSnackbar(
                                                    coroutineScope,
                                                    snackbarHostState,
                                                    "$userCreationFailedError: ${userException.message ?: unknownError}"
                                                )
                                            }
                                        )
                                    } else {
                                        Log.e("RegisterScreen", "currentUserId is null after successful registration")
                                        isLoading = false
                                        showSnackbar(
                                            coroutineScope,
                                            snackbarHostState,
                                            registrationFailedError
                                        )
                                    }
                                } else {
                                    Log.e("RegisterScreen", "Registration failed: ${exception?.message}")
                                    isLoading = false
                                    val errorMessage = exception?.message ?: registrationFailedError
                                    showSnackbar(
                                        coroutineScope,
                                        snackbarHostState,
                                        errorMessage
                                    )
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(text = stringResource(R.string.register_button))
                }
            }



            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.already_have_account))
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

private fun showSnackbar(
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    message: String
) {
    coroutineScope.launch {
        snackbarHostState.showSnackbar(message)
    }
}
