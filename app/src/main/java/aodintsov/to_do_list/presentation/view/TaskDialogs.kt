package aodintsov.to_do_list.view

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import aodintsov.to_do_list.R
import aodintsov.to_do_list.data.model.Task
import aodintsov.to_do_list.presentation.viewmodel.TaskViewModel

@Composable
fun ShowArchiveConfirmationDialog(task: Task, taskViewModel: TaskViewModel) {
    val showDialog = remember { mutableStateOf(true) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
            },
            title = {
                Text(text = if (task.archived) stringResource(id = R.string.unarchive_task) else stringResource(id = R.string.archive_task))
            },
            text = {
                Text(text = if (task.archived) stringResource(id = R.string.unarchive_task_confirm) else stringResource(id = R.string.archive_task_confirm))
            },
            confirmButton = {
                Button(onClick = {
                    if (task.archived) {
                        taskViewModel.unarchiveTask(task)
                    } else {
                        taskViewModel.archiveTask(task)
                    }
                    taskViewModel.fetchTasks(task.userId)
                    showDialog.value = false
                }) {
                    Text(text = stringResource(id = R.string.yes))
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDialog.value = false
                }) {
                    Text(text = stringResource(id = R.string.no))
                }
            }
        )
    }
}
