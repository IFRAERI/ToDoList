package aodintsov.to_do_list.view

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
fun RegisterScreen(
    navController: NavController,
    authViewModelFactory: AuthViewModelFactory,
    modifier: Modifier = Modifier
) {
    val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var showSnackbar by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Регистрация")

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
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Email и пароль не должны быть пустыми"
                    showSnackbar = true
                } else {
                    authViewModel.register(email, password) { success ->
                        if (success) {
                            navController.navigate("taskList")
                        } else {
                            // Обработка ошибки регистрации
                            errorMessage = "Регистрация не удалась. Пожалуйста, попробуйте снова."
                            showSnackbar = true
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Register")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Login")
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
            Text(text = errorMessage ?: "Unknown error")
        }
    }
}
