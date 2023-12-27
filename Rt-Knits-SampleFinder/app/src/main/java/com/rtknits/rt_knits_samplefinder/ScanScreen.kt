package com.rtknits.rt_knits_samplefinder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import com.rtknits.rt_knits_samplefinder.components.KeepScreenOn
import com.rtknits.rt_knits_samplefinder.components.OnLifecycleEvent
import com.rtknits.rt_knits_samplefinder.components.StrengthChart
import com.rtknits.rt_knits_samplefinder.scanners.ChainwayScannerServiceImpl
import com.rtknits.rt_knits_samplefinder.scanners.ScannerChooser
import com.rtknits.rt_knits_samplefinder.scanners.SunmiScannerServiceImpl
import com.rtknits.rt_knits_samplefinder.ui.theme.RtknitsSampleFinderTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.concurrent.thread
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

val maxPoints = 20;

@Composable
fun ScanScreen(sampleID: String) {
    KeepScreenOn()

    var lastPeriodicMax by remember {
        mutableStateOf(0)
    }
    val points = remember { mutableStateListOf<Int>() }

    val scannerService = remember { ScannerChooser().getAttachedScanner() }

    val context = LocalContext.current

    // lifecycle events for stopping / pausing the scan
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                // the RFID of the sample is simply the hex of the sampleID !
                scannerService.startLocateRFID(context, encodeHex(sampleID)) {
                    lastPeriodicMax = max(it,lastPeriodicMax)
                }
            }
            Lifecycle.Event.ON_PAUSE -> {
                scannerService.stopLocateRFID()
            }
            Lifecycle.Event.ON_DESTROY -> {
                scannerService.cleanup();
            }
            else -> Unit
        }
    }

    // thread for periodically updating the graph
    DisposableEffect(Unit) {
        val updateCallBackThread = thread(start = true, isDaemon = true) {
            while (true) {
                try {
                    Thread.sleep(100)
                    points.add(lastPeriodicMax);
                    lastPeriodicMax = 0
                } catch (e: InterruptedException) {
                    break
                }
            }
        }
        onDispose {
            updateCallBackThread.interrupt()
        }
    }
    Scaffold(
        topBar = { },
        bottomBar = {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                Text(
                    text = "Scanning for SampleID : $sampleID",
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
        },
    ) { innerPadding ->
        OutlinedCard(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            border = BorderStroke(1.dp, Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(innerPadding)
                .padding(bottom = 16.dp)
        ) {
            StrengthChart(
                points, modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun ScanPreview() {
    RtknitsSampleFinderTheme {
        ScanScreen("test")
    }
}

fun encodeHex(input: String): String {
    return input.map { it.code.toString(16) }.joinToString("").uppercase(
        Locale.getDefault()
    )
}

