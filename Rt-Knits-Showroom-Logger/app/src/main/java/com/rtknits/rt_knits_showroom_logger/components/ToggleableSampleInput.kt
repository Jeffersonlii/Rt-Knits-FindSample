package com.rtknits.rt_knits_showroom_logger.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun ToggleableSampleInput(onAdd: (String) -> Unit, modifier: Modifier = Modifier) {
    var isShowInput by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .requiredHeight(70.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TextButton(
            onClick = { isShowInput = !isShowInput }
        ) {
            if (isShowInput) {
                Text("hide")
            } else {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "add sample"
                )
            }

        }

        if (isShowInput) {
            SampleIDInput(onAdd = onAdd)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ToggleableSampleInputPreview() {
    ToggleableSampleInput({})
}

