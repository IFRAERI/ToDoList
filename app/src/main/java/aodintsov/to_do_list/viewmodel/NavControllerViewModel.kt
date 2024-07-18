package aodintsov.to_do_list.viewmodel

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class NavControllerViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    lateinit var navController: NavHostController

    fun initializeNavController(controller: NavHostController) {
        navController = controller
        val savedState = savedStateHandle.get<Bundle>("navState")
        savedState?.let {
            navController.restoreState(it)
        }
    }

    fun saveState() {
        savedStateHandle["navState"] = navController.saveState()
    }
}
