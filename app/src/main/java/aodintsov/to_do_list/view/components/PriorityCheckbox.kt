package aodintsov.to_do_list.view.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import aodintsov.to_do_list.R

@Composable
fun PriorityCheckbox(
    isPriority: Boolean,
    onPriorityChange: (Boolean) -> Unit,
    isDeadlineSet: Boolean,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Checkbox(
            checked = isPriority,
            onCheckedChange = {
                if (!isDeadlineSet) onPriorityChange(it)
            },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.onSurface
            )
        )
        Text(
            text = stringResource(R.string.priority),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
