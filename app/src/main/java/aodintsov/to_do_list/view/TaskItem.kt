package aodintsov.to_do_list.view

import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import aodintsov.to_do_list.R
import aodintsov.to_do_list.model.Task
import aodintsov.to_do_list.utils.*
import aodintsov.to_do_list.viewmodel.TaskViewModel
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun TaskItem(
    task: Task,
    onLongClick: () -> Unit,
    navController: NavController,
    taskViewModel: TaskViewModel
) {
    var showDialog by remember { mutableStateOf(false) }

    Log.d("TaskItem", "Rendering TaskItem for task: ${task.taskId}")

    val taskItemState = rememberTaskItemState(task)
    val dismissState = rememberDismissState(
        confirmStateChange = { dismissValue ->
            if (dismissValue == DismissValue.DismissedToEnd || dismissValue == DismissValue.DismissedToStart) {
                showDialog = true
                false
            } else {
                false
            }
        }
    )

    SwipeToDismiss(
        state = dismissState,
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.archive), tint = Color.White)
            }
        },
        dismissContent = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(if (taskItemState.isOverdue) Color.Red else MaterialTheme.colors.surface)
                    .combinedClickable(
                        onClick = {
                            taskItemState.isExpanded.value = !taskItemState.isExpanded.value
                        },
                        onLongClick = onLongClick
                    )
                    .animateContentSize()
            ) {
                TaskContent(task, taskItemState, navController)
            }
        }
    )

    if (showDialog) {
        ShowArchiveConfirmationDialog(task, taskViewModel)
    }
}
@Composable
fun TaskContent(task: Task, taskItemState: TaskItemState, navController: NavController) {
    val deferredTaskDescription = stringResource(R.string.deferred_task)
    val archivedTaskDescription = stringResource(R.string.archived_task)
    val highPriorityDescription = stringResource(R.string.high_priority)
    val completedDescription = stringResource(R.string.completed)
    val editTaskDescription = stringResource(R.string.edit_task)

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
                if (task.isDeferred) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = deferredTaskDescription,
                        tint = Color.Blue,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                if (task.archived) {
                    Icon(
                        imageVector = Icons.Default.Archive,
                        contentDescription = archivedTaskDescription,
                        tint = Color.Gray,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                if (task.priority) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = highPriorityDescription,
                        tint = Color.Red,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                if (task.completed && !taskItemState.isExpanded.value) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = completedDescription,
                        tint = Color.Green,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            if (taskItemState.isExpanded.value) {
                IconButton(onClick = {
                    if (!task.archived) {
                        navController.navigate("addEditTask/${task.taskId}")
                    }
                }) {
                    Icon(Icons.Default.MoreVert, contentDescription = editTaskDescription)
                }
            }
            Icon(
                imageVector = if (taskItemState.isExpanded.value) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                modifier = Modifier.rotate(taskItemState.rotation)
            )
        }
        if (taskItemState.isExpanded.value) {
            Text(text = task.description)
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
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = task.completed,
                    onCheckedChange = null
                )
                Text(text = stringResource(id = R.string.completed))
            }
            task.dueDate?.let {
                val formattedDate = SimpleDateFormat(stringResource(R.string.date_format), Locale.US).format(Date(it))
                Text(text = stringResource(id = R.string.deadline, formattedDate))
            }
        }
    }
}
