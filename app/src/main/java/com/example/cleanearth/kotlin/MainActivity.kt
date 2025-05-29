package com.example.cleanearth.kotlin

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cleanearth.CameraPreviewScreen
import com.example.cleanearth.ReformNavHost
import com.example.cleanearth.ui.theme.CleanEarthTheme

// so tireeeeeeed
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
                    composable("scan") { CameraPreviewScreen(navController = navController)}
                    // reform/{reformLabel} 경로로 ReformNavHost를 생성
                    composable(
                        route = "reform/{reformLabel}",
                        arguments = listOf(
                            androidx.navigation.navArgument("reformLabel") { type = androidx.navigation.NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val reformLabel = backStackEntry.arguments?.getString("reformLabel") ?: ""
                        ReformNavHost(reformLable = reformLabel, navController = navController)
                    }
                    // MainActivity 또는 메인 네비게이션에서
//                        composable(
//                            route = "reform_nav_host/{materialType}",
//                            arguments = listOf(navArgument("materialType") { type = NavType.StringType })
//                        ) { backStackEntry ->
//                            val materialType = backStackEntry.arguments?.getString("materialType") ?: "기타"
//                            ReformNavHost(
//                                reformLable = materialType,
//                                navController = navController
//                            )
//                        }
//
//                        // 상세 화면 추가 (ReformNavHost에서 호출하는 detail 화면)
//                        composable(
//                            route = "detail/{ideaId}",
//                            arguments = listOf(navArgument("ideaId") { type = NavType.StringType })
//                        ) { backStackEntry ->
//                            val ideaId = backStackEntry.arguments?.getString("ideaId") ?: return@composable
//
//                            val idea = ideasByCategory.values
//                                .flatten()
//                                .find { it.id == ideaId } ?: return@composable
//
//                            IdeaDetailScreen(
//                                idea = idea,
//                                onBack = { navController.navigate(IdeaDetailScreen(ideaId)) }
//                            )
//                        }
                    }
                }
            }
        }
    }
