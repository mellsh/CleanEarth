package com.example.cleanearth.kotlin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.cleanearth.R

@Composable
fun StartScreen(navController: NavHostController) {
    val lightGreen = Color(0xFFd4edc9)
    val darkGreen = Color(0xFF4CAF50)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 배경 이미지
//        Image(
////            painter = painterResource(id = R.drawable.startscreenbackgroudimage),
//            contentDescription = "Background",
//            contentScale = ContentScale.Crop,
//            modifier = Modifier.fillMaxSize()
//        )

        // 반투명 녹색 오버레이 + 콘텐츠
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(lightGreen.copy(alpha = 0.7f))
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // 가운데 정렬
        ) {
            // 인사말
            Text(
                text = "Welcome CleanEarth",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = darkGreen
            )

            // 부제목
            Text(
                text = "AI로 더 똑똑한 분리수거와\n리폼 가이드를 경험해보세요",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = darkGreen,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            // 대표 이미지 (중앙쯤에 위치)
            Image(
                painter = painterResource(id = R.drawable.earth),
                contentDescription = "Main Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(bottom = 32.dp),
                contentScale = ContentScale.Fit
            )

            // 버튼들
            Button(
                onClick = {navController.navigate("login")},
                colors = ButtonDefaults.buttonColors(containerColor = darkGreen),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    "로그인",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            OutlinedButton(
                onClick = { navController.navigate("signup") },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = darkGreen),
                shape = RoundedCornerShape(30.dp),
                border = BorderStroke(2.dp, darkGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(vertical = 8.dp)
            ) {
                Text("회원가입",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    modifier = Modifier.padding(vertical = 2.dp)
                    )
            }
        }
    }
}
