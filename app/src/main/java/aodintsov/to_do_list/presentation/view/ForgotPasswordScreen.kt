package aodintsov.to_do_list.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import aodintsov.to_do_list.R
import aodintsov.to_do_list.presentation.viewmodel.AuthViewModel

import aodintsov.to_do_list.presentation.viewmodel.AuthViewModelFactory
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    //authViewModelFactory: AuthViewModelFactory,
    modifier: Modifier = Modifier
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    var email by remember { mutableStateOf("") }
    var emailSent by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Получаем строки до вызова функций
    val resetEmailSent = stringResource(R.string.reset_email_sent)
    val resetEmailFailed = stringResource(R.string.reset_email_failed)
    val enterEmailPrompt = stringResource(R.string.enter_email_prompt)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.forgot_password_prompt),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email_label)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (email.isNotBlank()) {
                authViewModel.sendPasswordResetEmail(email) { success, exception ->
                    if (success) {
                        emailSent = true
                        Toast.makeText(context, resetEmailSent, Toast.LENGTH_SHORT).show()
                    } else {
                        errorMessage = exception?.message ?: resetEmailFailed
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, enterEmailPrompt, Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(stringResource(R.string.send_reset_email_button))
        }

        if (emailSent) {
            Text(
                text = resetEmailSent,
                color = MaterialTheme.colorScheme.primary
            )
        }

        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}
