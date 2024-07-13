package aodintsov.to_do_list.view

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import aodintsov.to_do_list.model.Task
import aodintsov.to_do_list.viewmodel.AuthViewModel
import aodintsov.to_do_list.viewmodel.AuthViewModelFactory
import aodintsov.to_do_list.viewmodel.TaskViewModel
import aodintsov.to_do_list.viewmodel.TaskViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    navController: NavController,
    userId: String,
    taskViewModelFactory: TaskViewModelFactory,
    authViewModelFactory: AuthViewModelFactory,
    modifier: Modifier = Modifier
) {
    val taskViewModel: TaskViewModel = viewModel(factory = taskViewModelFactory)
    val tasks by taskViewModel.tasks.observeAsState(emptyList())
    val isAscending by taskViewModel.isAscending.observeAsState(true)
    val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val currentUserId = authViewModel.getCurrentUserId() ?: userId
        taskViewModel.fetchTasks(currentUserId)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Task List") },
                actions = {

                    var searchQuery by remember { mutableStateOf("") }

                    TextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            taskViewModel.searchTasks(it)
                        },
                        label = { Text("Search") },
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    IconButton(onClick = {
                        taskViewModel.toggleSortOrder()
                    }) {
                        Icon(
                            imageVector = if (isAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                            contentDescription = "Sort"
                        )
                    }
                    IconButton(onClick = {
                        showLogoutDialog = true
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
                if (tasks.isEmpty()) {
                    Text(text = "No tasks available.")
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize().weight(1f)) {
                        items(tasks) { task ->
                            TaskItem(task = task, onLongClick = {
                                Log.d("TaskListScreen", "Navigating to addEditTask with taskId: ${task.taskId}")
                                navController.navigate("addEditTask/${task.taskId}")
                            })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { navController.navigate("addEditTask") },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Add Task")
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                Button(onClick = {
                    authViewModel.signOut{}
                    navController.navigate("login") {
                        popUpTo("taskList") { inclusive = true }
                    }
                    showLogoutDialog = false
                }) {
                    Text("Logout")
                }
            },
            dismissButton = {
                Button(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskItem(task: Task, onLongClick: () -> Unit) {
    val currentTime = System.currentTimeMillis()
    val isOverdue = task.dueDate?.let { it < currentTime && !task.completed } ?: false

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(if (isOverdue) Color.Red else MaterialTheme.colorScheme.background)
            .combinedClickable(
                onClick = { /* Do nothing on click */ },
                onLongClick = onLongClick
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = task.title, modifier = Modifier.weight(1f))
                IconButton(onClick = { onLongClick() }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Edit Task")
                }
            }
            Text(
                text = task.description,
                maxLines = 4 // Limit description to 4 lines
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = task.completed,
                    onCheckedChange = null // Task completion state is read-only in this context
                )
                Text(text = "Completed")
            }
            task.dueDate?.let {
                val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(it))
                Text(text = "Deadline: $formattedDate")
            }
        }
    }
}
