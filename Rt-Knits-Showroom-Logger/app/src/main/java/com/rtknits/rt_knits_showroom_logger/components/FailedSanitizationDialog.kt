package com.rtknits.rt_knits_showroom_logger.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FailedSanitizationDialog(
    onDismissRequest: () -> Unit,
    conflictingSamples: List<String>? = null,
    nonExistentSamples: List<String>? = null,
    invalidOperationSamples: List<String>? = null,
) {
    AlertDialog(
        icon = {
        },
        title = {
            Text(text = "Please resolve errors before Committing ")
        },
        text = {
            Column (verticalArrangement = Arrangement.spacedBy(30.dp)){
                if(conflictingSamples != null && conflictingSamples.isNotEmpty()){
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text(
                            text = "${conflictingSamples.size} Sample(s) are being Added and Removed.",
                        )
                        conflictingSamples.forEach {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
                if(nonExistentSamples != null && nonExistentSamples.isNotEmpty()){
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text(text = "${nonExistentSamples.size} Sample(s) do not exist in FileMaker")
                        nonExistentSamples.forEach {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
                if(invalidOperationSamples != null && invalidOperationSamples.isNotEmpty()){
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text(text = "${invalidOperationSamples.size} Sample(s) have conflicting operations (ex. Added when sample is already in showroom).")
                        invalidOperationSamples.forEach {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
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
fun FailedSanitizationDialogPreview() {
//    RtKnitsShowroomLoggerTheme {
    FailedSanitizationDialog({}, listOf("asdf","234"), listOf("asdf","234"), listOf("asdf","234"))
//    }
}


