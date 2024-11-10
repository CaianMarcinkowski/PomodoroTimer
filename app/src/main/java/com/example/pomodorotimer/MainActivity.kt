package com.example.pomodorotimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.lerp
import com.example.pomodorotimer.ui.theme.Coffe
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

        }
    }
}

@Composable
fun PomodoroTimer() {
    var progress by remember { mutableStateOf(0f) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var buttonText by remember { mutableStateOf("Iniciar") }
    val totalTimeInSeconds = 2 * 60
    val rounded = 90
    var elapsedTime by remember { mutableStateOf(0f) }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
    )

    var coroutimeScope = rememberCoroutineScope()

    LaunchedEffect(key1 = isTimerRunning) {
        if (isTimerRunning) {
            while (elapsedTime < totalTimeInSeconds) {
                delay(1000)
                elapsedTime += 1f
                progress = elapsedTime / totalTimeInSeconds
            }
            isTimerRunning = false
            buttonText = "Iniciar"
            progress = 0f
            elapsedTime = 0f
        }
    }

    fun formatTime(seconds: Float): String {
        val minutes = (seconds / 60).toInt()
        val secs = (seconds % 60).toInt()
        return String.format("%02d:%02d", minutes, secs)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Text(text = formatTime(elapsedTime))

        Spacer(modifier = Modifier.weight(0.1f))

        /*
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(bottom = 16.dp)
                .fillMaxSize(),
            color = Color.Blue,
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        */

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .clip(RoundedCornerShape(bottomStart = rounded.dp, bottomEnd = rounded.dp))
                .background(Color.LightGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .fillMaxHeight(animatedProgress)
                    .background(Coffe)
                    .clip(RoundedCornerShape(bottomStart = rounded.dp, bottomEnd = rounded.dp))
            )

            DrawWaves(
                progress = animatedProgress,
                modifier = Modifier.fillMaxSize(),
                backgroundColor = Coffe,
                liquidColor = Color.LightGray
            )
        }

        Spacer(modifier = Modifier.weight(0.1f))

        Button(onClick = {
            if (isTimerRunning) {
                isTimerRunning = false
                buttonText = "Iniciar"
            } else {
                isTimerRunning = true
                buttonText = "Pausar"
            }
        }) {
            Text(text = buttonText)
        }
    }
}

@Composable
fun DrawWaves(progress: Float, modifier: Modifier = Modifier, backgroundColor: Color = Color.Gray, liquidColor: Color = Color.Red) {
    val waveHeight = 60f
    val maxWaveHeight = 30f
    var waveAmplitude by remember { mutableStateOf(0f) }
    val waveOffset = remember { Animatable(0f) }

    LaunchedEffect(waveOffset) {
        while (true) {
            waveOffset.animateTo(
                targetValue = waveOffset.value + 2 * PI.toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 2000,
                        easing = LinearEasing
                    )
                )
            )
        }
    }

    var showWaves by remember { mutableStateOf(false) }

    LaunchedEffect(progress) {
        if (progress > 0.02f) {
            showWaves = true
        }
    }

    if (showWaves) {
        Canvas(modifier = modifier) {
            val width = size.width
            val height = size.height
            val waveYOffset = height * (1 - progress)
            val path = Path()

            waveAmplitude = waveHeight * progress
            waveAmplitude = (waveHeight * progress).coerceAtMost(maxWaveHeight)

            path.moveTo(0f, height)

            val waveColor: Color = lerp(backgroundColor, liquidColor, progress)

            for (x in 0..width.toInt() step 10) {
                val waveDirection = sin(2 * PI * (x / width.toFloat()) + waveOffset.value)


                val y =
                    (waveYOffset - waveAmplitude * waveDirection ).toFloat()


                drawLine(
                    start = Offset(x.toFloat(), waveYOffset),
                    end = Offset(x.toFloat(), y),
                    color = waveColor,
                    strokeWidth = 10f
                )

                path.lineTo(x.toFloat(), y.toFloat())

            }

            path.lineTo(width, waveYOffset)
            path.lineTo(0f, waveYOffset)
            path.close()

        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PomodoroTimer()
}