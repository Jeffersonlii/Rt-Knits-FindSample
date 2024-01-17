package com.rtknits.rt_knits_samplefinder

import android.widget.Toast
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.material3.TextField
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rtknits.rt_knits_samplefinder.ui.theme.RtknitsSampleFinderTheme
import kotlinx.coroutines.launch

val sampleIDTrailing = "SG"
val trailingOptions = listOf(sampleIDTrailing)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleSelect(sheetState: SheetState, sampleIds: SnapshotStateList<String>) {
    var inputSampleId by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit){
        sheetState.expand()
    }

    Column(
        modifier = Modifier
            .padding(12.dp)
            .padding(bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
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


        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(0.dp, 150.dp)
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
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(60.dp),
        ) {
            OutlinedTextField(
                value = inputSampleId,
                onValueChange = { inputSampleId = it },
                singleLine = true,
                label = { Text("SampleID") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "search icon") },
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
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

            var expanded by remember { mutableStateOf(false) }
            var selectedOptionText by remember { mutableStateOf(sampleIDTrailing) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier
                    .fillMaxHeight()
                    .width(60.dp),
            ) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor(),
                    readOnly = true,
                    label = { Text("") },
                    value = selectedOptionText,
                    onValueChange = {},
//                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    trailingOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                selectedOptionText = selectionOption
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }

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
                            sampleIds.add("$inputSampleId$selectedOptionText".uppercase())
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
            remember {
                mutableStateListOf<String>(
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
                    "a",
                    "a"
                )
            })
    }
}

