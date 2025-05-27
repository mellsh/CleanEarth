package com.example.cleanearth.kotlin

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.cleanearth.java.LoginHandler

@Composable
fun LoginScreen(
    navController: NavController,
    onLoginClicked: () -> Unit = {} // 로그인 완료 후 호출
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val loginHandler = remember {
        LoginHandler(
            context
        )
    }
    val darkGreen = Color(0xFF4CAF50)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "로그인",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "이메일과 비밀번호를 입력해주세요",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 이메일 입력
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("이메일") },
            singleLine = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "이메일 아이콘",
                    tint = darkGreen
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = darkGreen,
                cursorColor = darkGreen,
                focusedLabelColor = darkGreen
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // 비밀번호 입력
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "비밀번호 아이콘",
                    tint = darkGreen
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = darkGreen,
                cursorColor = darkGreen,
                focusedLabelColor = darkGreen
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {when {
                email.isBlank() || password.isBlank() -> {
                    errorMessage = "모든 항목을 입력해주세요."
                }
                loginHandler.login(email, password) -> {
                    errorMessage = ""
                    val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)
                    sharedPref.edit().putString("email", email).apply()
                    navController.navigate("main") // ✅ 이동!
                }
                else -> {
                    errorMessage = "이메일 또는 비밀번호가 잘못되었습니다."
                }
            }},
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = darkGreen),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .border(2.dp, darkGreen, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
        ) {
            Text("로그인", style = MaterialTheme.typography.titleMedium)
        }
    }
}


