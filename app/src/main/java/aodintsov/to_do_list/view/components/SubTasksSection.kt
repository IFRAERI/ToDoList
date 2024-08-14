package aodintsov.to_do_list.view.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import aodintsov.to_do_list.R
import aodintsov.to_do_list.model.SubTask

@Composable
fun SubTasksSection(
    subTasks: List<SubTask>,
    onSubTasksChange: (List<SubTask>) -> Unit,
    modifier: Modifier = Modifier,
    onAddSubTask: () -> Unit
) {
    Column(modifier = modifier) {
        Text(text = stringResource(R.string.subtasks_label))

        subTasks.forEachIndexed { index, subTask ->
            Row(
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                TextField(
                    value = subTask.title,
                    onValueChange = {
                        val updatedSubTasks = subTasks.toMutableList().apply {
                            set(index, subTask.copy(title = it))
                        }
                        onSubTasksChange(updatedSubTasks)
                    },
                    label = { Text(stringResource(R.string.subtasks_label, index + 1)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.weight(1f)
                )
                Checkbox(
                    checked = subTask.completed,
                    onCheckedChange = {
                        val updatedSubTasks = subTasks.toMutableList().apply {
                            set(index, subTask.copy(completed = it))
                        }
                        onSubTasksChange(updatedSubTasks)
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
            val newSubTask = SubTask(
                subTaskId = System.currentTimeMillis().toString(),
                title = "",
                completed = false
            )
            onSubTasksChange(subTasks + newSubTask)
        }) {
            Text(text = stringResource(R.string.add_subtask))
        }
    }
}
