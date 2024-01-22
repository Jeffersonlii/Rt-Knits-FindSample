package com.rtknits.rt_knits_samplefinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rtknits.rt_knits_samplefinder.components.Header
import com.rtknits.rt_knits_samplefinder.components.PermanentAlertDialog
import com.rtknits.rt_knits_samplefinder.scanners.NoScannerDetected
import com.rtknits.rt_knits_samplefinder.scanners.ScannerChooser
import com.rtknits.rt_knits_samplefinder.scanners.ScannerService
import com.rtknits.rt_knits_samplefinder.ui.theme.RtknitsSampleFinderTheme
import com.sunmi.rfid.RFIDManager
import kotlinx.coroutines.delay
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RFIDManager.getInstance().connect(this);

        setContent {
            RtknitsSampleFinderTheme {
                Base()
            }
        }
    }
}

@Composable
fun Base() {
    val navController = rememberNavController()
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
        // Apply the padding globally to the whole BottomNavScreensController
        Box(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            NavGraph(navController)

            when {
                openNoDeviceDialog -> {
                    PermanentAlertDialog(
                        onDismissRequest = { },
                        onConfirmation = { },
                        dialogTitle = "No UHF Device Detected",
                        dialogText = "Make sure this app is running on a Chainway or Sunmi UHF device.\n" +
                                "Make sure electronics are turned on and charged.\n" + "Make sure no other apps are using the scanner.",
                    )
                }

                openConnectingDialog -> {
                    PermanentAlertDialog(
                        onDismissRequest = { },
                        onConfirmation = { },
                        dialogTitle = "Connecting...",
                        dialogText = "",
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BasePreview() {
    RtknitsSampleFinderTheme {
        Base()
    }
}