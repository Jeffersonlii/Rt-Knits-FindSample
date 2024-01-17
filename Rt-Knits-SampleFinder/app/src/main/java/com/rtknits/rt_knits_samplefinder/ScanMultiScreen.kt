package com.rtknits.rt_knits_samplefinder

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.rtknits.rt_knits_samplefinder.components.KeepScreenOn
import com.rtknits.rt_knits_samplefinder.components.OnLifecycleEvent
import com.rtknits.rt_knits_samplefinder.components.PingSingle
import com.rtknits.rt_knits_samplefinder.components.PingSingleState
import com.rtknits.rt_knits_samplefinder.components.encodeHex
import com.rtknits.rt_knits_samplefinder.scanners.ChainwayScannerServiceImpl
import com.rtknits.rt_knits_samplefinder.scanners.ScannerChooser
import com.rtknits.rt_knits_samplefinder.scanners.ScannerService
import com.rtknits.rt_knits_samplefinder.ui.theme.RtknitsSampleFinderTheme
import kotlin.concurrent.thread

data class SearchingSample(val sampleId: String, var state: PingSingleState)

@Composable
fun ScanMultiScreen(sampleIDs: Array<String>) {
    KeepScreenOn()
    val scanner = remember { ScannerChooser.getAttachedScanner() }
    val strengths = remember { mutableStateListOf<SearchingSample>() }

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                sampleIDs.forEach { id ->
                    strengths.add(SearchingSample(id, PingSingleState(id)))
                }

                scanner.startLocateMultipleRFID()
                sampleIDs.forEachIndexed { i, sampleId ->
                    scanner.registerRFIDtoLocate(encodeHex(sampleId)) { strength ->
                        strengths[i].state.ping(strength)
                    }
                }
            }

            Lifecycle.Event.ON_PAUSE -> {
                scanner.stopLocateMultipleRFID()
                strengths.forEach{s-> s.state.finalize()}
                strengths.clear()
                Log.e("jeff", strengths.size.toString())
            }

            Lifecycle.Event.ON_DESTROY -> {
                scanner.disconnect()
            }

            else -> Unit
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp, 0.dp)
    ) {
        itemsIndexed(strengths) { index, s ->
            PingSingle(
                s.sampleId, s.state,
                modifier = if (index <= 1) Modifier.padding(top = 16.dp) else Modifier
            );
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



