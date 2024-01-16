package com.rtknits.rt_knits_samplefinder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.rtknits.rt_knits_samplefinder.components.KeepScreenOn
import com.rtknits.rt_knits_samplefinder.components.OnLifecycleEvent
import com.rtknits.rt_knits_samplefinder.components.PingSingle
import com.rtknits.rt_knits_samplefinder.scanners.ScannerChooser
import com.rtknits.rt_knits_samplefinder.ui.theme.RtknitsSampleFinderTheme

data class SearchingSample(val sampleId: String, var strength: Int)

@Composable
fun ScanMultiScreen(sampleIDs: Array<String>) {
    KeepScreenOn()
    val scanner = remember { ScannerChooser.getAttachedScanner() }
    val strengths = remember{ Array(sampleIDs.size){
        SearchingSample(sampleIDs[it], 0)}
    }
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                scanner.startLocateMultipleRFID()
                sampleIDs.forEachIndexed { i, sampleId ->
                    scanner.registerRFIDtoLocate(sampleId) {
                        strengths[i].strength = it
                    }
                }
            }

            Lifecycle.Event.ON_PAUSE -> {
                scanner.stopLocateMultipleRFID()
            }

            Lifecycle.Event.ON_DESTROY -> {
                scanner.cleanup()
            }

            else -> Unit
        }
    }

    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        strengths.sortedArrayWith {
            a1, a2 -> a2.strength - a1.strength
        }.forEach  { (sid, strength) ->
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



