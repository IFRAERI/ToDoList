package aodintsov.to_do_list.view

import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
//import androidx.test.espresso.base.Default

import aodintsov.to_do_list.R
import aodintsov.to_do_list.model.Task
import aodintsov.to_do_list.utils.*
import aodintsov.to_do_list.viewmodel.TaskViewModel
import java.util.Date
import java.util.Locale
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TaskItem(
    task: Task,
    onLongClick: () -> Unit,
    navController: NavController,
    taskViewModel: TaskViewModel // Передайте TaskViewModel в эту функцию
) {
    Log.d("TaskItem", "Rendering TaskItem for task: ${task.taskId}")

    val taskItemState = rememberTaskItemState(task)
    val dismissState = rememberSwipeToDismissState(
        task = task,
        onSwipe = {
            if (task.archived) {
                Log.d("TaskItem", "Unarchiving task: ${task.taskId}")
                taskViewModel.unarchiveTask(task) // Вызов метода разархивации
            } else {
                Log.d("TaskItem", "Archiving task: ${task.taskId}")
                taskViewModel.archiveTask(task) // Вызов метода архивации
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background) // Используйте основной цвет фона
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Archive", tint = Color.White)
            }
        },
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(if (taskItemState.isOverdue) Color.Red else MaterialTheme.colorScheme.surface)
                    .combinedClickable(
                        onClick = {
                            taskItemState.isExpanded.value = !taskItemState.isExpanded.value
                            Log.d("TaskItem", "Task expanded state changed for task: ${task.taskId}")
                        },
                        onLongClick = onLongClick
                    )
                    .animateContentSize()
            ) {
                TaskContent(task, taskItemState, navController)
            }
        }
    )
}


@Composable
fun TaskContent(task: Task, taskItemState: TaskItemState, navController: NavController) {
    Log.d("TaskContent", "Rendering TaskContent for task: ${task.taskId}")
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
                if (task.priority) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "High Priority",
                        tint = Color.Red,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                if (task.completed && !taskItemState.isExpanded.value) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.Green,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            if (taskItemState.isExpanded.value) {
                Log.d("TaskContent", "Showing edit option for task: ${task.taskId}")
                IconButton(onClick = {
                    if (!task.archived) { // Логика для перехода на экран редактирования, если задача не архивирована
                        navController.navigate("addEditTask/${task.taskId}")
                    }
                }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Edit Task")
                }
            }
            Icon(
                imageVector = if (taskItemState.isExpanded.value) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                modifier = Modifier.rotate(taskItemState.rotation)
            )
        }
        if (taskItemState.isExpanded.value) {
           // Log.d("TaskContent", "Showing edit option for task: ${task.taskId}")
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
