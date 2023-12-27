package com.rtknits.rt_knits_samplefinder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rtknits.rt_knits_samplefinder.ui.theme.RtknitsSampleFinderTheme
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(nc: NavHostController) {
    var sampleid by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current


    // auto open keyboard!
    LaunchedEffect(Unit) {
        delay(200)
        focusRequester.requestFocus()
    }

    Scaffold(bottomBar = {

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = sampleid,
                onValueChange = { sampleid = it },
                singleLine = true,
                label = { Text("Enter SampleID here...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "search icon") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (!sampleid.equals("")) Icon(
                        Icons.Filled.Close,
                        contentDescription = "close icon",
                        modifier = Modifier.clickable { sampleid = "" }
                    )
                }

            )

            FilledTonalButton(
                enabled = !sampleid.equals(""),
                modifier = Modifier.fillMaxWidth(),
                onClick = { nc.navigate(Screen.Scan.route.replace("{sampleID}", sampleid)) }) {
                Text(text = "Scan For Sample")
            }
        }
        OutlinedTextField(
            value = sampleid,
            onValueChange = { sampleid = it },
            singleLine = true,
            label = { Text("Enter SampleID here...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "search icon") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            trailingIcon = {
                if (!sampleid.equals("")) Icon(
                    Icons.Filled.Close,
                    contentDescription = "close icon",
                    modifier = Modifier.clickable {
                        sampleid = ""
                        focusManager.clearFocus()
                    }
                )
            }

        )

    }) { padding ->
        Paragraph(
            modifier = Modifier
                .padding(padding)
                .padding(bottom = 16.dp)
                .fillMaxWidth()
                .clickable { focusManager.clearFocus() }
        )
    }
}

@Composable
fun Paragraph(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, Color.Black),
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "SampleFinder assists you in locating samples within your office using by scanning the RFID tags.",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = """
        Instructions:
            1. Input the SampleID of the sample
            2. Scan your surroundings and locate the item via the signal strength
            """,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Please note, SampleFinder relies on RFID tags attached to the sample. Ensure your item was tagged with RFID at the time of creation.",
                style = MaterialTheme.typography.bodySmall
            )

        }


    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    RtknitsSampleFinderTheme {
        HomeScreen(nc = rememberNavController())
    }
}

