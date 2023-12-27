package com.rtknits.rt_knits_samplefinder.scanners

import android.content.Context
import java.util.function.IntConsumer

interface ScannerService {

    fun getScannerName(): String
    fun isConnected(): Boolean
    fun startLocateRFID(context: Context, targetEPCHex: String, callback: IntConsumer)
    fun stopLocateRFID()

    fun cleanup()
}