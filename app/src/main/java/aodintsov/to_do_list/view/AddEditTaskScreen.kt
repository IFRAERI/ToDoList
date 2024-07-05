package aodintsov.to_do_list.view

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import aodintsov.to_do_list.model.Task
import aodintsov.to_do_list.viewmodel.TaskViewModel
import aodintsov.to_do_list.viewmodel.TaskViewModelFactory

@Composable
fun AddEditTaskScreen(
    navController: NavController,
    taskId: String?,
    taskViewModelFactory: TaskViewModelFactory,
    modifier: Modifier = Modifier
) {
    val taskViewModel: TaskViewModel = viewModel(factory = taskViewModelFactory)
    var taskTitle by rememberSaveable { mutableStateOf("") }
    var taskDescription by rememberSaveable { mutableStateOf("") }
    var taskLoaded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        Log.d("AddEditTaskScreen", "LaunchedEffect triggered with taskId: $taskId")
        taskViewModel.fetchTasks("yourUserId")
    }

    val tasks by taskViewModel.tasks.observeAsState()

    LaunchedEffect(tasks) {
        if (tasks != null && taskId != null && !taskLoaded) {
            val task = taskViewModel.getTaskById(taskId)
            if (task != null) {
                Log.d("AddEditTaskScreen", "Task found: ${task.title}, ${task.description}")
                taskTitle = task.title
                taskDescription = task.description
                taskLoaded = true
            } else {
                Log.d("AddEditTaskScreen", "Task not found with taskId: $taskId")
            }
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Add/Edit Task")

        TextField(
            value = taskTitle,
            onValueChange = { taskTitle = it },
            label = { Text("Title") },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        TextField(
            value = taskDescription,
            onValueChange = { taskDescription = it },
            label = { Text("Description") },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Button(
            onClick = {
                if (taskId == null) {
                    taskViewModel.addTask(Task(taskId = System.currentTimeMillis().toString(), title = taskTitle, description = taskDescription, userId = "yourUserId"))
                } else {
                    val updatedTask = Task(taskId = taskId, title = taskTitle, description = taskDescription, userId = "yourUserId")
                    taskViewModel.updateTask(updatedTask)
                }
                navController.navigateUp()
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(text = "Save Task")
        }

        if (taskId != null) {
            Button(
                onClick = {
                    taskViewModel.deleteTask(taskId, "yourUserId")
                    navController.navigateUp()
                },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(text = "Delete Task")
            }
        }
    }
}
