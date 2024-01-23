package com.rtknits.rt_knits_samplefinder.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import com.rtknits.rt_knits_samplefinder.R

const val sampleIDTrailing = "SG"
@Composable
fun SampleIdInput(onAdd: (String) -> Unit, modifier: Modifier = Modifier){
    var doTrailing by remember { mutableStateOf(true) }
    var inputSampleId by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    val onAddSample = remember {
        {
            val sid = if (doTrailing)
                "$inputSampleId$sampleIDTrailing"
            else
                inputSampleId

            onAdd(sid.uppercase())
            inputSampleId = ""
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .requiredHeight(60.dp),
    ) {
        Box(Modifier.weight(1f)) {
            Switch(
                checked = doTrailing,
                onCheckedChange = {
                    inputSampleId = ""
                    doTrailing = it

                    if (doTrailing) {
                        Toast.makeText(
                            context,
                            "SG will be appended to your number",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(context, "Raw Text Mode", Toast.LENGTH_SHORT).show()

                    }
                },
                thumbContent = if (doTrailing) {
                    {
                        Text(
                            sampleIDTrailing,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                } else {
                    null
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = ((-10).dp), y = 4.dp)
                    .zIndex(1f)
            )


            OutlinedTextField(
                value = inputSampleId,
                onValueChange = { inputSampleId = it },
                singleLine = true,
                label = { Text("SampleID") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "search icon") },
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (inputSampleId != "") {
                            onAddSample()
                        }
                    }),
                keyboardOptions = KeyboardOptions
                    .Default
                    .copy(
                        keyboardType =
                        if (doTrailing)
                            KeyboardType.Number
                        else
                            KeyboardType.Password,

                        ),
            )
        }
        if (inputSampleId != "") {
            Button(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(size = 4.dp),
                colors = ButtonDefaults
                    .buttonColors(
                        containerColor =
                        Color(ContextCompat.getColor(context, R.color.rt_blue))
                    ),
                onClick = onAddSample
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Add Sampleid")
            }
        }
    }
}