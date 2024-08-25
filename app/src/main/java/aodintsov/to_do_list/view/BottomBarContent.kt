// BottomBar.kt
package aodintsov.to_do_list.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import aodintsov.to_do_list.R

@Composable
fun BottomBarContent(
    completedTaskCount: Int,
    totalTaskCount: Int,
    modifier: Modifier = Modifier
) {

        Text(
            text = stringResource(id = R.string.completed_tasks, completedTaskCount, totalTaskCount),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Start
        )
    }
