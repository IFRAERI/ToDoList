package aodintsov.to_do_list.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import aodintsov.to_do_list.R
import aodintsov.to_do_list.model.SubTask
import aodintsov.to_do_list.viewmodel.TaskViewModel
import aodintsov.to_do_list.viewmodel.TaskViewModelFactory
import aodintsov.to_do_list.view.components.*
import java.util.*
import aodintsov.to_do_list.model.Task


@Composable
fun AddEditTaskScreen(
    navController: NavController,
    userId: String,
    taskId: String?,
    taskViewModelFactory: TaskViewModelFactory,
    modifier: Modifier = Modifier
) {
    val taskViewModel: TaskViewModel = viewModel(factory = taskViewModelFactory)
    var taskTitle by rememberSaveable { mutableStateOf("") }
    var taskDescription by rememberSaveable { mutableStateOf("") }
    var subTasks by rememberSaveable { mutableStateOf(listOf<SubTask>()) }
    var isPriority by rememberSaveable { mutableStateOf(false) }
    var isCompleted by rememberSaveable { mutableStateOf(false) }
    var deadline by rememberSaveable { mutableStateOf<Long?>(null) }
    var assignedTo by rememberSaveable { mutableStateOf("") }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var showSnackbar by rememberSaveable { mutableStateOf(false) }
    var snackbarMessage by rememberSaveable { mutableStateOf("") }
    val emptyFieldsError = stringResource(R.string.empty_fields_message)
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    deadline?.let { calendar.timeInMillis = it }

    // Fetch tasks when screen is loaded
    LaunchedEffect(taskId) {
        taskViewModel.fetchTasks(userId)
    }

    // Load task details if taskId is not null
    val tasks by taskViewModel.tasks.observeAsState()
    LaunchedEffect(tasks) {
        if (tasks != null && taskId != null) {
            val task = taskViewModel.getTaskById(taskId)
            task?.let {
                taskTitle = it.title
                taskDescription = it.description
                isCompleted = it.completed
                deadline = it.dueDate
                assignedTo = it.assignedTo
                subTasks = it.subTasks
                isPriority = it.priority
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TitleInput(
                title = taskTitle,
                onTitleChange = { taskTitle = it }
            )

            DescriptionInput(
                description = taskDescription,
                onDescriptionChange = { taskDescription = it }
            )

            CompletionCheckbox(
                isCompleted = isCompleted,
                subTasks = subTasks,
                onCompletionChange = { isCompleted = it }
            )

            PriorityCheckbox(
                isPriority = isPriority,
                onPriorityChange = { isPriority = it },
                isDeadlineSet = deadline != null
            )

            DeadlinePicker(
                deadline = deadline,
                onDatePick = { selectedCalendar ->
                    deadline = selectedCalendar.timeInMillis
                    isPriority = true // Установка высокого приоритета при наличии дедлайна
                },
                onClearDeadline = {
                    deadline = null
                    isPriority = false
                }
            )

            SubTasksSection(
                subTasks = subTasks,
                onAddSubTask = {
                    subTasks = subTasks + SubTask(
                        subTaskId = System.currentTimeMillis().toString(),
                        title = "",
                        completed = false
                    )
                },
                onSubTasksChange = { updatedSubTasks ->
                    subTasks = updatedSubTasks
                }
            )

            ActionButtons(
                taskId = taskId,
                taskTitle = taskTitle,
                taskDescription = taskDescription,
                userId = userId,
                isCompleted = isCompleted,
                deadline = deadline,
                assignedTo = assignedTo,
                subTasks = subTasks,
                priorityTask = isPriority,
                navController = navController,
                taskViewModel = taskViewModel,
                onShowSnackbar = { message ->
                    snackbarMessage = message
                    showSnackbar = true
                },
                onSaveTask = {
                    if (taskTitle.isBlank() || taskDescription.isBlank()) {
                        snackbarMessage = emptyFieldsError
                        showSnackbar = true
                    } else {
                        subTasks = subTasks.filter { it.title.isNotBlank() }
                        if (taskId == null) {
                            taskViewModel.addTask(
                                Task(
                                    taskId = System.currentTimeMillis().toString(),
                                    title = taskTitle,
                                    description = taskDescription,
                                    userId = userId,
                                    completed = isCompleted,
                                    dueDate = deadline,
                                    assignedTo = assignedTo,
                                    subTasks = subTasks,
                                    priority = isPriority,
                                    createdAt = System.currentTimeMillis(),
                                    completionDate = if (isCompleted) System.currentTimeMillis() else null
                                )
                            )
                        } else {
                            val updatedTask = Task(
                                taskId = taskId,
                                title = taskTitle,
                                description = taskDescription,
                                userId = userId,
                                completed = isCompleted,
                                dueDate = deadline,
                                assignedTo = assignedTo,
                                subTasks = subTasks,
                                priority = isPriority,
                                completionDate = if (isCompleted) System.currentTimeMillis() else null
                            )
                            taskViewModel.updateTask(updatedTask)
                        }
                        navController.navigateUp()
                    }
                }
            )


            if (showSnackbar) {
            Snackbar(
                action = {
                    Button(onClick = { showSnackbar = false }) {
                        Text(stringResource(R.string.dismiss))
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = snackbarMessage)
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(text = stringResource(R.string.confirm_delete_title)) },
                text = { Text(text = stringResource(R.string.confirm_delete_message)) },
                confirmButton = {
                    Button(
                        onClick = {
                            taskViewModel.deleteTask(taskId!!, userId)
                            navController.navigateUp()
                            showDeleteDialog = false
                        }
                    ) {
                        Text(text = stringResource(R.string.confirm_delete))
                    }
                },
                dismissButton = {
                    Button(onClick = { showDeleteDialog = false }) {
                        Text(text = stringResource(R.string.cancel_delete))
                    }
                }
            )
        }
    }
}}
