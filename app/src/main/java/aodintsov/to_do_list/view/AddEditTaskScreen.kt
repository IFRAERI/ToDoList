package aodintsov.to_do_list.view

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.BoxScopeInstance.align
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
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import aodintsov.to_do_list.R
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    navController: NavController,
    userId: String,
    taskId: String?,
    taskViewModelFactory: TaskViewModelFactory,
    modifier: Modifier = Modifier
) {
    var priorityTask by rememberSaveable { mutableStateOf(false) }
    val taskViewModel: TaskViewModel = viewModel(factory = taskViewModelFactory)
    var taskTitle by rememberSaveable { mutableStateOf("") }
    var taskDescription by rememberSaveable { mutableStateOf("") }
    var taskLoaded by rememberSaveable { mutableStateOf(false) }
    var isCompleted by rememberSaveable { mutableStateOf(false) }
    var deadline by rememberSaveable { mutableStateOf<Long?>(null) }
    var assignedTo by rememberSaveable { mutableStateOf("") }
    var subTasks by rememberSaveable { mutableStateOf(listOf<SubTask>()) }
    var completionDate by rememberSaveable { mutableStateOf<Long?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(taskId) {
        Log.d("AddEditTaskScreen", "LaunchedEffect triggered with taskId: $taskId")
        taskViewModel.fetchTasks(userId)
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
                priorityTask = task.priority
                completionDate = task.completionDate
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
            priorityTask = true // Автоматически устанавливаем высокий приоритет при установке дедлайна
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 0.dp)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.add_edit_task_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = taskTitle,
                onValueChange = { taskTitle = it },
                label = { Text(stringResource(R.string.title_label)) },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            TextField(
                value = taskDescription,
                onValueChange = { taskDescription = it },
                label = { Text(stringResource(R.string.description_label)) },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = {
                        if (subTasks.all { it.completed } || subTasks.isEmpty()) {
                            isCompleted = it
                            completionDate = if (it) System.currentTimeMillis() else null
                        }
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(text = stringResource(R.string.completed), color = MaterialTheme.colorScheme.onBackground)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Checkbox(
                    checked = priorityTask,
                    onCheckedChange = { if (deadline == null) priorityTask = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = stringResource(R.string.priority),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                MyDeadlineText(deadline)
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {
                    if (deadline != null) {
                        deadline = null
                        priorityTask = false
                    } else {
                        datePickerDialog.show()
                    }
                }) {
                    Text(text = if (deadline != null) stringResource(R.string.remove_deadline) else stringResource(R.string.set_deadline))
                }
            }

            Text(text = stringResource(R.string.subtasks_label), color = MaterialTheme.colorScheme.onBackground)
            subTasks.forEachIndexed { index, subTask ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = subTask.title,
                        onValueChange = {
                            subTasks = subTasks.toMutableList().apply {
                                set(index, subTask.copy(title = it))
                            }
                        },
                        label = { Text(stringResource(R.string.subtasks_label, index + 1)) },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .weight(1f)
                    )
                    Checkbox(
                        checked = subTask.completed,
                        onCheckedChange = {
                            subTasks = subTasks.toMutableList().apply {
                                set(index, subTask.copy(completed = it))
                            }
                        },
                        enabled = subTask.title.isNotBlank(),
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
            Button(onClick = {
                subTasks = subTasks + SubTask(
                    subTaskId = System.currentTimeMillis().toString(),
                    title = "",
                    completed = false
                )
            }) {
                Text(text = stringResource(R.string.add_subtask))
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (taskTitle.isBlank() || taskDescription.isBlank()) {
                            scope.launch {
                                snackbarHostState.showSnackbar(R.string.empty_fields_message.toString())
                            }
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
                                        priority = priorityTask,
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
                                    priority = priorityTask,
                                    completionDate = if (isCompleted) System.currentTimeMillis() else null
                                )
                                taskViewModel.updateTask(updatedTask)
                            }
                            navController.navigateUp()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.save_task))
                }

                if (taskId != null) {
                    Button(
                        onClick = {
                            taskViewModel.deleteTask(taskId, userId)
                            navController.navigateUp()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = stringResource(R.string.delete_task))
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun MyDeadlineText(deadline: Long?) {
    val context = LocalContext.current
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    val deadlineText = deadline?.let {
        dateFormatter.format(it)
    } ?: stringResource(id = R.string.no_deadline)

    Text(
        text = stringResource(id = R.string.deadline, deadlineText),
        color = MaterialTheme.colorScheme.onBackground
    )
}