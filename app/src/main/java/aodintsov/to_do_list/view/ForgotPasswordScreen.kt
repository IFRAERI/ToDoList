package aodintsov.to_do_list.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import aodintsov.to_do_list.viewmodel.AuthViewModel
import aodintsov.to_do_list.viewmodel.AuthViewModelFactory

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    authViewModelFactory: AuthViewModelFactory,
    modifier: Modifier = Modifier
) {
    val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
    var email by remember { mutableStateOf("") }
    var emailSent by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Enter your email to reset your password", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (email.isNotBlank()) {
                authViewModel.sendPasswordResetEmail(email) { success, exception ->
                    if (success) {
                        emailSent = true
                        Toast.makeText(context, "Password reset email sent!", Toast.LENGTH_SHORT).show()
                    } else {
                        errorMessage = exception?.message ?: "Error sending password reset email"
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Send reset email")
        }

        if (emailSent) {
            Text("Password reset email sent!", color = MaterialTheme.colorScheme.primary)
        }

        if (errorMessage != null) {
            Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
        }
    }
}
