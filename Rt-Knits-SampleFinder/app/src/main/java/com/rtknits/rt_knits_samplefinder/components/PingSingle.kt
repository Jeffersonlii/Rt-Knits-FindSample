package com.rtknits.rt_knits_samplefinder.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rtknits.rt_knits_samplefinder.NavControllerContext
import com.rtknits.rt_knits_samplefinder.Screen
import com.rtknits.rt_knits_samplefinder.scanners.ScannerChooser
import com.rtknits.rt_knits_samplefinder.scanners.ScannerService
import com.rtknits.rt_knits_samplefinder.ui.theme.RtknitsSampleFinderTheme
import kotlin.concurrent.thread
import kotlin.math.max

@Composable
fun PingSingle(sampleID: String, strength: Int) {
    val nc = NavControllerContext.current

    // lifecycle events for stopping / pausing the scan

    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
                .padding(start = 8.dp)) {
            Text(
                text = sampleID,
                style = MaterialTheme.typography.titleLarge
            )

            Box(modifier = Modifier.weight(1f))

                Text(
                    text = strengthToTip(strength),
                    style = MaterialTheme.typography.bodyLarge
                )
            if(strength > 0) {
                IconButton(onClick = {
                    nc?.navigate(
                        Screen.ScanSingle.route.replace(
                            "{sampleID}", sampleID
                        )
                    )
                }) {
                    Icon(Icons.Outlined.PlayArrow, contentDescription = "Focus")
                }
            }else{
                IconButton(onClick = {}, enabled = false) {}
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun PingSinglePreview() {
    RtknitsSampleFinderTheme {
        PingSingle("23489SG", 12)
    }
}
