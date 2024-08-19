package aodintsov.to_do_list.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import aodintsov.to_do_list.R

@Composable
fun PriorityCheckbox(
    isPriority: Boolean,
    onPriorityChange: (Boolean) -> Unit,
    isDeadlineSet: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(), // Optionally fill the width of the parent
        verticalAlignment = Alignment.CenterVertically, // Center align the checkbox and text vertically
        horizontalArrangement = Arrangement.Start // Align children horizontally at the start
    ) {
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
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 8.dp) // Add padding to create space between checkbox and text
        )
    }
}
