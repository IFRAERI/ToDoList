package aodintsov.to_do_list.view.components

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import aodintsov.to_do_list.R
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun DeferredDateTimePicker(
    deferredUntil: Long?,
    onDeferredDateTimeChange: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val currentDate = Calendar.getInstance()

    deferredUntil?.let { calendar.timeInMillis = it }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            onDeferredDateTimeChange(calendar.timeInMillis)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )


    datePickerDialog.datePicker.minDate = currentDate.timeInMillis

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = { datePickerDialog.show() }) {
            Text(text = stringResource(id = R.string.select_Date))
        }
        Spacer(modifier = Modifier.width(16.dp))  // Add space between the Button and Text
        deferredUntil?.let {
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            Text(text = dateFormatter.format(Date(it)),
                color = MaterialTheme.colorScheme.onBackground )

        }
    }

}
