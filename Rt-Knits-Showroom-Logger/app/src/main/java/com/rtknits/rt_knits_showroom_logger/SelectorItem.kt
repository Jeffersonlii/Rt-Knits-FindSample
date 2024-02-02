package com.rtknits.rt_knits_showroom_logger

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rtknits.rt_knits_showroom_logger.api.SampleInfo
import com.rtknits.rt_knits_showroom_logger.components.annotateSampleId

@Composable
fun SelectorItem(
    sampleId: String,
    isScanModeOn: Boolean,
    onDelete: () -> Unit,
    isConflicting: Boolean,
    sampleInfo: SampleInfo? = null
) {

    ListItem(
        headlineContent = {
            Column() {
                Text(
                    annotateSampleId(sampleId),
                )
                if (sampleInfo?.customerName != null && sampleInfo.customerName != "") {
                    Text(
                        "${sampleInfo.customerName} - ${sampleInfo.description}",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }

            }

        },
        trailingContent = {
            if (isScanModeOn) return@ListItem

            IconButton(
                onClick = onDelete,
                modifier = Modifier.height(25.dp)
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "delete item",
                )
            }

        },
        colors = ListItemDefaults.colors(
            containerColor = if (isConflicting)
                Color.Red.copy(alpha = 0.1f)
            else Color.Transparent
        )
    )
}

@Preview(showBackground = true)
@Composable
fun SelectorItemPreview() {
//    RtKnitsShowroomLoggerTheme {
    SelectorItem(
        sampleId = "12345SG",
        isScanModeOn = false,
        onDelete = {

        },
        isConflicting = false,
        SampleInfo(
            "12312SG",
            customerName = "cus1",
            description = "TSHIRT STYLE"
        )
    )
//    }
}



