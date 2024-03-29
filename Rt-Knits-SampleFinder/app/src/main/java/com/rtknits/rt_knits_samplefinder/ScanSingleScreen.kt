package com.rtknits.rt_knits_samplefinder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.rtknits.rt_knits_samplefinder.components.KeepScreenOn
import com.rtknits.rt_knits_samplefinder.components.LocateSingle
import com.rtknits.rt_knits_samplefinder.components.annotateSampleId
import com.rtknits.rt_knits_samplefinder.components.disableClickAndRipple
import com.rtknits.rt_knits_samplefinder.scanners.ScannerChooser
import com.rtknits.rt_knits_samplefinder.ui.theme.RtknitsSampleFinderTheme

@Composable
fun ScanSingleScreen(sampleID: String) {
    KeepScreenOn()
    Box(modifier = Modifier
        .fillMaxHeight()
        .padding(16.dp)) {
        LocateSingle(sampleID)
        OutlinedButton(
            onClick = {}, enabled = true, modifier = Modifier
                .padding(12.dp, 8.dp)
                .fillMaxWidth()
                .disableClickAndRipple()
                .align(Alignment.BottomCenter)
        ) {
            Text(
                "Sample ID : ",
                color = Color.Black,
            )
            Text(
                annotateSampleId(sampleID),
                color = Color.Black,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScanSinglePreview() {
    RtknitsSampleFinderTheme {
        ScanSingleScreen("s1")
    }
}



