package com.rtknits.rt_knits_samplefinder.scanners

// since this app can run on 2 different devices (Sunmi, Chainway)
// we use this service to detect which device is currently connected
class ScannerChooser {
    private val scanners =arrayOf<ScannerService>(
        ChainwayScannerServiceImpl(),
        SunmiScannerServiceImpl()
    )
    fun getAttachedScanner(): ScannerService{
        for (scanner in scanners) {
            if(scanner.isConnected()){
                return scanner
            }
        }
        throw NoScannerDetected()
    }

}

class NoScannerDetected() : Exception()