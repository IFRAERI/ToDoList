package aodintsov.to_do_list.view.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import aodintsov.to_do_list.R
import aodintsov.to_do_list.model.SubTask

@Composable
fun CompletionCheckbox(
    isCompleted: Boolean,
    onCompletionChange: (Boolean) -> Unit,
    subTasks: List<SubTask>,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Checkbox(
            checked = isCompleted,
            onCheckedChange = {
                if (subTasks.all { it.completed } || subTasks.isEmpty()) {
                    onCompletionChange(it)
                }
            },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.onSurface
            )
        )
        Text(text = stringResource(R.string.completed), color = MaterialTheme.colorScheme.onBackground)
    }
}
