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
import com.rtknits.rt_knits_showroom_logger.api.APIService
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
    var openNoDeviceDialog by remember { mutableStateOf(false) }
    var openConnectingDialog by remember { mutableStateOf(false) }
    var openNoDBDialog by remember { mutableStateOf(false) }

    val scannerService by remember {
        var ss: ScannerService? = null

        try {
            ss = ScannerChooser.getAttachedScanner()
        } catch (e: NoScannerDetected) {
            openNoDeviceDialog = true
        } finally {
            openConnectingDialog = false
        }

        return@remember mutableStateOf(ss)
    }
    val api = remember { APIService(BuildConfig.FM_user, BuildConfig.FM_pass) }


    LaunchedEffect(api.ready) {
        if (!api.ready) return@LaunchedEffect

        openNoDBDialog = !api.connected
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

                openNoDBDialog -> {
                    AlertDialog(
                        onDismissRequest = { },
                        confirmButton = { },
                        title = { Text(text = "FileMaker Connection Failed") },
                        text = {
                            Column {
                                Text("Database : Marketing Store")
                                Text("Username : ${BuildConfig.FM_user}")
                                Text("Password : ${BuildConfig.FM_pass}\n")
                                Text("Check with the ERP team to diagnose\n")
                                Text("Make sure the account exists, passwords match and the user has fmrest extended privilege")
                            }
                        }
                    )
                }

                else -> {
                    // no problems? render the app!
                    scannerService?.let { SelectorParent(it) }
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

