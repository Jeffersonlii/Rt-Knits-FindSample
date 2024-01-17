package com.rtknits.rt_knits_samplefinder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rtknits.rt_knits_samplefinder.scanners.ChainwayScannerServiceImpl
import com.rtknits.rt_knits_samplefinder.ui.theme.RtknitsSampleFinderTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val nc = NavControllerContext.current

    val focusManager = LocalFocusManager.current
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val sampleIds = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
//        sampleIds.add("HELLO")
//        sampleIds.add("ASD")
//        sampleIds.add("JELLO")
//        sampleIds.add("ONE")
    }

    Scaffold(modifier = Modifier.padding(16.dp), bottomBar = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(12.dp)
        ) {
            ExtendedFloatingActionButton(modifier = Modifier.weight(1f),
                text = { Text("Select Samples") },
                icon = { Icon(Icons.Filled.Add, contentDescription = "add") },
                onClick = {
                    showBottomSheet = true
                })

            if (sampleIds.size > 0) {
                ExtendedFloatingActionButton(
                    modifier = Modifier.weight(1f),
                    text = { Text("Search ${sampleIds.size} Samples") },
                    icon = { Icon(Icons.Filled.Place, contentDescription = "add") },
                    onClick = {

                        // navigate to scanning screen
                        when (sampleIds.size) {
                            1 -> {
                                nc?.navigate(
                                    Screen.ScanSingle.route.replace(
                                        "{sampleID}", sampleIds[0]
                                    )
                                )
                            }
                            else -> {
                                nc?.navigate(
                                    Screen.ScanMultiple.route.replace(
                                        "{sampleIDs}", sampleIds.joinToString(separator = ",")
                                    )
                                )
                            }
                        }
                    },
                )
            }
        }


    }) { padding ->
        Paragraph(modifier = Modifier
            .padding(padding)
            .padding(bottom = 8.dp)
            .fillMaxWidth()
            .clickable { focusManager.clearFocus() })
        when {
            showBottomSheet -> {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = sheetState,
                    modifier = Modifier.imePadding(),
                    content = {
                        // sampleIds are modified within the SampleSelect Composable
                        SampleSelect(sheetState, sampleIds)
                    }
                )
            }
        }
    }
}

@Composable
fun Paragraph(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ), border = BorderStroke(1.dp, Color.Black), modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "SampleFinder assists you in locating samples within your office using by scanning the RFID tags.",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = """
        Instructions:
            1. Input the SampleID of the sample
            2. Scan your surroundings and locate the item via the signal strength
            """, style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Please note, SampleFinder relies on RFID tags attached to the sample. Ensure your item was tagged with RFID at the time of creation.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    RtknitsSampleFinderTheme {
        HomeScreen()
    }
}

