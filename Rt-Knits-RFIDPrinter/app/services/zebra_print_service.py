import requests

def err_msg(func: str):
    return f"Zebra SDK {func} Failed, make sure Zebra Browser Printer service is running! "

# This service is the facade to interact with the zebra printer
# This module uses the Zebra Browser Print Service (https://www.zebra.com/us/en/support-downloads/printer-software/by-request-software.html#browser-print)
# this service exposes an endpoint on "http://127.0.0.1:9100", and is meant for the Zebra Browser Print JavaScript Library to interact with
# I've stepped through the JS lib using a debugger to find the core apis to talk to the printer, and implimented it in python to directly talk to the printer
# Therefor the API has no documentation.
class ZebraPrinterService:
    __printerInfo = None
    __printerURL = "http://127.0.0.1:9100"

    def connect(self):
        if(self.__printerInfo is not None): return
        try:
            printerInfoResp = requests.get(url = f"{self.__printerURL}/default?type=printer")
        except:
            return err_msg("connect")
        if(printerInfoResp.status_code == 200):
            self.__printerInfo = printerInfoResp.json()
            print(self.__printerInfo)
        else:
            return err_msg("connect")
    def writeZPL(self, zpl:str):
        try:
            writeResp = requests.post(url = f"{self.__printerURL}/write",
                      json = {
                                'device': self.__printerInfo,
                                'data': zpl,
                                })
        except:
            return err_msg("WriteZPL")
        if(writeResp.status_code != 200):
            return err_msg("WriteZPL")
    def isConnected(self):
        return self.__printerInfo is not None