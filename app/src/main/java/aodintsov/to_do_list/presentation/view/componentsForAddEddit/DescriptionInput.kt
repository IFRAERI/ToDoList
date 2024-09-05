package aodintsov.to_do_list.view.components

import androidx.compose.foundation.layout.Column
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
fun DescriptionInput(
    description: String,
    onDescriptionChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val maxLength = 250
    val descriptionLength = description.length

    Column(modifier = modifier) {
        TextField(
            value = description,
            onValueChange = { if (it.length <= maxLength) onDescriptionChange(it) },
            label = { Text(stringResource(R.string.description_label)) },
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
            text = "$descriptionLength/$maxLength",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 16.dp)
        )
    }
}
