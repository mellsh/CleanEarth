// UserProfileScreen.kt
package com.example.cleanearth.kotlin

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cleanearth.java.User
import com.example.cleanearth.java.UserProfileHandler

@Composable
fun UserProfileScreen(
    email: String,
    navController: NavController,
    currentRoute: String = "profile",           // 하단 탭 선택 상태
    onTabSelect: (String) -> Unit = {},         // 탭 변경 콜백
    onLogoutClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val userProfileHandler = remember {
        UserProfileHandler(
            context
        )
    }

    val userState = remember { mutableStateOf<User?>(null) }

    LaunchedEffect(email) {
        try {
            val result = userProfileHandler.getUserProfile(email)
            userState.value = result
        } catch (e: Exception) {
            Log.e("UserProfileScreen", "유저 정보 로딩 실패: ${e.message}")
        }
    }

    val user = userState.value
    val name = user?.nickname ?: "-"
    val emailShown = user?.email ?: "-"
    val dateOfBirth = user?.birthdate ?: "-"
    val gender = user?.gender ?: "-"



    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavBar(currentRoute, onTabSelect, navController) }   // ③ 하단 탭바
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            /* ─── 타이틀 ─── */
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 64.dp, bottom = 24.dp)
            )

            /* ─── 프로필 카드 ─── */
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(vertical = 16.dp)) {
                    ProfileItem(label = "Name",  value = name)
                    Divider(thickness = 1.dp, color = Color.LightGray)

                    ProfileItem(label = "Email", value = emailShown)
                    Divider(thickness = 1.dp, color = Color.LightGray)

                    ProfileItem(label = "Date of Birth", value = dateOfBirth)
                    Divider(thickness = 1.dp, color = Color.LightGray)

                    ProfileItem(label = "Gender", value = gender)
                    Divider(thickness = 1.dp, color = Color.LightGray)

                    /* ─── ② 반투명 회색 로그아웃 줄 ─── */
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)
                                sharedPref.edit().clear().apply()
                                navController.navigate("start") {
                                popUpTo("userprofile/{email}")
                                }}
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Log Out",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Log Out",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))
        }
    }
}

/* ────────────────────────────────────────── */
/* 하단 탭바: 홈 화면과 동일하게 재사용      */
@Composable
fun BottomNavBar(
    currentRoute: String,
    onSelect: (String) -> Unit,
    navController: NavController
    ) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = {navController.navigate("main") {
                popUpTo("userprofile/{email}") { inclusive = false }}},
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentRoute == "camera",
            onClick = { onSelect("camera")
                navController.navigate("scan") },
            icon = { Icon(Icons.Default.CameraAlt, contentDescription = "Scan") },
            label = { Text("Camera") }
        )
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = { onSelect("profile") },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") }
        )
    }
}

/* ────────────────────────────────────────── */
@Composable
fun ProfileItem(
    label: String,
    value: String,
    trailingArrow: Boolean = false,
    onClick: () -> Unit = {}
) {
    val clickable = if (trailingArrow) Modifier.clickable { onClick() } else Modifier
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(clickable)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (trailingArrow) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}