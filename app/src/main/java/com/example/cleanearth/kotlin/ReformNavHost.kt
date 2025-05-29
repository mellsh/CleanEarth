// nav/ReformNavHost.kt
package com.example.cleanearth

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.cleanearth.data.*
import com.example.cleanearth.kotlin.MainScreen

@Composable
fun ReformNavHost(reformLable: String, navController: NavHostController) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "ideas/$reformLable"   // 받은 파라미터로 시작 화면 설정
    ) {
        /* ───── 카테고리별 추천 리스트 화면 ───── */
        composable("main") { MainScreen(navController) }
        composable("camera") { CameraPreviewScreen(navController = navController) }
        composable(
            route = "ideas/{category}",
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStack ->
            val category = backStack.arguments?.getString("category") ?: reformLable.toString()

            ReformIdeasScreen(
                category = category,
                onIdeaClick = { idea ->                      // 카드 클릭 → 상세
                    navController.navigate("detail/${idea.id}")
                },
                onHomeClick = { navController.popBackStack() } // 필요 없으면 제거
            )
        }

        /* ───── 상세 설명 화면 ───── */
        composable(
            route = "detail/{ideaId}",
            arguments = listOf(navArgument("ideaId") { type = NavType.StringType })
        ) { backStack ->
            val ideaId = backStack.arguments?.getString("ideaId") ?: return@composable

            // 모든 카테고리 풀(flatten)에서 id 매칭
            val idea = ideasByCategory.values
                .flatten()
                .find { it.id == ideaId } ?: return@composable

            IdeaDetailScreen(
                idea = idea,
                onBack = { navController.popBackStack() },
                navController = navController
            )
        }
    }
}