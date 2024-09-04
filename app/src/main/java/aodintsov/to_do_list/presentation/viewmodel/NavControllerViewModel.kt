package aodintsov.to_do_list.presentation.viewmodel

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController

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
