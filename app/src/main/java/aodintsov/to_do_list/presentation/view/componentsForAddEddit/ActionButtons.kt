package aodintsov.to_do_list.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import aodintsov.to_do_list.R
//import aodintsov.to_do_list.model.SubTask
//import aodintsov.to_do_list.model.Task
import aodintsov.to_do_list.presentation.viewmodel.TaskViewModel

@Composable
fun ActionButtons(
    taskId: String?,
   // taskTitle: String,
  //  taskDescription: String,
    userId: String,
  //  isCompleted: Boolean,
 //   deadline: Long?,
 //   assignedTo: String,
  //  subTasks: List<SubTask>,
 //   priorityTask: Boolean,
    navController: NavController,
    taskViewModel: TaskViewModel,
//    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSaveTask: () -> Unit // Логика сохранения будет передаваться извне
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
        //val emptyFieldsError = stringResource(R.string.empty_fields_message)

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Button(
            onClick = {
                onSaveTask() // Вызов переданной логики сохранения
            },
            modifier = Modifier.weight(1f)
        ) {
            Text(text = stringResource(R.string.save_task))
        }

        if (taskId != null) {
            Button(
                onClick = {
                    showDeleteDialog = true
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.delete_task))
            }
        }
    }

    if (showDeleteDialog) {
        DeleteTaskDialog(
            onDeleteConfirmed = {
                taskViewModel.deleteTask(taskId!!, userId)
                navController.navigateUp()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
fun DeleteTaskDialog(
    onDeleteConfirmed: () -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.confirm_delete_title)) },
        text = { Text(text = stringResource(R.string.confirm_delete_message)) },
        confirmButton = {
            Button(onClick = onDeleteConfirmed) {
                Text(text = stringResource(R.string.confirm_delete))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel_delete))
            }
        }
    )
}