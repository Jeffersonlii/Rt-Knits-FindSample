package com.rtknits.rt_knits_showroom_logger

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ConfirmationDialog(
    onConfirmation: ()->Unit,
    onDismissRequest: ()->Unit,
    adds : SnapshotStateList<String>,
    removes : SnapshotStateList<String>,
    ) {
    AlertDialog(
    icon = {
    },
    title = {
        Text(text = "Are you sure you want to commit this revision?")
    },
    text = {
        Column {
            Text(text = "Additions : ${adds.size}")
            Text(text = "Removals : ${removes.size} \n")
            Text(text = "This Action Cannot be Reversed")
        }
    },
    onDismissRequest = {
        onDismissRequest()
    },
    confirmButton = {
        TextButton(
            onClick = {
                onConfirmation()
            }
        ) {
            Text("Confirm")
        }
    },
    dismissButton = {
        TextButton(
            onClick = {
                onDismissRequest()
            }
        ) {
            Text("Cancel")
        }
    }
    )

}
@Preview(showBackground = true)
@Composable
fun ConfirmationDialogPreview() {
//    RtKnitsShowroomLoggerTheme {
    ConfirmationDialog({},{},SnapshotStateList<String>(),SnapshotStateList<String>())
//    }
}


