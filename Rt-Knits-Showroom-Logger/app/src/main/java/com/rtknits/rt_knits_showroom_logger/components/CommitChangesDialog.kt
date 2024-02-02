package com.rtknits.rt_knits_showroom_logger.components

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rtknits.rt_knits_showroom_logger.APIProvider
import com.rtknits.rt_knits_showroom_logger.api.Operation
import kotlinx.coroutines.runBlocking

@Composable
fun CommitChangesDialog(
    onConfirmation: () -> Unit,
    onDismissRequest: () -> Unit,
    adds: MutableList<String>,
    removes: MutableList<String>,
) {

    val context = LocalContext.current
    val api = APIProvider.current
    var confirmLoading by remember { mutableStateOf(false) }
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
                    confirmLoading = true

                    runBlocking {
                        val failedSamples = ArrayList<String>()

                        adds.forEach {
                            val resp = api.createShowRoomLog(it, Operation.ADDITION)
                            if (!resp.success) {
                                failedSamples.add(it)
                            }
                        }
                        removes.forEach {
                            val resp = api.createShowRoomLog(it, Operation.REMOVAL)
                            if (!resp.success) {
                                failedSamples.add(it)
                            }
                        }
                        Toast.makeText(
                            context,
                            if (failedSamples.size > 0) "${failedSamples.size} Sample(s) failed. ${
                                failedSamples.joinToString(
                                    separator = ", "
                                )
                            }" else "Operation Successful!",
                            Toast.LENGTH_LONG
                        ).show()

                        confirmLoading = false
                        onConfirmation()
                    }
                }
            ) {
                if (confirmLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(32.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                } else {
                    Text("Confirm")

                }
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

@Preview(showBackground = true)
@Composable
fun ConfirmationDialogPreview() {
//    RtKnitsShowroomLoggerTheme {
    CommitChangesDialog({}, {}, SnapshotStateList<String>(), SnapshotStateList<String>())
//    }
}


