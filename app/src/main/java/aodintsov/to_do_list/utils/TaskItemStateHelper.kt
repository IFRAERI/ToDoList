package aodintsov.to_do_list.utils

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import aodintsov.to_do_list.model.Task

data class TaskItemState(
    val isExpanded: androidx.compose.runtime.MutableState<Boolean>,
    val isOverdue: Boolean,
    val rotation: Float
)

@Composable
fun rememberTaskItemState(task: Task): TaskItemState {
    val isExpanded = rememberSaveable { mutableStateOf(false) }
    val currentTime = System.currentTimeMillis()
    val isOverdue = task.dueDate?.let { it < currentTime && !task.completed } ?: false
    val rotation by animateFloatAsState(targetValue = if (isExpanded.value) 180f else 0f)

    return TaskItemState(isExpanded, isOverdue, rotation)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberSwipeToDismissState(
    task: Task,
    onSwipe: () -> Unit
): SwipeToDismissBoxState {
    var swipeHandled by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    return rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (!swipeHandled) {
                Log.d("Task", "Swipe detected for task: ${task.taskId}")
                swipeHandled = true
                if (it == SwipeToDismissBoxValue.EndToStart) {
                    showArchiveConfirmationDialog(context, task, onSwipe)
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }
    )
}

fun showArchiveConfirmationDialog(
    context: Context,
    task: Task,
    onConfirm: () -> Unit
) {
    Log.d("Task", "Showing archive confirmation dialog")
    AlertDialog.Builder(context)
        .setTitle(if (task.archived) "Unarchive Task" else "Archive Task")
        .setMessage(if (task.archived) "Do you want to unarchive this task?" else "Do you want to archive this task?")
        .setPositiveButton("Yes") { _, _ -> onConfirm() }
        .setNegativeButton("No", null)
        .show()
}
