package com.rtknits.rt_knits_showroom_logger

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.rtknits.rt_knits_showroom_logger.api.SampleInfo
import com.rtknits.rt_knits_showroom_logger.components.ToggleableSampleInput
import kotlinx.coroutines.launch

@Composable
fun SelectorBody(
    sampleIds: MutableList<String>,
    modifier: Modifier = Modifier,
    conflictingSamples: MutableList<String> = mutableListOf(),
    borderStroke: BorderStroke = BorderStroke(1.dp, Color.Black),
    containerColor: Color = Color.White,
    isScanModeOnState: MutableState<Boolean> = mutableStateOf(false),
    sampleInfoMap: SnapshotStateMap<String, SampleInfo?>
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val onAddManually = remember(key1 = sampleIds) {
        { sampleId: String ->
            scope.launch {
                // scroll to bottom of list of sampleids
                listState.animateScrollToItem(sampleIds.size - 1)
            }

            // dont add if sample is already added
            if (sampleIds.find { s -> s.lowercase() == sampleId.lowercase() } == null) {
                sampleIds.add(sampleId)
            }
        }
    }

    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
        ),
        modifier = modifier,
        border = borderStroke,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            when (sampleIds.size) {
                0 -> {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        if (!isScanModeOnState.value) {
                            Box(modifier = Modifier.weight(1f))

                            Text(
                                text = "No Samples Selected",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            TextButton(
                                onClick = { isScanModeOnState.value = !isScanModeOnState.value },
                                enabled = !isScanModeOnState.value
                            ) {
                                Column(
                                ) {
                                    Text(
                                        "Scan for Samples",
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                }
                            }
                            Box(modifier = Modifier.weight(1f))
                            ToggleableSampleInput(
                                onAdd = onAddManually,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .padding(bottom = 8.dp)
                                    .padding(start = 8.dp)
                                    .align(Alignment.Start)
                            )
                        }
                    }
                }

                else -> {
                    Column(modifier = Modifier.weight(1f)) {
                        LazyColumn(state = listState) {
                            items(sampleIds.size) { index ->
                                val sampleId = sampleIds[index]

                                SelectorItem(
                                    sampleId = sampleId,
                                    isScanModeOn = isScanModeOnState.value,
                                    onDelete = {
                                        sampleIds.removeAt(index)
                                    },
                                    isConflicting = conflictingSamples.contains(sampleId),
                                    sampleInfo = if(sampleInfoMap.containsKey(sampleId)) sampleInfoMap[sampleId] else null
                                )
                                HorizontalDivider()
                                if (!isScanModeOnState.value && index == sampleIds.size - 1) {
                                    ToggleableSampleInput(
                                        onAdd = onAddManually,
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .padding(bottom = 8.dp)
                                            .padding(start = 8.dp)
                                    )
                                }
                            }

                        }
                    }
                }

            }
            if (isScanModeOnState.value) {
                ScanningIndicator()
            }
        }
    }
}

@Composable
fun ScanningIndicator() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Scanning for Samples ...",
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 8.dp),
            style = MaterialTheme.typography.labelLarge
        )
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            color = Color(ContextCompat.getColor(context, R.color.rt_blue)),
        )
    }

}

@Preview(showBackground = true)
@Composable
fun SelectorBodyPreview() {
//    RtKnitsShowroomLoggerTheme {
    SelectorBody(
        remember {
            mutableStateListOf<String>(
                "asg",
                "a",
                "a",
                "a",
                "a",
                "a",
                "a",
                "a",
                "a",
                "a",
                "a",
                "a"
            )
        },
        conflictingSamples = mutableListOf("asg"),
        isScanModeOnState = mutableStateOf(false),
        sampleInfoMap = mutableStateMapOf()
    )
//    }
}



