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
import androidx.compose.material.icons.filled.*
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
    var filterState by remember { mutableStateOf(0) }

    val filteredTasks = when (filterState) {
        1 -> tasks.filter { it.completed }
        2 -> tasks.filter { !it.completed }
        else -> tasks
    }

    LaunchedEffect(Unit) {
        val currentUserId = authViewModel.getCurrentUserId() ?: userId
        taskViewModel.fetchTasks(currentUserId)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
        .padding(start = 8.dp, end = 8.dp),
        topBar = {
            TopAppBar(
                title = { Text("Task List") },
                actions = {
                    var searchQuery by remember { mutableStateOf("") }
                    Row {
                        TextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                                taskViewModel.searchTasks(it)
                            },
                            label = { Text("Search") },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .weight(0.65f)
                                //.padding(8.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.weight(0.05f))

                        IconButton(onClick = {
                            filterState = (filterState + 1) % 3
                        }) {
                            val icon = when (filterState) {
                                1 -> Icons.Default.Check
                                2 -> Icons.Default.Clear
                                else -> Icons.Default.Circle
                            }
                            Icon(imageVector = icon, contentDescription = "Filter Tasks")
                        }

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


                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background)
        ) {
            Column(modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
            ) {
                if (filteredTasks.isEmpty()) {
                    Text(text = "No tasks available.")
                } else {
                    LazyColumn(modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                    ) {
                        items(filteredTasks) { task ->
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
    var isExpanded by remember { mutableStateOf(false) }
    val currentTime = System.currentTimeMillis()
    val isOverdue = task.dueDate?.let { it < currentTime && !task.completed } ?: false

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(if (isOverdue) Color.Red else MaterialTheme.colorScheme.surface)
            .combinedClickable(
                onClick = { isExpanded = !isExpanded },
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
                maxLines = if (isExpanded) Int.MAX_VALUE else 4, // Limit description to 4 lines if not expanded
            )
//            Text(
//                text = "Created on: ${SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(task.taskId.toLong()))}",
//                style = MaterialTheme.typography.bodySmall,
//                color = Color.Gray
//            )
            if (isExpanded) {
                Column {
                    task.subTasks.forEach { subTask ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 26.dp, top = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (subTask.completed) Icons.Default.Check else Icons.Default.Clear,
                                contentDescription = null,
                                tint = if (subTask.completed) Color.Green else Color.Red
                            )
                            Text(
                                text = subTask.title,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
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
