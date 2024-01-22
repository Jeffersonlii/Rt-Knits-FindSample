package com.rtknits.rt_knits_showroom_logger.scanners

// chooses between multiple vendors of RFID scanners
// right now we only work with chainway SDK
// we use this service to detect which device is currently connected
class ScannerChooser {
    companion object {
        private val scanners =arrayOf<ScannerService>(
            ChainwayScannerServiceImpl(),
        )
        private var attachedScanner: ScannerService? = null;
        fun getAttachedScanner(): ScannerService{
            val s = attachedScanner
            if(s != null) return s

            for (scanner in scanners) {
                if(scanner.isConnected()){
                    attachedScanner = scanner
                    return scanner
                }
            }
            throw NoScannerDetected()
        }
    }
}

class NoScannerDetected() : Exception()