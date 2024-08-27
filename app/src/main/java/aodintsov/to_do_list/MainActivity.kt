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
import aodintsov.to_do_list.model.UserRepositoryImpl
import aodintsov.to_do_list.navigation.AppNavigation
import aodintsov.to_do_list.ui.theme.ToDoListTheme
import aodintsov.to_do_list.utils.AlarmUtils
import aodintsov.to_do_list.viewmodel.*
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.*

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

        val firestore = FirestoreService()
        val firebaseAuth = FirebaseAuth.getInstance()
        val taskRepository = TaskRepositoryImpl(firestore)
        val userRepository = UserRepositoryImpl(firestore)
        val authViewModelFactory = AuthViewModelFactory(firebaseAuth, userRepository)

        val userViewModelFactory = UserViewModelFactory(userRepository)
        val taskViewModelFactory = TaskViewModelFactory(taskRepository, AuthViewModel(firebaseAuth,userRepository), createSavedStateHandle())

        setContent {
            ToDoListTheme {
                val navControllerViewModel = rememberNavControllerViewModel()
                val navController = navControllerViewModel.navController

                val snackbarHostState = remember { SnackbarHostState() }

                Content(
                    navController = navController,
                    authViewModelFactory = authViewModelFactory,
                    taskViewModelFactory = taskViewModelFactory,
                    userViewModelFactory = userViewModelFactory,
                    firebaseAuth = firebaseAuth,
                    snackbarHostState = snackbarHostState
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
        startDeferredTaskChecker(taskViewModelFactory)
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
        snackbarHostState: SnackbarHostState,
        userViewModelFactory: UserViewModelFactory, // Обязательно включаем этот параметр
        modifier: Modifier = Modifier
    ) {
        val userViewModel: UserViewModel = viewModel(factory = userViewModelFactory)
        AppNavigation(
            navController = navController,
            taskViewModelFactory = taskViewModelFactory,
            authViewModelFactory = authViewModelFactory,
            firebaseAuth = firebaseAuth,
            modifier = modifier,
            userViewModel = userViewModel,
            userViewModelFactory = userViewModelFactory // Передаем userViewModelFactory в AppNavigation
        )

        SnackbarHost(hostState = snackbarHostState)
    }


    private fun startDeferredTaskChecker(taskViewModelFactory: TaskViewModelFactory) {
        mainActivityScope.launch {
            delay(15000)

            val taskViewModel = TaskViewModel(
                repository = taskViewModelFactory.getRepository(),
                authViewModel = taskViewModelFactory.getAuthViewModel(),
                savedStateHandle = createSavedStateHandle()
            )

            while (isActive) {
                taskViewModel.checkAndActivateDeferredTasks()
                delay(3600000)
            }
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
