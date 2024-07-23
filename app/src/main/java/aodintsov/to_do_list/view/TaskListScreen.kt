package aodintsov.to_do_list.view

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import aodintsov.to_do_list.R
import aodintsov.to_do_list.model.Task
import aodintsov.to_do_list.viewmodel.AuthViewModel
import aodintsov.to_do_list.viewmodel.AuthViewModelFactory
import aodintsov.to_do_list.viewmodel.TaskViewModel
import aodintsov.to_do_list.viewmodel.TaskViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
//import androidx.compose.material3.Divider
//import androidx.room.parser.expansion.ExpandableSection

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
    var filterState by remember { mutableIntStateOf(0) }
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val today = dateFormatter.format(Date())
    val calendar = Calendar.getInstance()
    calendar.time = Date()
    calendar.add(Calendar.DAY_OF_YEAR, -7)
    val sevenDaysAgo = dateFormatter.format(calendar.time)
//    var filteredTasks: List<Task>
//    val groupedTasks = filteredTasks.groupBy {




    val filteredTasks = when (filterState) {
        1 -> tasks.filter { it.completed }
        2 -> tasks.filter { !it.completed }
        else -> tasks
    }
    val highPriorityTasks = filteredTasks.filter { it.priority && !it.completed }
    val groupedTasks = filteredTasks.groupBy { task ->
        dateFormatter.format(Date(task.createdAt ?: 0))
    }.toMutableMap()
    groupedTasks[today] = (groupedTasks[today] ?: emptyList()) + highPriorityTasks.filterNot { task ->
        groupedTasks[today]?.contains(task) == true
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
                title = { Text(stringResource(id = R.string.task_list_title)) },
                actions = {
                    var searchQuery by remember { mutableStateOf("") }
                    Row {
                        TextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                                taskViewModel.searchTasks(it)
                            },
                            label = { Text(stringResource(id = R.string.search_label)) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.5f
                                )
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
                            Icon(
                                imageVector = icon,
                                contentDescription = stringResource(id = R.string.filter_tasks)
                            )
                        }

                        IconButton(onClick = {
                            taskViewModel.toggleSortOrder()
                        }) {
                            Icon(
                                imageVector = if (isAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = stringResource(id = R.string.sort)
                            )
                        }

                        IconButton(onClick = {
                            showLogoutDialog = true
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.Logout,
                                contentDescription = stringResource(id = R.string.logout)
                            )
                        }
                    }


                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (filteredTasks.isEmpty()) {
                    Text(text = stringResource(id = R.string.no_tasks_available))
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        groupedTasks.forEach { (date, tasks) ->
                            item {
                                val isExpanded = date>= sevenDaysAgo

                                ExpandableSection(title = date, initiallyExpanded = date == today) {
                                    tasks.forEach { task ->
                                        TaskItem(task = task, onLongClick = {
                                            Log.d(
                                                "TaskListScreen",
                                                "Navigating to addEditTask with taskId: ${task.taskId}"
                                            )
                                            navController.navigate("addEditTask/${task.taskId}")
                                        })
                                        //HorizontalDivider()
                                        RoundedDivider(modifier = Modifier.padding(vertical = 4.dp))

                                    }
                                }
                            }
//                        items(filteredTasks) { task ->
//                            TaskItem(task = task, onLongClick = {
//                                Log.d("TaskListScreen", "Navigating to addEditTask with taskId: ${task.taskId}")
//                                navController.navigate("addEditTask/${task.taskId}")
//                            })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { navController.navigate("addEditTask") },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = stringResource(id = R.string.add_task))
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(id = R.string.confirm_logout)) },
            text = { Text(stringResource(id = R.string.logout_message)) },
            confirmButton = {
                Button(onClick = {
                    authViewModel.signOut {}
                    navController.navigate("login") {
                        popUpTo("taskList") { inclusive = true }
                    }
                    showLogoutDialog = false
                }) {
                    Text(stringResource(id = R.string.logout_confirm))
                }
            },
            dismissButton = {
                Button(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(id = R.string.logout_cancel))
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
    val rotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(if (isOverdue) Color.Red else MaterialTheme.colorScheme.surface)
            .combinedClickable(
                onClick = { isExpanded = !isExpanded },
                onLongClick = onLongClick
            )
            .animateContentSize()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = task.title,
                        modifier = Modifier.weight(1f)
                    )
                    if (task.completed && !isExpanded) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.Green,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                if (isExpanded) {
                    IconButton(onClick = { onLongClick() }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Edit Task")
                    }
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier.rotate(rotation)
                )
            }
            if (isExpanded) {
                Text(
                    text = task.description,
                )
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = task.completed,
                        onCheckedChange = null // Task completion state is read-only in this context
                    )
                    Text(text = stringResource(id = R.string.completed))
                }
                task.dueDate?.let {
                    val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(it))
                    Text(text = stringResource(id = R.string.deadline, formattedDate))
                }
            }
        }
    }
}



@Composable
fun ExpandableSection(title: String, initiallyExpanded: Boolean = false, content: @Composable () -> Unit) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }
    val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize()  // Добавление анимации изменения размера
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                modifier = Modifier.rotate(rotation)
            )
        }
        if (expanded) {
            content()
        }
    }
}


@Composable
fun RoundedDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp)
            )
    )
}