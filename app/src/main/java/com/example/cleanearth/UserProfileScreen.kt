// UserProfileScreen.kt
package com.example.cleanearth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cleanearth.User
import com.example.cleanearth.UserProfileHandler

@Composable
fun UserProfileScreen(email: String, navController: NavController) {
    val context = LocalContext.current
    val userProfileHandler = remember { UserProfileHandler(context) }

    val userState = remember { mutableStateOf<User?>(null) }

    LaunchedEffect(email) {
        val result = userProfileHandler.getUserProfile(email)
        userState.value = result
    }

    val user = userState.value
    val name = user?.nickname ?: "-"
    val emailShown = user?.email ?: "-"
    val dateOfBirth = user?.birthdate ?: "-"
    val gender = user?.gender ?: "-"



    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // 타이틀을 좀 더 아래로
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 0.dp, top = 64.dp, bottom = 24.dp)
            )

            // 프로필 카드
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                    ProfileItem(label = "Name", value = name)
                    Divider(color = Color.LightGray, thickness = 1.dp)

                    ProfileItem(label = "Email", value = email)
                    Divider(color = Color.LightGray, thickness = 1.dp)

                    ProfileItem(label = "Date of Birth", value = dateOfBirth)
                    Divider(color = Color.LightGray, thickness = 1.dp)

                    ProfileItem(label = "Gender", value = gender)
                }
            }

            // 로그아웃 버튼을 카드 바로 아래에 배치
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.navigate("start") {
                    popUpTo("userprofile/{email}") { inclusive = true }},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFa616e9) // 초록색
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "Log Out",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            // 남은 공간을 모두 아래로 밀어 빈 공간 확보
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun ProfileItem(
    label: String,
    value: String,
    trailingArrow: Boolean = false,
    onClick: () -> Unit = {}
) {
    val clickableModifier = if (trailingArrow) Modifier.clickable { onClick() } else Modifier

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(clickableModifier)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
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