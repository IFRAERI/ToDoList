package aodintsov.to_do_list.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import aodintsov.to_do_list.R



@Composable
fun TitleInput(
    title: String,
    onTitleChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val maxLength = 70
    val titleLength = title.length

    Column(modifier = modifier) {
        TextField(
            value = title,
            onValueChange = { if (it.length <= maxLength) onTitleChange(it) },
            label = { Text(stringResource(R.string.title_label)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        )
        Text(
            text = "$titleLength/$maxLength",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 16.dp)
        )
    }
}
