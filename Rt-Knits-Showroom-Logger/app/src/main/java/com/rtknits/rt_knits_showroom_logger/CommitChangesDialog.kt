package com.rtknits.rt_knits_showroom_logger

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CommitChangesDialog(
    onConfirmation: () -> Unit,
    onDismissRequest: () -> Unit,
    adds: MutableList<String>,
    removes: MutableList<String>,
) {
    val conflictingSamples = remember(key1 = adds, key2 = removes) {
        adds.filter { it in removes }
    }

    if (conflictingSamples.isNotEmpty()) {
        ConflictingDialog(
            onDismissRequest = onDismissRequest,
            conflictingSamples = conflictingSamples
        )
    } else {
        ConfirmationDialog(
            onConfirmation = onConfirmation,
            onDismissRequest = onDismissRequest,
            adds = adds,
            removes = removes
        )
    }
}

@Composable
fun ConflictingDialog(
    onDismissRequest: () -> Unit,
    conflictingSamples: List<String>,
) {
    AlertDialog(
        icon = {
        },
        title = {
            Text(text = "Please resolve conflicts before Committing ")
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(text = "${conflictingSamples.size} Sample(s) are being Added and Removed.\n")
                conflictingSamples.forEach {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Okay")
            }
        }
    )
}

@Composable
fun ConfirmationDialog(
    onConfirmation: () -> Unit,
    onDismissRequest: () -> Unit,
    adds: MutableList<String>,
    removes: MutableList<String>,
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
    CommitChangesDialog({}, {}, SnapshotStateList<String>(), SnapshotStateList<String>())
//    }
}


