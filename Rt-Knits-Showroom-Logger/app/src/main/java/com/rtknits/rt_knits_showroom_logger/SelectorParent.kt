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
import com.rtknits.rt_knits_showroom_logger.components.decodeHex
import com.rtknits.rt_knits_showroom_logger.components.rememberSaveableMutableStateListOf
import com.rtknits.rt_knits_showroom_logger.scanners.ScannerChooser
import com.rtknits.rt_knits_showroom_logger.scanners.ScannerService

enum class ActionOptions(
    val title: String,
    var displayColor: Color = Color.Black,
    var assocSamples: SnapshotStateList<String> = SnapshotStateList<String>()
) {
    ADD("Add to Showroom"),
    REMOVE("Remove from Showroom")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorParent() {
    val context = LocalContext.current
    val scannerService by remember { mutableStateOf<ScannerService?>(ScannerChooser.getAttachedScanner()) }

    var selectedTab by remember { mutableStateOf(ActionOptions.ADD) }
    var tabState by remember { mutableStateOf(selectedTab.ordinal) }
    val addList = rememberSaveableMutableStateListOf<String>()
    val removeList = rememberSaveableMutableStateListOf<String>()
    LaunchedEffect(Unit) {
        ActionOptions.ADD.assocSamples = addList;
        ActionOptions.ADD.displayColor = Color(ContextCompat.getColor(context, R.color.rt_blue))
        ActionOptions.REMOVE.assocSamples = removeList;
        ActionOptions.REMOVE.displayColor = Color(ContextCompat.getColor(context, R.color.rt_red))
    }

    var isScanModeOn by remember { mutableStateOf(false) }

    var isConfirmationDialogOpen by remember { mutableStateOf(false) }

    LaunchedEffect(isScanModeOn) {
        if (isScanModeOn) {
            val listToPushTo = selectedTab.assocSamples;
            scannerService?.startInventorying { epc ->
                val detectedSampleId = epc.decodeHex().uppercase()

                // if string has non alphanumerics, it is not a valid Sampleid (likely an unset new RFID tag)
                if (!detectedSampleId.matches("^[A-Za-z0-9]+$".toRegex())) return@startInventorying;

                // dont add already added samples
                if (listToPushTo.contains(detectedSampleId)) return@startInventorying;

                listToPushTo.add(detectedSampleId)
            };

        } else {
            scannerService?.stopInventorying();
        }
    }

    Column(
        modifier = Modifier.fillMaxHeight(),
    ) {
        PrimaryTabRow(
            selectedTabIndex = tabState,
        ) {

            ActionOptions.entries.forEachIndexed { index, action ->
                Tab(
                    enabled = !isScanModeOn,
                    selected = tabState == index,
                    onClick = {
                        tabState = index
                        selectedTab = action
                    },
                    text = {
                        Text(
                            text = "${action.title} (${action.assocSamples.size})",
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color =
                            if (isScanModeOn && selectedTab != action) {
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
                selectedTab.assocSamples,
                borderStroke = BorderStroke(1.dp, selectedTab.displayColor),
                modifier = Modifier.weight(1f),
                containerColor = selectedTab.displayColor.copy(alpha = 0.1f),
                isScanModeOn = isScanModeOn
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(8.dp, 0.dp)
                .padding(bottom = 8.dp)
        ) {

            if (isScanModeOn) {
                OutlinedButton(
                    onClick = { isScanModeOn = !isScanModeOn }, modifier = Modifier.weight(1f)
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
                    onClick = { isScanModeOn = !isScanModeOn },
                    modifier = Modifier.weight(1f),
                    enabled = !isScanModeOn
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

            Button(
                onClick = {
                    isConfirmationDialogOpen = true;
                },
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Commit Changes", modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

            }
        }


    }
    if (isConfirmationDialogOpen) {
        ConfirmationDialog(
            onConfirmation = {},
            onDismissRequest = {
                isConfirmationDialogOpen = false
            },
            adds = addList,
            removes = removeList
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SelectorParentPreview() {
//    RtKnitsShowroomLoggerTheme {
    SelectorParent()
//    }
}

