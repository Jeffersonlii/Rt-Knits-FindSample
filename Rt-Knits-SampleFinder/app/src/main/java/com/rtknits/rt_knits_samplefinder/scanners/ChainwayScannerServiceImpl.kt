package com.rtknits.rt_knits_samplefinder.scanners

import android.content.Context
import com.rscja.deviceapi.RFIDWithUHFUART
import com.rscja.deviceapi.interfaces.ConnectionStatus
import java.util.function.IntConsumer
import com.rscja.deviceapi.entity.UHFTAGInfo
import com.rscja.deviceapi.interfaces.IUHF
import com.sunmi.rfid.ReaderCall
import kotlin.concurrent.thread
import kotlin.math.max
import kotlin.math.min


enum class Modes {
    SINGLE, MULTI
}

class ChainwayScannerServiceImpl : ScannerService {
    companion object {
        private val mReader: RFIDWithUHFUART = RFIDWithUHFUART.getInstance();
        private var connected = false;

        private var activeMode: Modes? = null;
        private var inventoryingThread = Thread();
        private var inventoryListeners = mutableListOf<(String, String) -> Any>()

        init {
            connected = try {
                val res = mReader.init()
                if (!res) {
                    // problem with connecting!
                    throw UnsatisfiedLinkError()
                }
                true;
            } catch (e: UnsatisfiedLinkError) {
                // this happens when the device is not chainway!
                false;
            }
        }

    }

    override fun getScannerName(): String {
        return "Chainway"
    }

    override fun isConnected(): Boolean {
        return mReader.connectStatus == ConnectionStatus.CONNECTED;
    }


    override fun startLocateSingleRFID(
        context: Context,
        targetEPCHex: String,
        callback: IntConsumer
    ): Boolean {
        val attempt = mReader.startLocation(
            context, targetEPCHex, IUHF.Bank_EPC, 32
        ) { i, _ ->
            val strength = min(100, max(0, i))
            callback.accept(strength)
        }
        if (attempt) {
            activeMode = Modes.SINGLE;
        }
        return attempt;
    }

    override fun stopLocateSingleRFID() {
        activeMode = null;
        mReader.stopLocation()
    }

    override fun startLocateMultipleRFID(): Boolean {
        val attempt = mReader.startInventoryTag()
        if (attempt) {
            activeMode = Modes.MULTI;

            inventoryListeners.clear();
            inventoryingThread = thread(start = true, isDaemon = true) {
                while (true) {
                    try {
                        val tagInfo: UHFTAGInfo? = mReader.readTagFromBuffer();
                        if (tagInfo != null) {
                            println("${tagInfo.epc} :blop: ${tagInfo.rssi}")
                            inventoryListeners.forEach { listener ->
                                listener(tagInfo.epc, tagInfo.rssi)
                            }
                        }
                    } catch (e: InterruptedException) {
                        // The thread was interrupted, stop looping
                        break
                    }
                }
            }
        }

        return attempt;
    }

    override fun registerRFIDtoLocate(targetEPCHex: String, callback: IntConsumer) {

        when (activeMode) {
            null -> {
                throw LocateMultiRFIDexception(
                    "Trying to register a RFID without starting the locator, call startLocateMultipleRFID() first."
                )
            }

            Modes.SINGLE -> {
                throw LocateMultiRFIDexception(
                    "Scanner in Single locate mode, stop it and start the multi mode"
                )
            }

            Modes.MULTI -> {
                // register the RFID for scanning!
                inventoryListeners.add { epc: String, rssi: String ->
                    print("${epc} : : ${targetEPCHex} : : ${rssi}")
                    if (epcAreEqual(targetEPCHex, epc)) {
                        callback.accept(rssiToStrength(rssi.toDouble()))
                    }
                }
            }
        }
    }

    override fun stopLocateMultipleRFID() {
        mReader.stopInventory()
        inventoryingThread.interrupt()
        activeMode = null
    }

    override fun cleanup() {
        stopLocateSingleRFID();
        stopLocateMultipleRFID();
        mReader.free()
        connected = false;

    }
}

fun rssiToStrength(rssi: Double, minRssi: Int = -100, maxRssi: Int = -50): Int {
    return when {
        rssi < minRssi -> 0
        rssi > maxRssi -> 100
        else -> ((rssi - minRssi) * 100 / (maxRssi - minRssi)).toInt()
    }
}

// This function checks if 2 EPCs are equal, even if one of them is padded
// ex. aHex = 123AB, bHex = 123AB0000
// => true
fun epcAreEqual(aHex: String, bHexPadded: String): Boolean {
    if (bHexPadded.startsWith(aHex)) {
        val remaining = bHexPadded.substring(aHex.length)
        return remaining.all { it == '0' }
    }
    return false
}

class LocateMultiRFIDexception(message: String? = null, cause: Throwable? = null) :
    Exception(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}