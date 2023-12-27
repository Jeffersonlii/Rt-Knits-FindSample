package com.rtknits.rt_knits_samplefinder.scanners

import android.content.Context
import com.rscja.deviceapi.RFIDWithUHFUART
import com.rscja.deviceapi.interfaces.ConnectionStatus
import com.rscja.deviceapi.interfaces.IUHF
import java.util.function.IntConsumer
import kotlin.math.max
import kotlin.math.min


class ChainwayScannerServiceImpl : ScannerService {
    companion object {
        private val mReader: RFIDWithUHFUART = RFIDWithUHFUART.getInstance();
        private var connected = false;
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

    override fun startLocateRFID(context: Context, targetEPCHex: String, callback: IntConsumer) {
        if (isConnected()) {
            mReader.startLocation(
                context, targetEPCHex, IUHF.Bank_EPC, 32
            ) { i, _ ->
                val strength = min(100, max(0, i))
                println(strength)

                callback.accept(strength)
            }
        }

    }

    override fun stopLocateRFID() {
        if (isConnected()) {
            mReader.stopLocation()
        }
    }

    override fun cleanup() {
        if (isConnected()) {
            mReader.stopLocation()
//            mReader.free()
//            connected = false;
        }
    }
}