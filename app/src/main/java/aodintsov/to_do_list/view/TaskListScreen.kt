package aodintsov.to_do_list.view

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import aodintsov.to_do_list.model.Task
import aodintsov.to_do_list.viewmodel.AuthViewModel
import aodintsov.to_do_list.viewmodel.AuthViewModelFactory
import aodintsov.to_do_list.viewmodel.TaskViewModel
import aodintsov.to_do_list.viewmodel.TaskViewModelFactory

@Composable
fun TaskListScreen(
    navController: NavController,
    userId: String,
    taskViewModelFactory: TaskViewModelFactory,
    authViewModelFactory: AuthViewModelFactory,
    modifier: Modifier = Modifier
) {
    val taskViewModel: TaskViewModel = viewModel(factory = taskViewModelFactory)
    val tasks = taskViewModel.tasks.observeAsState(emptyList())
    val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)

    val currentUserId = authViewModel.getCurrentUserId() ?: userId
    taskViewModel.fetchTasks(currentUserId)
    tasks.value.forEach { task ->
        Log.d("TaskListScreen", "Task ID: ${task.taskId}")
    }

    Column(modifier = modifier) {
        Text(text = "Task List")
        if (tasks.value.isEmpty()) {
            Text(text = "No tasks available.")
        } else {
            LazyColumn {
                items(tasks.value) { task ->
                    TaskItem(task = task) {
                        Log.d("TaskListScreen", "Navigating to addEditTask with taskId: ${task.taskId}")
                        navController.navigate("addEditTask/${task.taskId}")
                    }
                }
            }
        }
        Button(onClick = { navController.navigate("addEditTask") }) {
            Text(text = "Add Task")
        }
        Button(onClick = { taskViewModel.deleteAllTasks() }) {
            Text(text = "Delete All Tasks")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskItem(task: Task, onLongClick: () -> Unit) {
    Column(modifier = Modifier.combinedClickable(
        onClick = { /* Do nothing on click */ },
        onLongClick = onLongClick
    )) {
        Text(text = task.title)
        Text(text = task.description)
    }
}
