package aodintsov.to_do_list.view

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import aodintsov.to_do_list.model.SubTask
import aodintsov.to_do_list.model.Task
import aodintsov.to_do_list.viewmodel.TaskViewModel
import aodintsov.to_do_list.viewmodel.TaskViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
    var isCompleted by rememberSaveable { mutableStateOf(false) }
    var deadline by rememberSaveable { mutableStateOf<Long?>(null) }
    var assignedTo by rememberSaveable { mutableStateOf("") } // New field for delegation
    var subTasks by rememberSaveable { mutableStateOf(listOf<SubTask>()) } // New field for subtasks

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
                isCompleted = task.completed
                deadline = task.dueDate
                assignedTo = task.assignedTo
                subTasks = task.subTasks
                taskLoaded = true
            } else {
                Log.d("AddEditTaskScreen", "Task not found with taskId: $taskId")
            }
        }
    }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    deadline?.let { calendar.timeInMillis = it }
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            deadline = calendar.timeInMillis
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        Text(text = "Add/Edit Task")

        Spacer(modifier = Modifier.height(16.dp))

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

        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { isCompleted = it }
            )
            Text(text = "Completed")
        }

        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(text = "Deadline: ${deadline?.let { dateFormatter.format(it) } ?: "No deadline"}")
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { datePickerDialog.show() }) {
                Text(text = "Set Deadline")
            }
        }

        TextField(
            value = assignedTo,
            onValueChange = { assignedTo = it },
            label = { Text("Assign To") },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Text(text = "Subtasks:")
        subTasks.forEachIndexed { index, subTask ->
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                TextField(
                    value = subTask.title,
                    onValueChange = {
                        subTasks = subTasks.toMutableList().apply {
                            set(index, subTask.copy(title = it))
                        }
                    },
                    label = { Text("Subtask ${index + 1}") },
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .weight(1f)
                )
                IconButton(onClick = {
                    subTasks = subTasks.toMutableList().apply {
                        removeAt(index)
                    }
                }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Remove subtask")
                }
            }
        }
        Button(onClick = {
            subTasks = subTasks + SubTask(
                subTaskId = System.currentTimeMillis().toString(),
                title = "",
                completed = false
            )
        }) {
            Text(text = "Add Subtask")
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    if (taskId == null) {
                        taskViewModel.addTask(
                            Task(
                                taskId = System.currentTimeMillis().toString(),
                                title = taskTitle,
                                description = taskDescription,
                                userId = "yourUserId",
                                completed = isCompleted,
                                dueDate = deadline,
                                assignedTo = assignedTo,
                                subTasks = subTasks
                            )
                        )
                    } else {
                        val updatedTask = Task(
                            taskId = taskId,
                            title = taskTitle,
                            description = taskDescription,
                            userId = "yourUserId",
                            completed = isCompleted,
                            dueDate = deadline,
                            assignedTo = assignedTo,
                            subTasks = subTasks
                        )
                        taskViewModel.updateTask(updatedTask)
                    }
                    navController.navigateUp()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Save Task")
            }

            if (taskId != null) {
                Button(
                    onClick = {
                        taskViewModel.deleteTask(taskId, "yourUserId")
                        navController.navigateUp()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Delete Task")
                }
            }
        }
    }
}
