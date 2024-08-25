package aodintsov.to_do_list.view.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import aodintsov.to_do_list.R
import aodintsov.to_do_list.model.SubTask
@Composable
fun CompletionCheckbox(
    isCompleted: Boolean,
    onCompletionChange: (Boolean) -> Unit,
    subTasks: List<SubTask>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically, // Align children vertically to the center
        horizontalArrangement = Arrangement.Start // Align children horizontally at the start
    ) {
        Checkbox(
            checked = isCompleted,
            onCheckedChange = { checked ->
                if (subTasks.all { subTask -> subTask.completed } || subTasks.isEmpty()) {
                    onCompletionChange(checked)
                }
            },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.onSurface
            )
        )
        Text(
            text = stringResource(R.string.completed),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 8.dp) // Add some padding between the checkbox and the text
        )
    }
}
