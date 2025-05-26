package com.example.cleanearth

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cleanearth.ui.MainScreen
import com.example.cleanearth.ui.UserProfileScreen
import com.example.cleanearth.ui.theme.CleanEarthTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE)
        val email = sharedPref.getString("email", null)

        setContent {
            val navController = rememberNavController()
            CleanEarthTheme {
                NavHost(
                    navController = navController,
                    startDestination = if (email != null) "main" else "start" // ✅ 핵심
                ) {
                    composable("start") { StartScreen(navController) }
                    composable("login") { LoginScreen(navController) }
                    composable("signup") { SignUpScreen(navController) }
                    composable("main") { MainScreen(navController) }
                    composable("signupprofile/{email}") { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email") ?: ""
                        ProfileScreen(
                            navController = navController,
                            onNextClicked = {
                                // Profile 작성 완료 후 로그인 화면으로 이동
                                navController.navigate("login") {
                                    popUpTo("signupprofile/$email") { inclusive = true }
                                }
                            },
                            onBackClicked = {
                                navController.popBackStack()
                            },
                            email = email // ← ProfileScreen에 email이 필요하다면
                        )
                    }
                    composable("profile/{email}") {
                        val emailArg = it.arguments?.getString("email") ?: ""
                        UserProfileScreen(email = emailArg, navController = navController)
                    }
                    // 추후 카메라
                    composable("scan") { /* ScanScreen(navController) */ }
                }
            }
        }
    }
}
