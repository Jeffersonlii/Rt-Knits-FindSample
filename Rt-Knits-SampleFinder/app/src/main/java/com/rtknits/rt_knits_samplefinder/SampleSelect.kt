package com.rtknits.rt_knits_samplefinder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rtknits.rt_knits_samplefinder.ui.theme.RtknitsSampleFinderTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleSelect(sheetState: SheetState, sampleIds: SnapshotStateList<String>) {
    var inputSampleId by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .padding(12.dp)
            .padding(bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Select Samples to Locate - ${sampleIds.size}",
            style = MaterialTheme.typography.titleLarge,
        )

        OutlinedCard(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            border = BorderStroke(1.dp, Color.Black),
            modifier = Modifier.weight(1f)

        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                when (sampleIds.size) {
                    0 -> {
                        Text(
                            text = "No Samples Selected",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(0.dp, 12.dp)
                        )
                    }

                    else -> {
                        LazyColumn(state = listState) {
                            items(sampleIds.size) { index ->
                                val sampleId = sampleIds[index]

                                ListItem(
                                    headlineContent = { Text(sampleId) },
                                    trailingContent = {
                                        Icon(
                                            Icons.Filled.Delete,
                                            contentDescription = "delete item",
                                            modifier = Modifier.clickable {
                                                sampleIds.removeAt(index)
                                            }
                                        )
                                    },
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
        ) {
            OutlinedTextField(
                value = inputSampleId,
                onValueChange = { inputSampleId = it },
                singleLine = true,
                label = { Text("Enter SampleID here...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "search icon") },
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() },
                    onPrevious = { focusManager.clearFocus() }),
                trailingIcon = {
                    if (inputSampleId != "") Icon(
                        Icons.Filled.Close,
                        contentDescription = "close icon",
                        modifier = Modifier.clickable {
                            inputSampleId = ""
                        }
                    )
                }
            )



            when {
                inputSampleId != "" -> {
                    IconButton(modifier = Modifier.padding(top = 8.dp), onClick = {
                        scope.launch {
                            // scroll to bottom of list of sampleids
                            listState.animateScrollToItem(sampleIds.size - 1)

                            // expand sheet to fullest so our input remains visible
                            sheetState.expand()
                        }

                        // dont add if sample is already added
                        if (sampleIds.find { s -> s.lowercase() == inputSampleId.lowercase() } == null) {
                            sampleIds.add(inputSampleId.uppercase())
                        }
                        inputSampleId = ""
                    }) {
                        Icon(Icons.Outlined.Add, contentDescription = "Localized description")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SampleSelectorPreview() {
    RtknitsSampleFinderTheme {
        SampleSelect(
            rememberModalBottomSheetState(),
            remember { mutableStateListOf<String>("a") })
    }
}

