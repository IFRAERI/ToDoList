package aodintsov.to_do_list.view

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Login")
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                Log.d("LoginScreen", "Login button clicked")
                if (email.isBlank() || password.isBlank()) {
                    snackbarMessage = "Email и пароль не должны быть пустыми"
                    showSnackbar = true
                    Log.d("LoginScreen", "Email or password is blank")
                } else {
                    authViewModel.login(email, password) { success ->
                        if (success) {
                            Log.d("LoginScreen", "Login successful")
                            navController.navigate("taskList") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            snackbarMessage = "Вход не удался. Пожалуйста, попробуйте снова."
                            showSnackbar = true
                            Log.d("LoginScreen", "Login failed")
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                Log.d("LoginScreen", "Register button clicked")
                try {
                    navController.navigate("register")
                    Log.d("LoginScreen", "Navigated to register")
                } catch (e: Exception) {
                    Log.e("LoginScreen", "Navigation error: ${e.message}")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Register")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                Log.d("LoginScreen", "Forgot Password button clicked")
                if (email.isNotBlank()) {
                    authViewModel.sendPasswordResetEmail(email) { success, exception ->
                        if (success) {
                            snackbarMessage = "Password reset email sent!"
                            Log.d("LoginScreen", "Password reset email sent")
                        } else {
                            snackbarMessage = "Failed to send password reset email: ${exception?.message}"
                            Log.d("LoginScreen", "Failed to send password reset email: ${exception?.message}")
                        }
                        showSnackbar = true
                    }
                } else {
                    snackbarMessage = "Please enter your email"
                    showSnackbar = true
                    Log.d("LoginScreen", "Email is blank for password reset")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Forgot Password")
        }
    }

    if (showSnackbar) {
        Snackbar(
            action = {
                Button(onClick = { showSnackbar = false }) {
                    Text("Dismiss")
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = snackbarMessage ?: "Unknown error")
        }
    }
}
