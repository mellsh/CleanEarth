package com.example.cleanearth;

import com.example.cleanearth.java.SimpleImageAnalyzer

import org.json.JSONObject
import android.hardware.Camera

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Looper
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.cleanearth.java.CameraPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.util.*
import android.os.Handler


@Composable
fun CameraPreviewScreen(
    onNavigateToSignUp: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onNavigateToCamera: () -> Unit = {},
    currentRoute: String = "scan",
    onTabSelect: (String) -> Unit = {},
    navController: NavController
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val darkGreen = Color(0xFF4CAF50)
    val currentRoute = "camera"
    val categoryResults = ""

    var showLoading by remember { mutableStateOf(false) }
    var showBanner  by remember { mutableStateOf(false) }
    var labelResult by remember { mutableStateOf<String?>(null) }
    var confidenceResult by remember { mutableStateOf<Float?>(null) }
    val pictureBitmap = remember { mutableStateOf<Bitmap?>(null) }
    val cameraView = remember { mutableStateOf<CameraPreview?>(null) }
    var runMessage by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Register the Java analysis result listener
    LaunchedEffect(Unit) {
        SimpleImageAnalyzer.setAnalysisResultListener(object : SimpleImageAnalyzer.AnalysisResultListener {
            override fun onAnalysisResult(label: String?, confidence: Float) {
                Log.d("KotlinReceiver", "Label: $label, Confidence: $confidence")
                // Compose 상태 등 원하는 처리 수행
            }
        })
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(android.Manifest.permission.CAMERA), 101)
        }
    }

    LaunchedEffect(showBanner) {
        if (showBanner) {
            delay(2500)          // 2.5초 노출 후
            showBanner = false
        }
    }

    if (showLoading) {
        LoadingDialog()
        LaunchedEffect(Unit) {
            delay(2000)
            showLoading = false
        }
    }

    Scaffold(
        bottomBar = {
            BottomNav(
                currentRoute, onTabSelect, navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            AndroidView(
                factory = {
                    CameraPreview(it).apply {
                        // Camera.PictureCallback implementation as per requirement
                        val callback = Camera.PictureCallback { data, camera ->
                            var photoFile = File(context.filesDir, "captured_image.jpg")
                            FileOutputStream(photoFile).use { it.write(data) }

                            showLoading = true

                            Thread {
                                try {
                                    val result = SimpleImageAnalyzer.analyzeImageFile(context, photoFile.absolutePath)
                                    val json = JSONObject(result)
                                    labelResult = json.getString("label")
                                    confidenceResult = json.getDouble("confidence").toFloat()
                                } catch (e: Exception) {
                                    labelResult = "오류"
                                    confidenceResult = 0f
                                } finally {
                                    showLoading = false
                                    // 촬영한 이미지 삭제
                                    if (photoFile.exists()) {
                                        photoFile.delete()
                                        Log.d("DeleteImage","사진이 정상적으로 삭제됨")
                                    }

                                    // Evaluate confidence and navigate accordingly
                                    val confidence = confidenceResult
                                    if (confidence != null) {
                                        if (confidence >= 0.5f) {
                                            // confidence가 0.5 이상이면 label에 따라 ReformNavHost로 이동
                                            val materialType = when (labelResult?.lowercase()) {
                                                "can" -> "캔"
                                                "plastic bottle" -> "플라스틱"
                                                "food-wrapper" -> "비닐"
                                                "paper" -> "종이"
                                                else -> "기타"
                                            }

                                            Log.d("Result", "높은 신뢰도 - ReformNavHost로 이동: $materialType")

                                            // UI 스레드에서 네비게이션 실행
                                            Handler(Looper.getMainLooper()).post {
                                                navController.navigate("reform/$materialType") {
                                                    // 현재 화면을 스택에서 제거하여 뒤로가기 방지
                                                    popUpTo(navController.graph.startDestinationId) {
                                                        inclusive = true
                                                    }
                                                    // 같은 destination으로의 중복 이동 방지
                                                    launchSingleTop = true
                                                }
                                            }
                                        } else {
                                            // confidence가 0.5 미만이면 기존 메시지 표시
                                            Log.d("Result", "일반쓰레기 캇!")
                                            runMessage = true
                                            coroutineScope.launch {
                                                delay(2500)
                                                runMessage = false
                                            }
                                        }
                                    }
                                }
                            }.start()
                        }
                        setPictureCallback(callback)
                        cameraView.value = this
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .weight(0.55f)
                    .clip(MaterialTheme.shapes.large)
                    .background(Color(0xFFEEEEEE))
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        darkGreen.copy(alpha = 0.7f),
                        shape = MaterialTheme.shapes.extraLarge
                    )
                    .clickable {
                        showLoading = true
                        cameraView.value?.takePicture()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "촬영",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
    // Show detection message banner if triggered
    if (runMessage) {
        DetectionBanner()
    }
}

@Composable
fun LoadingDialog() {
    var dotCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            dotCount = (dotCount + 1) % 4
        }
    }

    val dots = ".".repeat(dotCount)

    AlertDialog(
        onDismissRequest = {},
        confirmButton = {},
        title = null,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = Color(0xFF4CAF50),
                    trackColor = Color(0xFFC8E6C9)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("AI 분석중$dots", fontSize = 16.sp)
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    )
}

@Composable
fun DetectionBanner(
    message: String = "리폼이 불가능합니다.",
    bgColor: Color = Color(0xFFB00020),
    textColor: Color = Color.White,
    topMargin: Dp = 32.dp
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(top = topMargin, start = 24.dp, end = 24.dp, bottom = 8.dp)
            .shadow(4.dp, RoundedCornerShape(50))
            .clip(RoundedCornerShape(50))
            .background(bgColor.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
        )
    }
}

@Composable
fun BottomNav(
    currentRoute: String,
    onTabSelect: (String) -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    val email = sharedPref.getString("email", null)
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "홈") },
            selected = currentRoute == "home",
            onClick = {navController.navigate("main")},
            label = { Text("홈") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.PhotoCamera, contentDescription = "카메라") },
            selected = currentRoute == "camera",
            onClick = {},
            label = { Text("카메라") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "프로필") },
            selected = currentRoute == "profile",
            onClick = {if (email != null) {
                navController.navigate("profile/$email")}},
            label = { Text("프로필") }
        )
    }
}