package com.rtknits.rt_knits_samplefinder

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import com.rtknits.rt_knits_samplefinder.components.SampleIdInput
import com.rtknits.rt_knits_samplefinder.components.annotateSampleId
import com.rtknits.rt_knits_samplefinder.ui.theme.RtknitsSampleFinderTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleSelect(sheetState: SheetState, sampleIds: SnapshotStateList<String>) {
    val nc = NavControllerContext.current

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val onAddSample = remember {
        { inputSampleId: String ->
            scope.launch {
                // scroll to bottom of list of sampleids
                listState.animateScrollToItem(sampleIds.size - 1)

                // expand sheet to fullest so our input remains visible
                sheetState.expand()
            }

            // dont add if sample is already added
            if (sampleIds.find { s -> s.lowercase() == inputSampleId.lowercase() } == null) {
                sampleIds.add(inputSampleId)
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(12.dp)
            .padding(bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "${sampleIds.size} Sample(s) Selected",
                style = MaterialTheme.typography.titleLarge,
            )

            if (sampleIds.size > 0) {
                Button(
                    shape = RoundedCornerShape(size = 4.dp), // Adjust corner radius as needed
                    colors = ButtonDefaults
                        .buttonColors(
                            containerColor =
                            Color(ContextCompat.getColor(context, R.color.rt_blue))
                        ),
                    onClick = {
                        if (nc != null) {
                            gotoScan(nc, sampleIds)
                        }
                    },
                ) {
                    Text(text = "Find")
                }
            }
        }


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
                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No Samples Selected",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .padding(0.dp, 12.dp)
                            )
                        }
                    }

                    else -> {
                        LazyColumn(state = listState) {
                            items(sampleIds.size) { index ->
                                val sampleId = sampleIds[index]

                                ListItem(
                                    headlineContent = { Text(annotateSampleId(sampleId)) },
                                    trailingContent = {
                                        IconButton(onClick = { sampleIds.removeAt(index) }) {
                                            Icon(
                                                Icons.Filled.Delete,
                                                contentDescription = "delete item",
                                                modifier = Modifier.fillMaxHeight()
                                            )
                                        }
                                    },
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
        SampleIdInput(onAddSample)
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
                    "asg",
                    "a",
//                    "a",
//                    "a",
//                    "a",
//                    "a",
//                    "a",
//                    "a",
//                    "a",
//                    "a",
//                    "a",
//                    "a"
                )
            })
    }
}

