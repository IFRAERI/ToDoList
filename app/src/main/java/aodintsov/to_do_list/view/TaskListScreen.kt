package aodintsov.to_do_list.view

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    var searchQuery by remember { mutableStateOf("") }
    var showArchived by remember { mutableStateOf(false) }

    val filteredTasks = tasks.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
    }.filter { task ->
        when (filterState) {
            1 -> task.completed
            2 -> !task.completed
            else -> true
        }
    }.filter { task ->
        task.archived == showArchived
    }

    val groupedTasks = filteredTasks.groupBy { task ->
        dateFormatter.format(Date(task.createdAt ?: 0))
    }.filter { (_, groupTasks) ->
        groupTasks.isNotEmpty()
    }

    LaunchedEffect(Unit) {
        val currentUserId = authViewModel.getCurrentUserId() ?: userId
        taskViewModel.fetchTasks(currentUserId)
        Log.d("TaskListScreen", "Tasks fetched for user: $currentUserId")
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 8.dp, end = 8.dp),
        topBar = {
            TaskListTopBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { newValue -> searchQuery = newValue },
                filterState = filterState,
                onFilterChange = { filterState = (filterState + 1) % 3 },
                isAscending = isAscending,
                onSortOrderChange = { taskViewModel.toggleSortOrder() },
                showArchived = showArchived,
                onArchiveToggle = { showArchived = !showArchived },
                onLogoutClick = { showLogoutDialog = true }
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
                                ExpandableSection(title = date, initiallyExpanded = date == today) {
                                    tasks.forEach { task ->
                                        key(task.taskId) {  // Уникальный ключ для каждого элемента
                                            TaskItem(
                                                task = task,
                                                onLongClick = {
                                                    Log.d("TaskListScreen", "Navigating to addEditTask with taskId: ${task.taskId}")
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
