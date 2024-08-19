package aodintsov.to_do_list.utils

//import android.app.AlertDialog
//import android.content.Context
//import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
////noinspection UsingMaterialAndMaterial3Libraries
//import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.platform.LocalContext
import aodintsov.to_do_list.model.Task
//import aodintsov.to_do_list.R

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
    val rotation by animateFloatAsState(targetValue = if (isExpanded.value) 180f else 0f,
        label = ""
    )

    return TaskItemState(isExpanded, isOverdue, rotation)
}

//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun rememberSwipeToDismissState(
//    task: Task,
//    onSwipe: () -> Unit
//): DismissState {
//    var swipeHandled by rememberSaveable { mutableStateOf(false) }
//    val context = LocalContext.current
//    return rememberDismissState(
//        confirmStateChange = { dismissValue ->
//            if (!swipeHandled) {
//                Log.d("Task", "Swipe detected for task: ${task.taskId}")
//                swipeHandled = true
//                if (dismissValue == DismissValue.DismissedToEnd || dismissValue == DismissValue.DismissedToStart) {
//                    showArchiveConfirmationDialog(context, task, onSwipe)
//                    true
//                } else {
//                    false
//                }
//            } else {
//                false
//            }
//        }
//    )
//}

//fun showArchiveConfirmationDialog(
//    context: Context,
//    task: Task,
//    onConfirm: () -> Unit
//) {
//    AlertDialog.Builder(context)
//        .setTitle(if (task.archived) context.getString(R.string.unarchive_task) else context.getString(R.string.archive_task))
//        .setMessage(if (task.archived) context.getString(R.string.unarchive_task_confirm) else context.getString(R.string.archive_task_confirm))
//        .setPositiveButton(context.getString(R.string.yes)) { _, _ -> onConfirm() }
//        .setNegativeButton(context.getString(R.string.no), null)
//        .show()
//}
