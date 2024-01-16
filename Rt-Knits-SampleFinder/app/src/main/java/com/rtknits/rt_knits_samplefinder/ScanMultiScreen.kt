package com.rtknits.rt_knits_samplefinder

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.rtknits.rt_knits_samplefinder.components.KeepScreenOn
import com.rtknits.rt_knits_samplefinder.components.OnLifecycleEvent
import com.rtknits.rt_knits_samplefinder.components.PingSingle
import com.rtknits.rt_knits_samplefinder.components.encodeHex
import com.rtknits.rt_knits_samplefinder.scanners.ChainwayScannerServiceImpl
import com.rtknits.rt_knits_samplefinder.scanners.ScannerChooser
import com.rtknits.rt_knits_samplefinder.scanners.ScannerService
import com.rtknits.rt_knits_samplefinder.ui.theme.RtknitsSampleFinderTheme
import kotlin.concurrent.thread

data class SearchingSample(val sampleId: String, var strength: Int)

@Composable
fun ScanMultiScreen(sampleIDs: Array<String>) {
    KeepScreenOn()
    val scanner = remember { ScannerChooser.getAttachedScanner() }

    val strengths = remember { mutableStateListOf<SearchingSample>() }
    DisposableEffect(Unit) {
        strengths.clear()
        sampleIDs.forEach { id ->
            strengths.add(SearchingSample(id, 0))
        }
        onDispose {
        }
    }

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                Log.i("jeff", "starting")
                scanner.startLocateMultipleRFID()
                sampleIDs.forEachIndexed { i, sampleId ->
                    scanner.registerRFIDtoLocate(encodeHex(sampleId)) { strength ->
                        val cp = strengths[i].copy()
                        cp.strength = strength

                        strengths[i] = cp
                    }
                }
            }

            Lifecycle.Event.ON_PAUSE -> {
                scanner.stopLocateMultipleRFID()
            }

            Lifecycle.Event.ON_DESTROY -> {
                scanner.disconnect()
            }

            else -> Unit
        }
    }

    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        strengths.sortedByDescending { it.strength }.forEach { (sid, strength) ->
            PingSingle(sid, strength)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScanPreview() {
    RtknitsSampleFinderTheme {
        ScanMultiScreen(arrayOf("23443SG", "92374SG", "45480SG"))
    }
}



