package com.rtknits.rt_knits_samplefinder.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.rtknits.rt_knits_samplefinder.R
import com.rtknits.rt_knits_samplefinder.ui.theme.RtknitsSampleFinderTheme
import kotlin.math.max
import kotlin.math.min

const val maxPoints = 40;

@Composable
fun StrengthChart(data: List<Int>, modifier: Modifier = Modifier, graphOnly: Boolean = false,  ) {
    val context = LocalContext.current
    val chartData = data.takeLast(maxPoints)
    val maxValue = 100
    val minValue = 0
    val newestData = if (data.isEmpty()) 0 else data.last()

    Box() {
        Canvas(modifier = modifier) {
            // slightly make the canvas width bigger, because we want to overflow to the left
            // we want to overflow to hide the jankey cubic bezier transition.
            // (when we remove the last element, the smoothing curve is recalculated.
            // This recalculation looks janky to the user, so we hide it offscreen by shifting everything to the left)
            val canvasWidth = size.width * 1.15F
            val canvasHeight = size.height
            val columnWidth = canvasWidth / (chartData.size + 1)
            val heightFactor = canvasHeight / (maxValue - minValue)

            val path = Path()
            for (i in 0 until chartData.size - 1) {
                val chartY = min(max(chartData[i], 1), 99)
                val nextChartY = min(max(chartData[i + 1], 1), 99)

                // notice the (i - 2), we are shifting the start pos to the left off screen.
                val startX = columnWidth * (i - 2)
                val startY = canvasHeight - (chartY - minValue) * heightFactor
                val stopX = columnWidth * (i - 1)
                val stopY = canvasHeight - (nextChartY - minValue) * heightFactor

                if (i == 0) {
                    path.moveTo(startX, startY)
                } else {
                    val controlX1 = (startX + stopX) / 2
                    val controlX2 = (startX + stopX) / 2
                    path.cubicTo(controlX1, startY, controlX2, stopY, stopX, stopY)
                }
            }

            drawPath(
                path = path,
                color = Color(ContextCompat.getColor(context, R.color.rt_blue)),
                style = Stroke(width = 6f, cap = StrokeCap.Butt)
            )

            // drawing the background grid
            val gridColor = Color.Gray.copy(alpha = 0.3f) // Adjust alpha for grid transparency
            val gridStrokeWidth = 1f // Adjust for grid line thickness
            val gridSize = 50f // Adjust for grid size

            for (x in 0 until size.width.toInt() step gridSize.toInt()) {
                drawLine(
                    color = gridColor,
                    start = Offset(x.toFloat(), 0f),
                    end = Offset(x.toFloat(), size.height),
                    strokeWidth = gridStrokeWidth
                )
            }

            for (y in 0 until size.height.toInt() step gridSize.toInt()) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y.toFloat()),
                    end = Offset(size.width, y.toFloat()),
                    strokeWidth = gridStrokeWidth
                )
            }
        }

        if(!graphOnly){
            // draw the middle text
            Column(modifier = Modifier.align(Alignment.Center)
                , horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$newestData",
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    text = strengthToTip(newestData),
                    style = MaterialTheme.typography.headlineLarge,

                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StrengthPreview() {
    RtknitsSampleFinderTheme {
        StrengthChart(
            listOf(1, 2, 3, 4, 5, 80, 50,3,4,6,3,12,2,32,1,9,23,23,4,5,4), modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        )
    }
}
