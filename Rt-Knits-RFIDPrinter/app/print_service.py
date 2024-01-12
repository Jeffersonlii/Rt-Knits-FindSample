import ctypes
import os.path
import sys, os

if getattr(sys, 'frozen', False):
    # if this app is ran as a executable (compiled by pyinstaller)
    exe_path = os.path.dirname(sys.executable)
else:
    # if this app is ran on command line (python app.py)
    exe_path = os.path.dirname(os.path.abspath(__file__))

dll_path = os.path.join(exe_path, "static", "chainwaySDK", "ZPL_SDK_x64.dll")

def ascii_to_hex(text):
    return text.encode('utf-8').hex()
    
def err_msg(func: str, err: str):
    return f"Chainway Printer SDK {func} Failed : Error Code {err}. Refer to Docs for more detail"

# This service is the facade to interact with the printer
# Refer to https://www.servopack.de/support/zebra/ZPLII-Prog.pdf for ZPL commands
# This module uses the ctypes library to interop with the SDK, which is a DLL in C
class PrinterService():
    
    __printer_handle = ctypes.c_void_p()
    __dll = ctypes.cdll.LoadLibrary(dll_path)
    __isConnected = False
  
    def connect(self):
        # Call PrinterCreator function to set up the target printer model
        ret = self.__dll.PrinterCreator(ctypes.byref(self.__printer_handle), b'HM-T300 PRO')
        if(not ret == 0):
            print(err_msg("PrinterCreator",ret))
            return err_msg("PrinterCreator",ret)

        # Call PortOpen function to open the communication port and connect with the printer
        # Use USB connection
        ret = self.__dll.PortOpen(self.__printer_handle, b'USB')
        if(not ret == 0):
            print(err_msg("PortOpen",ret))
            return err_msg("PortOpen",ret)
        

        self.__isConnected = True
        
    def __del__(self):
        # Call PortClose function to close the communication port and disconnect with the printer[^3^][3][^2^][2]
        self.__dll.PortClose(self.__printer_handle)

        # Call PrinterDestroy function to release the resource of the printer object
        self.__dll.PrinterDestroy(self.__printer_handle)

    def printLabel(self, sampleid: str, copies = 1):
        if(not self.isConnected()):
            return "Printer not Connected"
        # the ZPL to send to the printer, encoded in bytes
        # notice we also change the underlying RFID EPC of the hex of the sampleid
        # this is how the rfid is linked to the sample!
        zpl = f"""
        ^XA
            ^MMC
            ^PQ{copies},{copies},0,Y
            ^RS8
            ^RFW,H,,,A^FD{ascii_to_hex(sampleid.lower())}^FS
            ^FO200,200^A0N,50,50^FD {sampleid}
        ^XZ
        """
        # Call WriteData function to send the ZPL commands to the printer
        ret = self.__dll.WriteData(self.__printer_handle, zpl.encode(), len(zpl))
        if(not ret == 0):
            print(err_msg("WriteData",ret))
            return err_msg("WriteData",ret)
        return None
    
    def isConnected(self):
        return self.__isConnected
    