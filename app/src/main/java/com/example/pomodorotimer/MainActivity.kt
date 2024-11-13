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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import com.example.pomodorotimer.ui.theme.Coffe
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sin


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoffeeMugTimer()
        }
    }
}

@Composable
fun CoffeeMugTimer() {
    var progress by remember { mutableFloatStateOf(0f) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var buttonText by remember { mutableStateOf("Start") }
    var statusText by remember { mutableStateOf("Work") }
    val workTimeInSeconds = 1 * 60
    val restTimeInSeconds = 1 * 60
    val rounded = 90
    var elapsedTime by remember { mutableFloatStateOf(0f) }
    var elapsedRestTime by remember { mutableIntStateOf(restTimeInSeconds) }
    var timeToShow by remember { mutableFloatStateOf(0f) }
    val widthFraction = 0.75f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 2000, easing = LinearEasing), label = ""
    )

    fun resetTimer() {
        isTimerRunning = false
        buttonText = "Start"
        statusText = "Work"
        progress = 0f
        elapsedTime = 0f
        elapsedRestTime = restTimeInSeconds
    }

    LaunchedEffect(key1 = isTimerRunning) {
        if (isTimerRunning) {
            timeToShow = elapsedTime
            while (elapsedTime < workTimeInSeconds) {
                delay(1000)
                elapsedTime += 1f
                timeToShow += 1f
                progress = elapsedTime / workTimeInSeconds
            }

            timeToShow = elapsedRestTime.toFloat()
            statusText = "Rest"
            while (elapsedRestTime > 0) {
                delay(1000)
                elapsedRestTime -= 1
                timeToShow -= 1
                progress = (elapsedRestTime.toFloat() / restTimeInSeconds)
            }

            resetTimer()
        }
    }

    fun formatTime(seconds: Float): String {
        val minutes = (seconds / 60).toInt()
        val secs = (seconds % 60).toInt()
        return String.format(locale = Locale.ENGLISH ,"%02d:%02d", minutes, secs)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.weight(0.1f))

        Column {
            Text(text = statusText)
            Text(text = formatTime(timeToShow))
        }

        Spacer(modifier = Modifier.weight(0.1f))

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(widthFraction)
                    .height(500.dp)
                    .clip(
                        RoundedCornerShape(
                            bottomStart = rounded.dp,
                            bottomEnd = rounded.dp,
                            topStart = 5.dp,
                            topEnd = 5.dp
                        )
                    )
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

            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .wrapContentWidth(Alignment.Start)
            ) {
                Canvas(modifier = Modifier
                    .fillMaxSize(widthFraction)
                    .wrapContentWidth(Alignment.Start)
                ) {

                    val mugWidth = size.width * widthFraction
                    val mugHeight = size.height
                    val mugHandleY = mugHeight * 0.2f
                    val mugHandleX = mugWidth * 0f

                    drawArc(
                        color = Color.LightGray,
                        startAngle = -90f,
                        sweepAngle = 180f,
                        useCenter = false,
                        size = Size(450f, 450f),
                        topLeft = Offset(mugHandleX - 225f, mugHandleY),
                        style = Stroke(width = 80f)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(0.1f))

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(start = 70.dp, end = 70.dp)
        ){

            Button(onClick = {
                if (isTimerRunning) {
                    isTimerRunning = false
                    buttonText = "Start"
                } else {
                    isTimerRunning = true
                    buttonText = "Pause"
                }
            }) {
                Text(text = buttonText)
            }
            Spacer(modifier = Modifier.weight(0.1f))

            Button(onClick = {
                isTimerRunning = false
                buttonText = "Start"
                progress = 0f
                elapsedTime = 0f
            }) {
                Text(text = "Stop")
            }

        }

        Spacer(modifier = Modifier.weight(0.1f))

    }
}

@Composable
fun DrawWaves(progress: Float, modifier: Modifier = Modifier, backgroundColor: Color = Color.Gray, liquidColor: Color = Color.Red) {
    val waveHeight = 60f
    val maxWaveHeight = 30f
    var waveAmplitude by remember { mutableFloatStateOf(0f) }
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

            val waveColor: Color = lerp(backgroundColor, liquidColor, progress.coerceIn(0f, 0.8f))

            for (x in 0..width.toInt() step 10) {
                val waveDirection = sin(2 * PI * (x / width) + waveOffset.value)

                val y =
                    (waveYOffset - waveAmplitude * waveDirection ).toFloat()

                drawLine(
                    start = Offset(x.toFloat(), waveYOffset),
                    end = Offset(x.toFloat(), y),
                    color = waveColor,
                    strokeWidth = 10f
                )

                path.lineTo(x.toFloat(), y)

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
    CoffeeMugTimer()
}