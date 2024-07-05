package aodintsov.to_do_list.navigation

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import aodintsov.to_do_list.view.AddEditTaskScreen
import aodintsov.to_do_list.view.TaskListScreen
import aodintsov.to_do_list.viewmodel.AuthViewModelFactory
import aodintsov.to_do_list.viewmodel.TaskViewModelFactory

@Composable
fun AppNavigation(
    navController: NavHostController,
    taskViewModelFactory: TaskViewModelFactory,
    authViewModelFactory: AuthViewModelFactory,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = "taskList") {
        composable("taskList") {
            TaskListScreen(
                navController = navController,
                userId = "yourUserId",
                taskViewModelFactory = taskViewModelFactory,
                authViewModelFactory = authViewModelFactory,
                modifier = modifier.padding(1.dp)
            )
        }
        composable("addEditTask/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            Log.d("AppNavigation", "Navigating to addEditTask with taskId: $taskId")
            AddEditTaskScreen(
                navController = navController,
                taskId = taskId,
                taskViewModelFactory = taskViewModelFactory
            )
        }
        composable("addEditTask") {
            AddEditTaskScreen(
                navController = navController,
                taskId = null,
                taskViewModelFactory = taskViewModelFactory
            )
        }
    }
}
