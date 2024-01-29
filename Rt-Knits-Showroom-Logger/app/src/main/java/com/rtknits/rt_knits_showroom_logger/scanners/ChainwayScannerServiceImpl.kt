package com.rtknits.rt_knits_showroom_logger.scanners

import com.rscja.deviceapi.RFIDWithUHFUART
import com.rscja.deviceapi.entity.UHFTAGInfo
import com.rscja.deviceapi.interfaces.ConnectionStatus
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChainwayScannerServiceImpl : ScannerService {
    companion object {
        private val mReader: RFIDWithUHFUART = RFIDWithUHFUART.getInstance();
        private var connected = false;

//        private var inventoryingThread = Thread();

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
        return "CW"
    }

    override fun isConnected(): Boolean {
        return mReader.connectStatus == ConnectionStatus.CONNECTED;
    }

    private var job: Job? = null

    @OptIn(DelicateCoroutinesApi::class)
    override fun startInventorying(onFind: (epc:String)->Unit): Boolean {
        val attempt = mReader.startInventoryTag()
        if (attempt) {
            // start job on the IO dispatcher
            job = GlobalScope.launch(Dispatchers.IO) {
                while (isActive) {
                    val tagInfo: UHFTAGInfo? = mReader.readTagFromBuffer()
                    if (tagInfo != null) {

                        // when RFID is found, switch to the main thread to perform UI updates
                        withContext(Dispatchers.Main) {
                            onFind(tagInfo.epc)
                        }
                    }
                }
            }
        }
        return attempt
    }

    override fun stopInventorying() {
        job?.cancel()
        mReader.stopInventory()
    }

    override fun disconnect() {
        stopInventorying();
        mReader.free()
        connected = false;
    }
}

