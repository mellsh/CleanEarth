package com.example.cleanearth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cleanearth.ui.UserProfileScreen
import com.example.cleanearth.ui.theme.CleanEarthTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CleanEarthTheme {
                val navController = rememberNavController()

                Surface(color = MaterialTheme.colorScheme.background) {
                    NavHost(navController = navController, startDestination = "start") {
                        composable("start") { StartScreen(navController) }
                        composable("login") {
                            LoginScreen(navController = navController)
                        }

                        composable("userprofile/{email}") { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            UserProfileScreen(email = email)
                        }
                        composable("signup") {SignUpScreen(navController = navController)}
                        composable("profile/{email}") { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            ProfileScreen(navController = navController, email = email)
                        }
                    }
                }
            }
        }
    }
}
