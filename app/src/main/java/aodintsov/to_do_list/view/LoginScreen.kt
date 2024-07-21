package aodintsov.to_do_list.view

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import aodintsov.to_do_list.R
import aodintsov.to_do_list.viewmodel.AuthViewModel
import aodintsov.to_do_list.viewmodel.AuthViewModelFactory
@Composable
fun LoginScreen(
    navController: NavController,
    authViewModelFactory: AuthViewModelFactory,
    modifier: Modifier = Modifier
) {
    val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by rememberSaveable { mutableStateOf("") }
    val empty_fields_error = stringResource(R.string.empty_fields_error)
    val login_failed_error = stringResource(R.string.login_failed_error)
    val reset_email_sent = stringResource(R.string.reset_email_sent)
    val reset_email_failed = stringResource(R.string.reset_email_failed)
    val enter_email_for_reset = stringResource(R.string.enter_email_for_reset)

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
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    snackbarMessage = empty_fields_error
                    showSnackbar = true
                } else {
                    authViewModel.login(email, password) { success ->
                        if (success) {
                            navController.navigate("taskList") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            snackbarMessage = login_failed_error
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
                            reset_email_sent
                        } else {
                            reset_email_failed ; exception?.message ?: "unknown error"
                        }
                        showSnackbar = true
                    }
                } else {
                    snackbarMessage = enter_email_for_reset
                    showSnackbar = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.forgot_password_button))
        }
    }

    if (showSnackbar) {
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