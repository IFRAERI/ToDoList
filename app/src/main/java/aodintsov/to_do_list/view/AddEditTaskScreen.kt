package aodintsov.to_do_list.view

import aodintsov.to_do_list.view.components.TitleInput
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
import android.widget.Toast

@Composable
fun AddEditTaskScreen(
    navController: NavController,
    userId: String,
    taskId: String?,
    taskViewModelFactory: TaskViewModelFactory,
    modifier: Modifier = Modifier
) {
    var userIdentifier by rememberSaveable { mutableStateOf(userId) }
    var activationTime by rememberSaveable { mutableStateOf<Long?>(null) }
    var isDeferred by rememberSaveable { mutableStateOf(false) }
    val taskViewModel: TaskViewModel = viewModel(factory = taskViewModelFactory)
    var taskTitle by rememberSaveable { mutableStateOf("") }
    var taskDescription by rememberSaveable { mutableStateOf("") }
    var subTasks by rememberSaveable { mutableStateOf(listOf<SubTask>()) }
    var isPriority by rememberSaveable { mutableStateOf(false) }
    var isCompleted by rememberSaveable { mutableStateOf(false) }
    var deadline by rememberSaveable { mutableStateOf<Long?>(null) }
    var assignedTo by rememberSaveable { mutableStateOf("") }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val emptyFieldsError = stringResource(R.string.empty_fields_message)
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
                activationTime = it.activationTime
                isDeferred = it.isDeferred
                userIdentifier = it.userId
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 0.dp)

            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
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
                    isPriority = true
                },
                onClearDeadline = {
                    deadline = null
                    isPriority = false
                }
            )

            DeferredDateTimePicker(
                deferredUntil = activationTime,
                onDeferredDateTimeChange = { selectedTime ->
                    activationTime = selectedTime
                    isDeferred = selectedTime != null
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
                userId = userIdentifier,
                navController = navController,
                taskViewModel = taskViewModel,
                onSaveTask = {
                    if (taskTitle.isBlank() || taskDescription.isBlank()) {
                        Toast.makeText(context, emptyFieldsError, Toast.LENGTH_SHORT).show()
                    } else {
                        subTasks = subTasks.filter { it.title.isNotBlank() }
                        if (taskId == null) {
                            taskViewModel.addTask(
                                Task(
                                    taskId = System.currentTimeMillis().toString(),
                                    title = taskTitle,
                                    description = taskDescription,
                                    userId = userIdentifier,
                                    completed = isCompleted,
                                    dueDate = deadline,
                                    assignedTo = assignedTo,
                                    subTasks = subTasks,
                                    priority = isPriority,
                                    activationTime = activationTime,
                                    isDeferred = isDeferred,
                                    createdAt = System.currentTimeMillis(),
                                    completionDate = if (isCompleted) System.currentTimeMillis() else null
                                )
                            )
                        } else {
                            val updatedTask = Task(
                                taskId = taskId,
                                title = taskTitle,
                                description = taskDescription,
                                userId = userIdentifier,
                                completed = isCompleted,
                                dueDate = deadline,
                                assignedTo = assignedTo,
                                subTasks = subTasks,
                                activationTime = activationTime,
                                isDeferred = isDeferred,
                                priority = isPriority,
                                completionDate = if (isCompleted) System.currentTimeMillis() else null
                            )
                            taskViewModel.updateTask(updatedTask)
                        }
                        navController.navigateUp()
                    }
                }
            )

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
    }
}