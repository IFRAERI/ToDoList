package aodintsov.to_do_list

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import aodintsov.to_do_list.model.FirestoreService
import aodintsov.to_do_list.model.TaskRepositoryImpl
import aodintsov.to_do_list.navigation.AppNavigation
import aodintsov.to_do_list.ui.theme.ToDoListTheme
import aodintsov.to_do_list.viewmodel.AuthViewModel
import aodintsov.to_do_list.viewmodel.AuthViewModelFactory
import aodintsov.to_do_list.viewmodel.NavControllerViewModel
import aodintsov.to_do_list.viewmodel.NavControllerViewModelFactory
import aodintsov.to_do_list.viewmodel.TaskViewModel
import aodintsov.to_do_list.viewmodel.TaskViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firestore = FirestoreService()
        val firebaseAuth = FirebaseAuth.getInstance()
        val repository = TaskRepositoryImpl(firestore)
        val taskViewModelFactory = TaskViewModelFactory(repository, createSavedStateHandle())
        val authViewModelFactory = AuthViewModelFactory(firebaseAuth)

        setContent {
            ToDoListTheme {
                val navControllerViewModel = rememberNavControllerViewModel()
                val navController = navControllerViewModel.navController

                var showLogoutDialog by remember { mutableStateOf(false) }

                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = currentBackStackEntry?.destination?.route

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
    }

    @SuppressLint("RememberReturnType")
    @Composable
    fun rememberNavControllerViewModel(): NavControllerViewModel {
        val context = LocalContext.current
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
        val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
        val taskViewModel: TaskViewModel by viewModels { taskViewModelFactory }

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
}