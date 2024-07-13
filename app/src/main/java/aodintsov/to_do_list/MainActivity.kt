package aodintsov.to_do_list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Sort
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

@OptIn(ExperimentalMaterial3Api::class)
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

                val showScaffold = currentDestination == "taskList"

                if (showScaffold) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopAppBar(
                                title = { Text("Task List") },
                                actions = {
                                    var searchQuery by remember { mutableStateOf("") }
                                    val taskViewModel: TaskViewModel = viewModel(factory = taskViewModelFactory)

                                    TextField(
                                        value = searchQuery,
                                        onValueChange = {
                                            searchQuery = it
                                            taskViewModel.searchTasks(it)
                                        },
                                        label = { Text("Search") },
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    IconButton(onClick = {
                                        taskViewModel.sortTasksByDate()
                                    }) {
                                        Icon(Icons.Default.Sort, contentDescription = "Sort")
                                    }
                                    IconButton(onClick = {
                                        showLogoutDialog = true
                                    }) {
                                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                                    }
                                }
                            )
                        }
                    ) { innerPadding ->
                        Content(
                            navController = navController,
                            authViewModelFactory = authViewModelFactory,
                            taskViewModelFactory = taskViewModelFactory,
                            firebaseAuth = firebaseAuth,
                            modifier = Modifier.padding(innerPadding),
                            showLogoutDialog = showLogoutDialog,
                            onDismissLogoutDialog = { showLogoutDialog = false }
                        )
                    }
                } else {
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