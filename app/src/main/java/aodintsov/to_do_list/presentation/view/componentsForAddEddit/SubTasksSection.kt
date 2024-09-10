package aodintsov.to_do_list.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import aodintsov.to_do_list.R
import aodintsov.to_do_list.data.model.SubTask
@Composable
fun SubTasksSection(
    subTasks: List<SubTask>,
    onSubTasksChange: (List<SubTask>) -> Unit,
    modifier: Modifier = Modifier,
    onAddSubTask: () -> Unit,
    onGenerateSubTasks: () -> Unit,
    isLoading: Boolean // Передаем состояние загрузки из ViewModel
) {
    val maxLength = 100

    Column(modifier = modifier) {
        Text(text = stringResource(R.string.subtasks_label),
            color = MaterialTheme.colorScheme.onBackground)

        subTasks.forEachIndexed { index, subTask ->
            Row(
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    TextField(
                        value = subTask.title,
                        onValueChange = {
                            if (it.length <= maxLength) {
                                val updatedSubTasks = subTasks.toMutableList().apply {
                                    set(index, subTask.copy(title = it))
                                }
                                onSubTasksChange(updatedSubTasks)
                            }
                        },
                        label = { Text(stringResource(R.string.subtasks_label, index + 1)) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "${subTask.title.length} / $maxLength",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.End),
                        color = if (subTask.title.length == maxLength) Color.Red else Color.Gray
                    )
                }

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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Кнопка добавления подзадачи
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

            // Кнопка генерации подзадач
            Button(
                onClick = {
                    onGenerateSubTasks()
                },
                enabled = !isLoading // Деактивируем кнопку, если идёт загрузка
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(text = stringResource(R.string.generate_subtasks))
                }

            }
        }
    }
}
