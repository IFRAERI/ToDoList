package aodintsov.to_do_list.view

//import android.util.Log
//import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import aodintsov.to_do_list.R
import aodintsov.to_do_list.presentation.viewmodel.AuthViewModel
import aodintsov.to_do_list.presentation.viewmodel.AuthViewModelFactory
import kotlinx.coroutines.delay
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    //authViewModelFactory: AuthViewModelFactory,
    modifier: Modifier = Modifier
) {

    val authViewModel: AuthViewModel = hiltViewModel()
   // val authViewModel: AuthViewModel = hiltViewModel()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by rememberSaveable { mutableStateOf("") }
    val emptyFieldsError = stringResource(R.string.empty_fields_error)
    val loginFailedError = stringResource(R.string.login_failed_error)
    val resetEmailSent = stringResource(R.string.reset_email_sent)
    val resetEmailFailed = stringResource(R.string.reset_email_failed)
    val enterEmailForReset = stringResource(R.string.enter_email_for_reset)
    val unknownError = stringResource(R.string.unknown_error)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = stringResource(R.string.login_screen_title))
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email_label)) },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password_label)) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation() // Скрытие пароля
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    snackbarMessage = emptyFieldsError
                    showSnackbar = true
                } else {
                    authViewModel.login(email, password) { success, exception ->
                        if (success) {
                            navController.navigate("taskList") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            snackbarMessage = loginFailedError
                            showSnackbar = true
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.login_button))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                navController.navigate("register")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.register_button))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (email.isNotBlank()) {
                    authViewModel.sendPasswordResetEmail(email) { success, exception ->
                        snackbarMessage = if (success) {
                            resetEmailSent
                        } else {
                            "$resetEmailFailed: ${exception?.message ?: unknownError}"
                        }
                        showSnackbar = true
                    }
                } else {
                    snackbarMessage = enterEmailForReset
                    showSnackbar = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.forgot_password_button))
        }
    }

    if (showSnackbar) {
        LaunchedEffect(snackbarMessage) {
            showSnackbar = true
            delay(3000) // Snackbar будет показываться в течение 3 секунд
            showSnackbar = false
        }

        Snackbar(
            action = {
                Button(onClick = { showSnackbar = false }) {
                    Text(stringResource(R.string.dismiss))
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = snackbarMessage)
        }
    }
}