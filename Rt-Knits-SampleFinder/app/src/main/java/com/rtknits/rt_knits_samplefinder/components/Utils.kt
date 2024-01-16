package com.rtknits.rt_knits_samplefinder.components

import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.util.Locale

// this composable can be included in any composable to keep the screen from locking automatically
@Composable
fun KeepScreenOn() = AndroidView({ View(it).apply { keepScreenOn = true } })

@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler.value(owner, event)
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}

fun encodeHex(input: String): String {
    return input.map { it.code.toString(16) }.joinToString("").uppercase(
        Locale.getDefault()
    )
}

fun Modifier.disableClickAndRipple(): Modifier =
    clickable(
        enabled = false,
        onClick = { },
    )

fun strengthToTip(strength: Int): String{
    return when {
        strength >= 100 -> "Right Next to You"
        strength >= 50 -> "In Close Vicinity"
        strength >= 1 -> "Detected"
        else -> "Not Detected"
    }


}