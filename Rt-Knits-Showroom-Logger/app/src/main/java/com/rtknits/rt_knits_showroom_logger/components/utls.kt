package com.rtknits.rt_knits_showroom_logger.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

const val sampleIDTrailing = "SG"
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

fun String.decodeHex(): String {
    require(length % 2 == 0) {"Must have an even length"}
    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
        .toString(Charsets.ISO_8859_1)
        .trim('\u0000')
}