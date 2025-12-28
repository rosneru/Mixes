package de.tysw.mixes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.tysw.mixes.ui.theme.MixExplorerTheme
import de.tysw.mixes.viewmodels.MainViewModel
import de.tysw.mixes.views.MainScreen
import de.tysw.mixes.views.SettingsScreen

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object Settings : Screen("settings")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MixExplorerTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            val viewModel: MainViewModel = viewModel()
            MainScreen(
                viewModel,
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
