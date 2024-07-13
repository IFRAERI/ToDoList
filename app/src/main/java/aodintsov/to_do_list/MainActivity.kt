package aodintsov.to_do_list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import aodintsov.to_do_list.model.TaskRepositoryImpl
import aodintsov.to_do_list.navigation.AppNavigation
import aodintsov.to_do_list.ui.theme.ToDoListTheme
import aodintsov.to_do_list.view.LoginScreen
import aodintsov.to_do_list.viewmodel.AuthViewModel
import aodintsov.to_do_list.viewmodel.AuthViewModelFactory
import aodintsov.to_do_list.viewmodel.TaskViewModel
import aodintsov.to_do_list.viewmodel.TaskViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firestore = FirebaseFirestore.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()
        val repository = TaskRepositoryImpl(firestore)
        val taskViewModelFactory = TaskViewModelFactory(repository, createSavedStateHandle())
        val authViewModelFactory = AuthViewModelFactory(firebaseAuth)

        setContent {
            ToDoListTheme {
                val navController = rememberNavController()
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
            }
        }
    }

    @Composable
    private fun Content(
        navController: NavController,
        authViewModelFactory: AuthViewModelFactory,
        taskViewModelFactory: TaskViewModelFactory,
        firebaseAuth: FirebaseAuth,
        modifier: Modifier = Modifier,
        showLogoutDialog: Boolean,
        onDismissLogoutDialog: () -> Unit
    ) {
        val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
        val taskViewModel: TaskViewModel by viewModels {
            taskViewModelFactory
        }

        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            taskViewModel.fetchTasks(currentUser.uid)
            AppNavigation(
                navController = navController,
                taskViewModelFactory = taskViewModelFactory,
                authViewModelFactory = authViewModelFactory,
                modifier = modifier
            )
        } else {
            LoginScreen(navController, authViewModelFactory = authViewModelFactory)
        }

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
        val savedStateHandle = SavedStateHandle()
        return savedStateHandle
    }
}
