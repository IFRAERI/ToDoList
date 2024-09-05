package aodintsov.to_do_list.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import aodintsov.to_do_list.view.*
import aodintsov.to_do_list.presentation.viewmodel.AuthViewModel

import aodintsov.to_do_list.presentation.viewmodel.AuthViewModelFactory
import aodintsov.to_do_list.presentation.viewmodel.TaskViewModel
import aodintsov.to_do_list.presentation.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val taskViewModel: TaskViewModel = hiltViewModel()

    // Получаем текущий userId
    val currentUserId = authViewModel.getCurrentUserId()

    if (currentUserId.isNullOrBlank()) {
        // Если userId пустой, вызываем getUser из UserViewModel
        userViewModel.getUser(currentUserId ?: "")
    }

    NavHost(
        navController = navController,
        startDestination = if (currentUserId == null) "login" else "taskList"
    ) {
        composable("login") {
            LoginScreen(
                navController = navController,
                modifier = modifier
            )
        }
        composable("register") {
            RegisterScreen(
                navController = navController,
                modifier = modifier
            )
        }
        composable("taskList") {
            TaskListScreen(
                navController = navController,
                userId = currentUserId ?: "",
                modifier = modifier
            )
        }
        composable("forgotPassword") {
            ForgotPasswordScreen(
                navController = navController,
                modifier = modifier
            )
        }
        composable("addEditTask/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            AddEditTaskScreen(
                navController = navController,
                userId = currentUserId ?: "",
                taskId = taskId,
                modifier = modifier
            )
        }
        composable("addEditTask") {
            AddEditTaskScreen(
                navController = navController,
                userId = currentUserId ?: "",
                taskId = null,
                modifier = modifier
            )
        }
    }
}
