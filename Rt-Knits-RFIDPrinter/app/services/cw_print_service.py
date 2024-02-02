import ctypes
import os.path
import os
from util import getAppPath

app_path = getAppPath()
dll_path = os.path.join(app_path, "static", "chainwaySDK", "ZPL_SDK_x64.dll")
    
def err_msg(func: str, err: str):
    return f"Chainway Printer SDK {func} Failed : Error Code {err}. Refer to Docs for more detail"

# This service is the facade to interact with the CW printer
# This module uses the ctypes library to interop with the SDK, which is a DLL in C
# you may find the documentation for the DLL under static/chainwaySDK/ZPL Windows SDK Manual.pdf
class CWPrinterService:
    
    __printer_handle = ctypes.c_void_p()
    __dll = ctypes.cdll.LoadLibrary(dll_path)
    __isConnected = False

    def connect(self):
        # Call PrinterCreator function to set up the target printer model
        ret = self.__dll.PrinterCreator(ctypes.byref(self.__printer_handle), b'HM-T300 PRO')
        if(not ret == 0):
            return err_msg("PrinterCreator",ret)

        # Call PortOpen function to open the communication port and connect with the printer
        # Use USB connection
        ret = self.__dll.PortOpen(self.__printer_handle, b'USB')
        if(not ret == 0):
            return err_msg("PortOpen",ret)
        
        self.__isConnected = True
        
    def __del__(self):
        # Call PortClose function to close the communication port and disconnect with the printer
        self.__dll.PortClose(self.__printer_handle)

        # Call PrinterDestroy function to release the resource of the printer object
        self.__dll.PrinterDestroy(self.__printer_handle)

    def writeZPL(self, zpl: str):
        ret = self.__dll.WriteData(self.__printer_handle, zpl.encode(), len(zpl))
        if(not ret == 0):
            return err_msg("WriteData",ret)
        
    def isConnected(self):
        return self.__isConnected
    


