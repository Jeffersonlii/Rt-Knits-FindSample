package com.rtknits.rt_knits_showroom_logger

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.rtknits.rt_knits_showroom_logger.api.Operation
import com.rtknits.rt_knits_showroom_logger.api.SampleInfo
import com.rtknits.rt_knits_showroom_logger.components.CommitChangesDialog
import com.rtknits.rt_knits_showroom_logger.components.FailedSanitizationDialog
import com.rtknits.rt_knits_showroom_logger.components.decodeHex
import com.rtknits.rt_knits_showroom_logger.components.rememberSaveableMutableStateListOf
import com.rtknits.rt_knits_showroom_logger.scanners.ScannerChooser
import com.rtknits.rt_knits_showroom_logger.scanners.ScannerService

data class ActionOptions(
    val title: String,
    var displayColor: Color = Color.Black,
    var assocSamples: SnapshotStateList<String> = SnapshotStateList(),
    var conflictingSamples: SnapshotStateList<String> = SnapshotStateList(),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorParent(scannerService: ScannerService) {
    val context = LocalContext.current
    val api = APIProvider.current
    val addList = rememberSaveableMutableStateListOf<String>()
    val removeList = rememberSaveableMutableStateListOf<String>()
    val sampleInfoMap = remember { mutableStateMapOf<String, SampleInfo?>() }
    val previousSampleOperationMap = remember { mutableStateMapOf<String, Operation?>() }

    val actions: List<ActionOptions> = remember {
        listOf(
            // any samples that are in the addList should NOT be in the removeList at the same time!
            ActionOptions(
                "Add to Showroom",
                Color(ContextCompat.getColor(context, R.color.rt_blue)),
                assocSamples = addList,
                conflictingSamples = removeList
            ),
            ActionOptions(
                "Remove from Showroom",
                Color(ContextCompat.getColor(context, R.color.rt_red)),
                assocSamples = removeList,
                conflictingSamples = addList
            )
        )
    }
    var curTab by remember { mutableStateOf(0) }
    val isScanModeOn = remember { mutableStateOf(false) }
    var isConfirmationDialogOpen by remember { mutableStateOf(false) }
    var isErrorDialogOpen by remember { mutableStateOf(false) }

    var conflictingSamples by remember { mutableStateOf(listOf<String>()) }
    var nonExistentSamples by remember { mutableStateOf(listOf<String>()) }
    var invalidOperationsSamples by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(isScanModeOn.value) {
        if (isScanModeOn.value) {
            val listToPushTo = actions[curTab].assocSamples;
            scannerService.startInventorying { epc ->
                val detectedSampleId = epc.decodeHex().uppercase()

                // if string has non alphanumerics, it is not a valid Sampleid (likely an unset new RFID tag)
                if (!detectedSampleId.matches("^[A-Za-z0-9]+$".toRegex())) return@startInventorying;

                // dont add already added samples
                if (listToPushTo.contains(detectedSampleId)) return@startInventorying;

                listToPushTo.add(detectedSampleId)
                if (!sampleInfoMap.containsKey(detectedSampleId)) {
                    api.getSampleInformation(detectedSampleId) {
                        if (it.success) {
                            sampleInfoMap[detectedSampleId] = it.sampleInfo
                        }
                    }
                }
                if (!previousSampleOperationMap.containsKey(detectedSampleId)) {
                    api.getSamplePreviousOperation(detectedSampleId) {
                        previousSampleOperationMap[detectedSampleId] = it
                    }
                }
            };

        } else {
            scannerService.stopInventorying();
        }
    }

    Column(
        modifier = Modifier.fillMaxHeight(),
    ) {
        PrimaryTabRow(
            selectedTabIndex = curTab,
        ) {
            actions.forEachIndexed { index, action ->
                Tab(
                    enabled = !isScanModeOn.value,
                    selected = curTab == index,
                    onClick = {
                        curTab = index
                    },
                    text = {
                        Text(
                            text = "${action.title} (${action.assocSamples.size})",
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color =
                            if (isScanModeOn.value && actions[curTab] != action) {
                                Color.Gray
                            } else {
                                action.displayColor
                            }
                        )
                    },
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
        ) {
            SelectorBody(
                sampleIds = actions[curTab].assocSamples,
                conflictingSamples = actions[curTab].conflictingSamples,
                borderStroke = BorderStroke(1.dp, actions[curTab].displayColor),
                modifier = Modifier.weight(1f),
                containerColor = actions[curTab].displayColor.copy(alpha = 0.1f),
                isScanModeOnState = isScanModeOn,
                sampleInfoMap = sampleInfoMap
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(8.dp, 0.dp)
                .padding(bottom = 8.dp)
        ) {

            if (isScanModeOn.value) {
                OutlinedButton(
                    onClick = { isScanModeOn.value = !isScanModeOn.value },
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Cancel", modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            } else {
                FilledTonalButton(
                    onClick = { isScanModeOn.value = !isScanModeOn.value },
                    modifier = Modifier.weight(1f),
                    enabled = !isScanModeOn.value
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Scan for Samples",
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }

            if (!isScanModeOn.value) {
                Button(
                    enabled = addList.isNotEmpty() || removeList.isNotEmpty(),
                    onClick = {
                        conflictingSamples = addList.filter { it in removeList }
                        nonExistentSamples = (
                                addList.filter { !sampleInfoMap.containsKey(it) || sampleInfoMap[it] == null } +
                                        removeList.filter { !sampleInfoMap.containsKey(it) || sampleInfoMap[it] == null }
                                ).distinct()
                        invalidOperationsSamples = (addList.filter {
                            previousSampleOperationMap.containsKey(it) && previousSampleOperationMap[it] == Operation.ADDITION
                        } + removeList.filter {
                            previousSampleOperationMap.containsKey(it) && previousSampleOperationMap[it] == Operation.REMOVAL
                        }).distinct()

                        if (conflictingSamples.isNotEmpty() ||
                            nonExistentSamples.isNotEmpty() ||
                            invalidOperationsSamples.isNotEmpty()
                        ) {
                            isErrorDialogOpen = true
                        } else {
                            isConfirmationDialogOpen = true;
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Commit Changes",
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                }
            }
        }
    }
    when {
        isConfirmationDialogOpen -> {
            CommitChangesDialog(
                onConfirmation = {
                    addList.clear()
                    removeList.clear()
                    previousSampleOperationMap.clear()
                    isConfirmationDialogOpen = false
                },
                onDismissRequest = {
                    isConfirmationDialogOpen = false
                },
                adds = addList,
                removes = removeList
            )
        }

        isErrorDialogOpen -> {
            FailedSanitizationDialog(
                onDismissRequest = { isErrorDialogOpen = false },
                conflictingSamples = conflictingSamples,
                nonExistentSamples = nonExistentSamples,
                invalidOperationSamples = invalidOperationsSamples
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun SelectorParentPreview() {
//    RtKnitsShowroomLoggerTheme {
    SelectorParent(
        scannerService = ScannerChooser.getAttachedScanner()
    )
//    }
}

