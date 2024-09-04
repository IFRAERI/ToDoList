package aodintsov.to_do_list.view

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import aodintsov.to_do_list.R
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.draw.clip
import aodintsov.to_do_list.data.model.TaskFilter
@Composable
fun TaskListTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filterState: TaskFilter,
    onFilterChange: () -> Unit,
    isAscending: Boolean,
    onSortOrderChange: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Row {
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text(stringResource(id = R.string.search_label)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .weight(0.65f)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.weight(0.05f))

        IconButton(onClick = onFilterChange) {
            val icon = when (filterState) {
                TaskFilter.ALL -> Icons.Default.Menu // Значок по умолчанию (три черточки)
                TaskFilter.ARCHIVED -> Icons.Default.Archive
                TaskFilter.DEFERRED -> Icons.Default.Schedule
                TaskFilter.COMPLETED -> Icons.Default.Check
                TaskFilter.UNCOMPLETED -> Icons.Default.Clear
                // Значок по умолчанию
            }
            Icon(
                imageVector = icon,
                contentDescription = stringResource(id = R.string.filter_tasks),
                tint = Color.Gray
            )
        }

        IconButton(onClick = onSortOrderChange) {
            Icon(
                imageVector = if (isAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = stringResource(id = R.string.sort),
                tint = Color.Gray
            )
        }

        IconButton(onClick = onLogoutClick) {
            Icon(
                Icons.AutoMirrored.Filled.Logout,
                contentDescription = stringResource(id = R.string.logout),
                tint = Color.Gray
            )
        }
    }
}
