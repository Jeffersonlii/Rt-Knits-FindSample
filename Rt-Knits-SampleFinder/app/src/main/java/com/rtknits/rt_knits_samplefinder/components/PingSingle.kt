package com.rtknits.rt_knits_samplefinder.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.rtknits.rt_knits_samplefinder.NavControllerContext
import com.rtknits.rt_knits_samplefinder.R
import com.rtknits.rt_knits_samplefinder.Screen
import com.rtknits.rt_knits_samplefinder.ui.theme.RtknitsSampleFinderTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.max

class PingSingleState(sampleID: String) {
    val strengths = mutableStateListOf<Int>()
    private var lastPeriodicMax by mutableStateOf(0)
    var highestPingOnChart by mutableStateOf(0)
    private val job = Job()

    init {
        CoroutineScope(Dispatchers.Default + job).launch {
            while (isActive) {
                try {
                    delay(100)

                    if (strengths.size > maxPoints) {
                        strengths.removeFirst()
                    }
                    strengths.add(lastPeriodicMax)
                    highestPingOnChart = strengths.max()
                    lastPeriodicMax = 0
                } catch (e: InterruptedException) {
                    break
                }
            }
        }
    }

    fun ping(strength: Int) {
        lastPeriodicMax = max(strength, lastPeriodicMax)
    }

    // call this function when the object is about to be garbage collected
    // to ensure coroutines do not persist and cause mem leaks
    fun finalize() {
        job.cancel()
    }
}

@Composable
fun PingSingle(sampleID: String, state: PingSingleState, modifier: Modifier = Modifier) {
    val nc = NavControllerContext.current
    val context = LocalContext.current

    // lifecycle events for stopping / pausing the scan
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(
            if (state.highestPingOnChart > 0) 2.dp else 1.dp,
            if (state.highestPingOnChart > 0) Color(
                ContextCompat.getColor(
                    context,
                    R.color.rt_red
                )
            ) else Color.Black
        ),
        modifier = modifier
    )
    {
        Box{
            StrengthChart(
                state.strengths,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                graphOnly = true
            )
            Column(
                modifier = Modifier
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = sampleID,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = strengthToTip(state.highestPingOnChart),
                    style = MaterialTheme.typography.bodyLarge
                )
                Box(modifier = Modifier.weight(1f))

                if (state.highestPingOnChart > 0) {
                    ElevatedButton(
                        onClick = {
                            nc?.navigate(
                                Screen.ScanSingle.route.replace(
                                    "{sampleID}", sampleID
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Isolate")
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = "Focus",
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PingSinglePreview() {
    RtknitsSampleFinderTheme {
        PingSingle("23489SG", PingSingleState(""))
    }
}
