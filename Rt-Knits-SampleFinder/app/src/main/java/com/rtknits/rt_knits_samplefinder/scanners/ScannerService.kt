package com.rtknits.rt_knits_samplefinder.scanners

import android.content.Context
import java.util.function.IntConsumer

interface ScannerService {

    fun getScannerName(): String
    fun isConnected(): Boolean
    fun startLocateSingleRFID(context: Context, targetEPCHex: String, callback: IntConsumer): Boolean
    fun stopLocateSingleRFID()

    fun startLocateMultipleRFID(): Boolean
    fun registerRFIDtoLocate(targetEPCHex: String, callback: IntConsumer)
    fun stopLocateMultipleRFID()

    fun cleanup()
}