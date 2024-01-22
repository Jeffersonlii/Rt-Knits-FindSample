package com.rtknits.rt_knits_showroom_logger

import android.graphics.Color.alpha
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
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
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import com.rtknits.rt_knits_showroom_logger.components.SampleIDInput
import com.rtknits.rt_knits_showroom_logger.components.ToggleableSampleInput
import com.rtknits.rt_knits_showroom_logger.components.annotateSampleId
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SelectorBody(
    sampleIds: SnapshotStateList<String>,
    modifier: Modifier = Modifier,
    borderStroke: BorderStroke = BorderStroke(1.dp, Color.Black),
    containerColor: Color = Color.White,
    isScanModeOn: Boolean = false,
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var isManualInput by remember { mutableStateOf(false) }

    val onAddManually = remember {
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
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!isScanModeOn) {
                            Text(
                                text = "No Samples Selected",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .padding(0.dp, 12.dp)
                            )
                            ToggleableSampleInput(
                                isShow = isManualInput,
                                onToggle = {
                                    isManualInput = !isManualInput
                                },
                                onAdd = onAddManually,
                                modifier = Modifier
                                    .padding(end = 8.dp, bottom = 8.dp)
                            )
                        }
                    }
                }

                else -> {
                    Column(modifier = Modifier.weight(1f)) {
                        LazyColumn(state = listState) {
                            items(sampleIds.size) { index ->
                                val sampleId = sampleIds[index]

                                ListItem(
                                    headlineContent = { Text(annotateSampleId(sampleId)) },
                                    trailingContent = {
                                        if (isScanModeOn) return@ListItem

                                        Icon(
                                            Icons.Filled.Delete,
                                            contentDescription = "delete item",
                                            modifier = Modifier.clickable {
                                                sampleIds.removeAt(index)
                                            }
                                        )
                                    },
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                )
                                HorizontalDivider()
                                if (!isScanModeOn && index == sampleIds.size - 1) {
                                    ToggleableSampleInput(
                                        isShow = isManualInput,
                                        onToggle = {
                                            isManualInput = !isManualInput
                                        },
                                        onAdd = onAddManually,
                                        modifier = Modifier
                                            .padding(end = 8.dp, bottom = 8.dp)
                                    )
                                }
                            }

                        }
                    }
                }
            }
            if (isScanModeOn) {
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
//                "asg",
//                "a",
//                "a",
//                "a",
//                "a",
//                "a",
//                "a",
//                "a",
//                "a",
//                "a",
//                "a",
//                "a"
            )
        },
        isScanModeOn = false
    )
//    }
}



