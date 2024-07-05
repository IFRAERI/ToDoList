package aodintsov.to_do_list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.compose.rememberNavController
import aodintsov.to_do_list.model.TaskRepositoryImpl
import aodintsov.to_do_list.navigation.AppNavigation
import aodintsov.to_do_list.ui.theme.ToDoListTheme
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

        setContent {
            ToDoListTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val taskViewModel: TaskViewModel by viewModels {
                        taskViewModelFactory
                    }
                    val authViewModelFactory = AuthViewModelFactory(firebaseAuth)

                    AppNavigation(
                        navController = navController,
                        taskViewModelFactory = taskViewModelFactory,
                        authViewModelFactory = authViewModelFactory,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun createSavedStateHandle(): SavedStateHandle {
        val savedStateHandle = SavedStateHandle()
        // Инициализируем SavedStateHandle, если это необходимо
        return savedStateHandle
    }
}
