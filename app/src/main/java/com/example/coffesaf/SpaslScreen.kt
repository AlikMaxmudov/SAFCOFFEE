package com.example.coffesaf

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AnimatedLogoSplashScreen(
                    onAnimationComplete = {
                        startActivity(Intent(this, StartActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun AnimatedLogoSplashScreen(onAnimationComplete: () -> Unit) {
    // Анимация прозрачности
    val alpha = remember { Animatable(0f) }
    // Анимация масштаба
    val scale = remember { Animatable(0.5f) }
    // Анимация вращения
    val rotation = remember { Animatable(0f) }
    // Анимация текста (поэтапное появление)
    val textProgress = remember { Animatable(0f) }
    val text = "SAF COFFE"

    // Запуск анимаций
    LaunchedEffect(key1 = true) {
        // Параллельный запуск анимаций
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1500)
            )
        }

        launch {
            scale.animateTo(
                targetValue = 1.2f,
                animationSpec = tween(durationMillis = 1000)
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500)
            )
        }

        launch {
            rotation.animateTo(
                targetValue = 10f,
                animationSpec = tween(durationMillis = 300)
            )
            rotation.animateTo(
                targetValue = -10f,
                animationSpec = tween(durationMillis = 600)
            )
            rotation.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 300)
            )
        }

        // Анимация текста с задержкой
        delay(800) // Задержка перед началом анимации текста
        textProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1500,
                delayMillis = 300
            )
        )

        delay(1000)
        onAnimationComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                modifier = Modifier
                    .size(200.dp)
                    .alpha(alpha.value)
                    .scale(scale.value)
                    .rotate(rotation.value),
                painter = painterResource(id = R.drawable.coffe_icon),
                contentDescription = "App Logo"
            )

            @Composable
            fun TypewriterText(text: String, progress: Float) {
                val visibleChars = (text.length * progress).toInt()

                Text(
                    text = text.substring(0, visibleChars),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            TypewriterText(text = "SAF COFFE", progress = textProgress.value)

        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    MaterialTheme {
        AnimatedLogoSplashScreen(onAnimationComplete = {})
    }
}