package com.example.cleanearth;

import com.example.cleanearth.java.SimpleImageAnalyzer

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.cleanearth.java.CameraPreview
import kotlinx.coroutines.delay
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.util.*

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

    var showLoading by remember { mutableStateOf(false) }
    val pictureBitmap = remember { mutableStateOf<Bitmap?>(null) }
    val cameraView = remember { mutableStateOf<CameraPreview?>(null) }

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
                        setPictureCallback { data, _ ->
                            val bmp = BitmapFactory.decodeByteArray(data, 0, data.size)
                            pictureBitmap.value = bmp
                            Log.d("Camera", "사진 저장 완료")
                        }
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
                    .background(darkGreen.copy(alpha = 0.7f), shape = MaterialTheme.shapes.extraLarge)
                    .clickable {
                        showLoading = true
                        cameraView.value?.takePicture()

                        cameraView.value?.setPictureCallback { data, _ ->
                            val bmp = BitmapFactory.decodeByteArray(data, 0, data.size)
                            pictureBitmap.value = bmp

                            val filename = "captured_${UUID.randomUUID()}.jpg"
                            val imageDir = File(context.filesDir, "images")
                            imageDir.mkdirs()
                            val file = File(imageDir, filename)
                            try {
                                FileOutputStream(file).use { out ->
                                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, out)
                                }
                                Log.d("Camera", "사진 저장됨: ${file.absolutePath}")
                                // Java에 이미지 전달하여 분석 시작
                                SimpleImageAnalyzer.analyzeImageFile(file.absolutePath, object : SimpleImageAnalyzer.ResultListener {
                                    override fun onResult(label: String, confidence: Float) {
                                        Log.d("FlaskResult", "Label: $label, Confidence: $confidence")
                                        // 여기서 label과 confidence 값을 활용할 수 있습니다.
                                        showLoading = false
                                    }
                                    override fun onError(error: String) {
                                        Log.e("FlaskResult", "오류 발생: $error")
                                        showLoading = false
                                    }
                                })
                            } catch (e: Exception) {
                                Log.e("Camera", "사진 저장 중 오류: ${e.message}")
                                showLoading = false
                            }
                        }
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