package aodintsov.to_do_list.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import aodintsov.to_do_list.R
import aodintsov.to_do_list.model.TaskFilter
import aodintsov.to_do_list.viewmodel.AuthViewModel
import aodintsov.to_do_list.viewmodel.AuthViewModelFactory
import aodintsov.to_do_list.viewmodel.TaskViewModel
import aodintsov.to_do_list.viewmodel.TaskViewModelFactory
import aodintsov.to_do_list.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun TaskListScreen(
    navController: NavController,
    userId: String,
    taskViewModelFactory: TaskViewModelFactory,
    authViewModelFactory: AuthViewModelFactory,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel
) {

    val taskViewModel: TaskViewModel = viewModel(factory = taskViewModelFactory)
    val tasks by taskViewModel.tasks.observeAsState(emptyList())
    val isAscending by taskViewModel.isAscending.observeAsState(true)
    val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
    var showLogoutDialog by remember { mutableStateOf(false) }
    var filterState by remember { mutableStateOf(TaskFilter.ALL) }
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val today = dateFormatter.format(Date())
    var searchQuery by remember { mutableStateOf("") }
    val completedTaskCount by taskViewModel.completedTaskCount.observeAsState(0)
    val totalTaskCount by taskViewModel.totalTaskCount.observeAsState(0)

    val filteredTasks = tasks.filter {
        Log.d("TaskListScreen", "Filtering task: ${it.title}, Archived: ${it.archived}, Deferred: ${it.isDeferred}")
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
    }.filter { task ->
        when (filterState) {
            TaskFilter.COMPLETED -> task.completed
            TaskFilter.UNCOMPLETED -> !task.completed
            TaskFilter.ARCHIVED -> task.archived
            TaskFilter.DEFERRED -> task.isDeferred && task.activationTime?.let { it > System.currentTimeMillis() } == true
            TaskFilter.ALL -> !task.archived && (!task.isDeferred || task.activationTime?.let { it <= System.currentTimeMillis() } == true)
        }
    }

    val groupedTasks = filteredTasks.groupBy { task ->
        dateFormatter.format(Date(task.createdAt))
    }.filter { (_, groupTasks) ->
        groupTasks.isNotEmpty()
    }

    LaunchedEffect(Unit) {
        val currentUserId = authViewModel.getCurrentUserId() ?: userId
        taskViewModel.fetchTasks(currentUserId)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 0.dp, end = 0.dp),
        topBar = {
            Text(text = "Testing changes")
            TaskListTopBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { newValue -> searchQuery = newValue },
                filterState = filterState,
                onFilterChange = {
                    filterState = when (filterState) {
                        TaskFilter.ALL -> TaskFilter.ARCHIVED
                        TaskFilter.ARCHIVED -> TaskFilter.DEFERRED
                        TaskFilter.DEFERRED -> TaskFilter.COMPLETED
                        TaskFilter.COMPLETED -> TaskFilter.UNCOMPLETED
                        TaskFilter.UNCOMPLETED -> TaskFilter.ALL
                    }
                },
                isAscending = isAscending,
                onSortOrderChange = { taskViewModel.toggleSortOrder() },
                onLogoutClick = { showLogoutDialog = true }
            )
        },
        bottomBar = {
            BottomBarContent(
                completedTaskCount = completedTaskCount,
                totalTaskCount = totalTaskCount
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addEditTask") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_task)
                )
            }
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
                    .padding(4.dp)
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
                                ExpandableSection(title = date, initiallyExpanded = date == today) {
                                    tasks.forEach { task ->
                                        key(task.taskId) {
                                            TaskItem(
                                                task = task,
                                                onLongClick = {
                                                    navController.navigate("addEditTask/${task.taskId}")
                                                },
                                                navController = navController,
                                                taskViewModel = taskViewModel
                                            )
                                            RoundedDivider(modifier = Modifier.padding(vertical = 4.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    BannerAdView(modifier = Modifier.fillMaxWidth())
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