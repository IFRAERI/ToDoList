package aodintsov.to_do_list.view.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import aodintsov.to_do_list.R
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MyDeadlineText(
    deadline: Long?,
    modifier: Modifier = Modifier
) {
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    val deadlineText = deadline?.let {
        dateFormatter.format(it)
    } ?: stringResource(id = R.string.no_deadline)

    Text(
        text = stringResource(id = R.string.deadline, deadlineText),
        modifier = modifier
    )
}
