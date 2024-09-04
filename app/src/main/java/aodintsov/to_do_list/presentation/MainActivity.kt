package aodintsov.to_do_list.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import aodintsov.to_do_list.R
import aodintsov.to_do_list.data.model.FirestoreService
import aodintsov.to_do_list.data.repositoryimpl.TaskRepositoryImpl
import aodintsov.to_do_list.data.repositoryimpl.UserRepositoryImpl
import aodintsov.to_do_list.navigation.AppNavigation
import aodintsov.to_do_list.presentation.viewmodel.AuthViewModel
import aodintsov.to_do_list.presentation.viewmodel.AuthViewModelFactory
import aodintsov.to_do_list.presentation.viewmodel.NavControllerViewModel
import aodintsov.to_do_list.presentation.viewmodel.NavControllerViewModelFactory
import aodintsov.to_do_list.presentation.viewmodel.TaskViewModel
import aodintsov.to_do_list.presentation.viewmodel.UserViewModel
import aodintsov.to_do_list.ui.theme.ToDoListTheme
import aodintsov.to_do_list.utils.AlarmUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.*
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import jakarta.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainActivityScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            AlarmUtils.setDailyReminder(this)
        } else {
            showSnackbar(R.string.notification_permission_denied)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}

        setContent {
            ToDoListTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }

                Content(
                    navController = navController,
                    snackbarHostState = snackbarHostState
                )
            }
        }

        createNotificationChannel()
        checkNotificationPermissionAndSetAlarm()
    }

    @Composable
    private fun Content(
        navController: NavHostController,
        snackbarHostState: SnackbarHostState,
        modifier: Modifier = Modifier
    ) {
        val authViewModel: AuthViewModel = hiltViewModel()
        val taskViewModel: TaskViewModel = hiltViewModel()
        val userViewModel: UserViewModel = hiltViewModel()

        AppNavigation(
            navController = navController,
            modifier = modifier,
            userViewModel = userViewModel
        )

        SnackbarHost(hostState = snackbarHostState)
    }

    private fun createNotificationChannel() {
        val name = "Tasks Channel"
        val descriptionText = "Channel for task reminders"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("tasks_channel", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun checkNotificationPermissionAndSetAlarm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    AlarmUtils.setDailyReminder(this)
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    showSnackbar(R.string.notification_permission_rationale)
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            AlarmUtils.setDailyReminder(this)
        }
    }

    private fun showSnackbar(messageResId: Int) {
        mainActivityScope.launch {
            val snackbarHostState = SnackbarHostState()
            snackbarHostState.showSnackbar(getString(messageResId))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivityScope.cancel()
    }
}
