package com.rtknits.rt_knits_showroom_logger.scanners



import kotlinx.coroutines.DelicateCoroutinesApi

class MockScannerServiceImpl : ScannerService {
    companion object {


    }

    override fun getScannerName(): String {
        return "Mock"
    }

    override fun isConnected(): Boolean {
        return true
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun startInventorying(onFind: (epc:String)->Unit): Boolean {
        return true
    }

    override fun stopInventorying() {
    }

    override fun disconnect() {
    }
}

