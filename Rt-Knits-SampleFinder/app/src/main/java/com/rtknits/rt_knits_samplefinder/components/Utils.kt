package com.rtknits.rt_knits_samplefinder.components

import android.annotation.SuppressLint
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.rtknits.rt_knits_samplefinder.R
import com.rtknits.rt_knits_samplefinder.sampleIDTrailing
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
    return input.lowercase()
        .map { it.code.toString(16) }
        .joinToString("")
        .uppercase(Locale.getDefault())
}

fun Modifier.disableClickAndRipple(): Modifier =
    clickable(
        enabled = false,
        onClick = { },
    )

fun strengthToTip(strength: Int): String {
    return when {
        strength >= 50 -> "Nearby"
        strength >= 1 -> "Detected"
        else -> "Not Detected"
    }
}


fun annotateSampleId(sampleId: String): AnnotatedString {

    return if (sampleId.endsWith(sampleIDTrailing)) {
        val annotated = buildAnnotatedString {
            append(sampleId.substringBeforeLast(sampleIDTrailing))
            withStyle(
                style = SpanStyle(
                    color = Color.Gray
                )
            ) {
                append(sampleIDTrailing)
            }
        }
        annotated;
    } else {
        buildAnnotatedString {
            append(sampleId)
        };
    }
}


// this saveable remembers the list state between reroutes 
//https://stackoverflow.com/questions/68885154/using-remembersaveable-with-mutablestatelistof
@Composable
fun <T : Any> rememberSaveableMutableStateListOf(vararg elements: T): SnapshotStateList<T> {
    return rememberSaveable(
        saver = listSaver(
            save = { stateList ->
                if (stateList.isNotEmpty()) {
                    val first = stateList.first()
                    if (!canBeSaved(first)) {
                        throw IllegalStateException("${first::class} cannot be saved. By default only types which can be stored in the Bundle class can be saved.")
                    }
                }
                stateList.toList()
            },
            restore = { it.toMutableStateList() }
        )
    ) {
        elements.toList().toMutableStateList()
    }
}
