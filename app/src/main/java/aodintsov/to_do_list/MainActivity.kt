package aodintsov.to_do_list

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
import aodintsov.to_do_list.model.FirestoreService
import aodintsov.to_do_list.model.TaskRepositoryImpl
import aodintsov.to_do_list.navigation.AppNavigation
import aodintsov.to_do_list.ui.theme.ToDoListTheme
import aodintsov.to_do_list.utils.AlarmUtils
import aodintsov.to_do_list.viewmodel.AuthViewModel
import aodintsov.to_do_list.viewmodel.AuthViewModelFactory
import aodintsov.to_do_list.viewmodel.NavControllerViewModel
import aodintsov.to_do_list.viewmodel.NavControllerViewModelFactory
import aodintsov.to_do_list.viewmodel.TaskViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            AlarmUtils.setDailyReminder(this)
        } else {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}

        val firestore = FirestoreService()
        val firebaseAuth = FirebaseAuth.getInstance()
        val repository = TaskRepositoryImpl(firestore)
        val authViewModel = AuthViewModel(firebaseAuth)
        val authViewModelFactory = AuthViewModelFactory(firebaseAuth)
        val taskViewModelFactory = TaskViewModelFactory(repository, authViewModel, createSavedStateHandle())

        setContent {
            ToDoListTheme {
                val navControllerViewModel = rememberNavControllerViewModel()
                val navController = navControllerViewModel.navController

                var showLogoutDialog by remember { mutableStateOf(false) }

                Content(
                    navController = navController,
                    authViewModelFactory = authViewModelFactory,
                    taskViewModelFactory = taskViewModelFactory,
                    firebaseAuth = firebaseAuth,
                    showLogoutDialog = showLogoutDialog,
                    onDismissLogoutDialog = { showLogoutDialog = false }
                )

                DisposableEffect(Unit) {
                    onDispose {
                        navControllerViewModel.saveState()
                    }
                }
            }
        }

        createNotificationChannel()
        checkNotificationPermissionAndSetAlarm()
    }

    @SuppressLint("RememberReturnType")
    @Composable
    fun rememberNavControllerViewModel(): NavControllerViewModel {
        val factory = NavControllerViewModelFactory(SavedStateHandle())
        val viewModel: NavControllerViewModel = viewModel(factory = factory)
        val navController = rememberNavController()
        remember(navController) {
            viewModel.initializeNavController(navController)
        }
        return viewModel
    }

    @Composable
    private fun Content(
        navController: NavHostController,
        authViewModelFactory: AuthViewModelFactory,
        taskViewModelFactory: TaskViewModelFactory,
        firebaseAuth: FirebaseAuth,
        modifier: Modifier = Modifier,
        showLogoutDialog: Boolean,
        onDismissLogoutDialog: () -> Unit
    ) {
        AppNavigation(
            navController = navController,
            taskViewModelFactory = taskViewModelFactory,
            authViewModelFactory = authViewModelFactory,
            firebaseAuth = firebaseAuth,
            modifier = modifier
        )

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = onDismissLogoutDialog,
                title = { Text("Confirm Logout") },
                text = { Text("Are you sure you want to logout?") },
                confirmButton = {
                    Button(onClick = {
                        firebaseAuth.signOut()
                        navController.navigate("login") {
                            popUpTo("taskList") { inclusive = true }
                        }
                        onDismissLogoutDialog()
                    }) {
                        Text("Logout")
                    }
                },
                dismissButton = {
                    Button(onClick = onDismissLogoutDialog) {
                        Text("Cancel")
                    }
                }
            )
        }
    }

    private fun createSavedStateHandle(): SavedStateHandle {
        return SavedStateHandle()
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

                }
                else -> {

                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {

            AlarmUtils.setDailyReminder(this)
        }
    }
}
