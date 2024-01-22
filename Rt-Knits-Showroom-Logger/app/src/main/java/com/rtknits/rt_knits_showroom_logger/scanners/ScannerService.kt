package com.rtknits.rt_knits_showroom_logger.scanners

interface ScannerService {

//    fun connect()

    fun getScannerName(): String
    fun isConnected(): Boolean

    fun startInventorying(onFind: (epc:String)->Unit): Boolean
    fun stopInventorying()

    fun disconnect()
}