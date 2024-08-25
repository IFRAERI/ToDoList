package aodintsov.to_do_list.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import aodintsov.to_do_list.view.*
import aodintsov.to_do_list.viewmodel.AuthViewModel
import aodintsov.to_do_list.viewmodel.AuthViewModelFactory
import aodintsov.to_do_list.viewmodel.TaskViewModelFactory
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(
    navController: NavHostController,
    taskViewModelFactory: TaskViewModelFactory,
    authViewModelFactory: AuthViewModelFactory,
    modifier: Modifier = Modifier,
    firebaseAuth: FirebaseAuth
) {
    val authViewModel = viewModel<AuthViewModel>(factory = authViewModelFactory)
    val currentUserId = authViewModel.getCurrentUserId()

    NavHost(
        navController = navController as NavHostController,
        startDestination = if (currentUserId == null) "login" else "taskList"
    ) {
        composable("login") {
            LoginScreen(
                navController = navController,
                authViewModelFactory = authViewModelFactory,
                modifier = modifier
            )
        }
        composable("register") {
            RegisterScreen(
                navController = navController,
                authViewModelFactory = authViewModelFactory,
                modifier = modifier
            )
        }
        composable("taskList") {
            TaskListScreen(
                navController = navController,
                userId = currentUserId ?: "",
                taskViewModelFactory = taskViewModelFactory,
                authViewModelFactory = authViewModelFactory,
                modifier = modifier
            )
        }
        composable("forgotPassword") {
            ForgotPasswordScreen(
                navController = navController,
                authViewModelFactory = authViewModelFactory,
                modifier = modifier
            )
        }
        composable("addEditTask/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            AddEditTaskScreen(
                navController = navController,
                userId = currentUserId ?: "",
                taskId = taskId,
                taskViewModelFactory = taskViewModelFactory
            )
        }
        composable("addEditTask") {
            AddEditTaskScreen(
                navController = navController,
                userId = currentUserId ?: "",
                taskId = null,
                taskViewModelFactory = taskViewModelFactory
            )
        }
    }
}
