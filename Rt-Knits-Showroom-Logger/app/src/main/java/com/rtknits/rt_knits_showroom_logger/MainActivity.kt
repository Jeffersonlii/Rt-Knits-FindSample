package com.rtknits.rt_knits_showroom_logger

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rtknits.rt_knits_showroom_logger.scanners.NoScannerDetected
import com.rtknits.rt_knits_showroom_logger.scanners.ScannerChooser
import com.rtknits.rt_knits_showroom_logger.components.Header
import com.rtknits.rt_knits_showroom_logger.scanners.ScannerService
import com.rtknits.rt_knits_showroom_logger.ui.theme.RtKnitsShowroomLoggerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RtKnitsShowroomLoggerTheme {
                Base()
            }
        }
    }
}

@Composable
fun Base() {

    Log.d("jeff", BuildConfig.FM_user)
    Log.d("jeff", BuildConfig.FM_pass)

    var scannerService by remember { mutableStateOf<ScannerService?>(null) }
    var openNoDeviceDialog by remember { mutableStateOf(false) }
    var openConnectingDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            scannerService = ScannerChooser.getAttachedScanner()
        } catch (e: NoScannerDetected) {
            openNoDeviceDialog = true
        } finally {
            openConnectingDialog = false
        }
    }

    Scaffold(
        topBar = {
            Column {
                Box(modifier = Modifier.padding(16.dp, 8.dp)) {
                    Header(scannerService?.getScannerName() ?: "")
                }
                HorizontalDivider()
            }
        },
        bottomBar = {
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
        ) {

            SelectorParent()

            when {
                openNoDeviceDialog -> {
                    AlertDialog(
                        onDismissRequest = { },
                        confirmButton = { },
                        title = { Text(text = "No UHF Device Detected") },
                        text = {
                            Text(
                                text = "Make sure this app is running on a Chainway UHF device.\n" +
                                        "Make sure electronics are turned on and charged.\n" +
                                        "Make sure no other apps are using the scanner."
                            )
                        }
                    )
                }

                openConnectingDialog -> {
                    AlertDialog(
                        onDismissRequest = { },
                        confirmButton = { },
                        title = { Text(text = "Connecting...") },
                        text = {
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    RtKnitsShowroomLoggerTheme {
        Base()
    }
}

