package aodintsov.to_do_list.view.components

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import aodintsov.to_do_list.R
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DeadlinePicker(
    deadline: Long?,
    onDatePick: (Calendar) -> Unit,
    onClearDeadline: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    // Установка календаря на текущее значение дедлайна
    deadline?.let { calendar.timeInMillis = it }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            onDatePick(calendar)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Row(modifier = modifier) {
        MyDeadlineText(deadline)
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = {
            if (deadline != null) {
                onClearDeadline() // Удаление дедлайна
            } else {
                datePickerDialog.show()
            }
        }) {
            Text(text = if (deadline != null) stringResource(R.string.remove_deadline) else stringResource(R.string.set_deadline))
        }
    }
}

@Composable
fun MyDeadlineText(deadline: Long?) {
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    val deadlineText = deadline?.let {
        dateFormatter.format(it)
    } ?: stringResource(id = R.string.no_deadline)

    Text(
        text = deadlineText,
        color = MaterialTheme.colorScheme.onBackground
    )
}
