package com.rtknits.rt_knits_samplefinder.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.rtknits.rt_knits_samplefinder.scanners.ChainwayScannerServiceImpl
import com.rtknits.rt_knits_samplefinder.scanners.ScannerChooser
import com.rtknits.rt_knits_samplefinder.scanners.ScannerService
import com.rtknits.rt_knits_samplefinder.ui.theme.RtknitsSampleFinderTheme
import kotlin.concurrent.thread
import kotlin.math.max

@Composable
fun LocateSingle(sampleID: String) {
    var lastPeriodicMax by remember {
        mutableStateOf(0)
    }
    val points = remember { mutableStateListOf<Int>() }
    val scanner = remember { ScannerChooser.getAttachedScanner() }

    val context = LocalContext.current

    // lifecycle events for stopping / pausing the scan

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                // the RFID of the sample is simply the hex of the sampleID !
                scanner.startLocateSingleRFID(context, encodeHex(sampleID)) {
                    lastPeriodicMax = max(it, lastPeriodicMax)
                }
            }

            Lifecycle.Event.ON_PAUSE -> {
                scanner.stopLocateSingleRFID()
            }

            Lifecycle.Event.ON_DESTROY -> {
                scanner.disconnect()
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
                    points.add(lastPeriodicMax)
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

    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        StrengthChart(
            points, modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LocateSinglePreview() {
    RtknitsSampleFinderTheme {
        LocateSingle("123")
    }
}
