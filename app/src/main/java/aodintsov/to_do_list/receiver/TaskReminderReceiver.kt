package aodintsov.to_do_list.receiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import aodintsov.to_do_list.model.FirestoreService
import aodintsov.to_do_list.model.TaskRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import aodintsov.to_do_list.R

class TaskReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val repository = TaskRepositoryImpl(FirestoreService())
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        CoroutineScope(Dispatchers.IO).launch {
            repository.getTasks(userId, { tasks ->
                val incompleteTasks = tasks.filter { !it.completed }
                if (incompleteTasks.isNotEmpty()) {
                    showNotification(context, incompleteTasks.size)
                }
            }, {
                // Обработка ошибки
            })
        }
    }

    private fun showNotification(context: Context, taskCount: Int) {
        val title = context.getString(R.string.unresolved_tasks_title)
        val text = context.getString(R.string.unresolved_tasks_text, taskCount)
        val notification = NotificationCompat.Builder(context, "tasks_channel")

            .setSmallIcon(R.drawable.img) // Замените на ваш значок
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Проверка разрешения на отправку уведомлений
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Если разрешение не предоставлено, просто вернемся
            return
        }

        NotificationManagerCompat.from(context).notify(1, notification)
    }
}
