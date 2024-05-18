package ke.co.banit.musicknob

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ke.co.banit.musicknob.ui.theme.MusicKnobTheme
import kotlin.math.atan2

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xff101010)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(150.dp)
                        .border(1.dp, Color.Green, RoundedCornerShape(10.dp))
                        .padding(30.dp)
                ) {
                    var volume by remember {
                        mutableFloatStateOf(0f)
                    }
                    val barCount = 20
                    VolumeKnob(
                        modifier = Modifier.size(100.dp)
                    ) {
                        volume = it
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    VolumeBar(
                        modifier = Modifier
                            .fillMaxSize()
                            .height(30.dp),
                        activeBars = (barCount * volume).toInt(),
                        barCount = barCount
                    )
                }
            }

        }
    }

    @Composable
    fun VolumeBar(
        modifier: Modifier = Modifier,
        activeBars: Int = 0,
        barCount: Int = 10
    ) {
        BoxWithConstraints(
            contentAlignment = Alignment.Center,
            modifier = modifier
        ) {
            val barWidth = remember {
                constraints.maxWidth / (2f * barCount)
            }

            Canvas(modifier = modifier) {
                for (i in 0 until barCount) {
                    drawRoundRect(
                        color = if (i in 0..activeBars) Color.Green else Color.Gray,
                        topLeft = Offset(i * barWidth * 2f + barWidth / 2f, 0f),
                        size = Size(barWidth, constraints.maxHeight.toFloat()),
                        cornerRadius = CornerRadius(6f)
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun VolumeKnob(
        modifier: Modifier = Modifier,
        limitingAngle: Float = 25f,
        onValueChange: (Float) -> Unit
    ) {
        var rotation by remember {
            mutableFloatStateOf(limitingAngle)
        }
        var touchX by remember {
            mutableFloatStateOf(0f)
        }
        var touchY by remember {
            mutableFloatStateOf(0f)
        }
        var centerX by remember {
            mutableFloatStateOf(0f)
        }
        var centerY by remember {
            mutableFloatStateOf(0f)
        }

        Image(painter = painterResource(id = R.drawable.volume),
            contentDescription = "Volume Knob",
            modifier = modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    val windowBounds = it.boundsInWindow()
                    centerX = windowBounds.size.width / 2f
                    centerY = windowBounds.size.height / 2f
                }
                .pointerInteropFilter { event ->
                    touchX = event.x
                    touchY = event.y
                    val angle = -atan2(
                        centerX - touchX,
                        centerY - touchY
                    ) * (180f / Math.PI).toFloat()
                    when (event.action) {
                        MotionEvent.ACTION_DOWN,
                        MotionEvent.ACTION_MOVE -> {
                            if (angle !in -limitingAngle..limitingAngle) {
                                val fixedAngle = if (angle in -180f..-limitingAngle) {
                                    360f + angle
                                } else angle

                                rotation = fixedAngle
                                val percent =
                                    (fixedAngle - limitingAngle) / (360f - 2 * limitingAngle)
                                onValueChange(percent)
                                true
                            } else false
                        }

                        else -> {
                            false
                        }
                    }
                }
                .rotate(rotation)
        )
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MusicKnobTheme {
        Greeting("Android")
    }
}
}