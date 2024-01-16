package com.rtknits.rt_knits_samplefinder.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rtknits.rt_knits_samplefinder.R
import com.rtknits.rt_knits_samplefinder.ui.theme.RtknitsSampleFinderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(deviceName: String) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_rtknits),
            contentDescription = "Logo",
            modifier = Modifier.size(100.dp, 50.dp),
        )
        Text(
            text = "SampleFinder",
            style = MaterialTheme.typography.labelLarge,
            letterSpacing = -1.sp,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .padding(start = 2.dp)
        )

        Box(modifier = Modifier.weight(2f))

        when{
            deviceName != "" ->{
                Text(
                    text = "Connected : ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(ContextCompat.getColor(context, R.color.rt_blue)),
                    letterSpacing = (-1).sp,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .padding(start = 2.dp)
                )
                Text(
                    text = deviceName,
                    style = MaterialTheme.typography.bodyLarge,
                    letterSpacing = (-1).sp,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .padding(start = 2.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HeaderPreview() {
    RtknitsSampleFinderTheme {
        Header(deviceName = "Chainway")
    }
}