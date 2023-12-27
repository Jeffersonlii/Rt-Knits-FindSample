package com.rtknits.rt_knits_samplefinder.scanners

import android.content.Context
import com.sunmi.rfid.RFIDManager
import com.sunmi.rfid.ReaderCall
import com.sunmi.rfid.constant.ParamCts
import com.sunmi.rfid.constant.ParamCts.BATTERY_REMAINING_PERCENT
import com.sunmi.rfid.constant.ParamCts.TAG_RSSI
import com.sunmi.rfid.entity.DataParameter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import java.util.function.IntConsumer
import kotlin.concurrent.thread
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

val PERCENT_ITEM = 100f / (94 - 31)

class SunmiScannerServiceImpl() : ScannerService {

    protected var call: ReaderCall? = null
    private var scannerResponded = false;
    private var rfidManager: RFIDManager = RFIDManager.getInstance()
    private var registrationThread: Thread = Thread()
    override fun getScannerName(): String {
        return "Sunmi"
    }

    override fun isConnected(): Boolean {
        return rfidManager.isConnect()
    }
    override fun startLocateRFID(context: Context, targetEPCHex: String, callback: IntConsumer) {

        if (rfidManager.isConnect()) {
            GlobalScope.launch {
                delay(500) // non-blocking delay for 500 ms
                if(!scannerResponded){
                    println("no resp")
                }
            }

            call = object : ReaderCall() {
                override fun onSuccess(cmd: Byte, params: DataParameter?) {}
                override fun onFailed(cmd: Byte, errorCode: Byte, msg: String?) {}

                override fun onTag(cmd: Byte, state: Byte, tag: DataParameter?) {
                    scannerResponded = true;
                    if (tag == null) {
                        return;
                    };
                    var epc = tag.getString(ParamCts.TAG_EPC);
                    epc = (epc?.replace(
                        " ",
                        ""
                    )?.uppercase(
                        Locale.getDefault()
                    )
                        ?: return)
                    if (epcAreEqual(targetEPCHex, epc)) {
                        // found the target rfid!

                        // calculate the signal to the rfid
                        val rssi = (Integer.parseInt(tag.getString(TAG_RSSI, "129")) - 129)
                        val strength = min(100, max(0, rssiToPerc(rssi)))
                        callback.accept(strength)
                    }

                }
            }

            RFIDManager.getInstance().getHelper()?.apply {

                // this thread continuously registers the readercall, so that the scanner will keep reading
                // without continuous registration, the scanner scans for a few seconds then stops.
                registrationThread = thread(start = true, isDaemon = true) {
                    while (true) {
                        try {
                            Thread.sleep(1000)
                            this.registerReaderCall(call as ReaderCall)
                            this.realTimeInventory(0)
                        } catch (e: InterruptedException) {
                            // The thread was interrupted, stop looping
                            break
                        }
                    }
                }
            }
        }
    }

    override fun stopLocateRFID() {

    }

    override fun cleanup() {
        RFIDManager.getInstance().getHelper()?.apply {
            registrationThread.interrupt()
            this.inventory(1)
            this.unregisterReaderCall()
        }
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

// converts RSSI to Percentage from 0 to 100
fun rssiToPerc(rssi: Int): Int {
    val perc = 100 - (((rssi * -1) - 31) * PERCENT_ITEM)
    return perc.toInt()
}