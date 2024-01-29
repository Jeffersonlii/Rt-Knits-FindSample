import ctypes
import os.path
import re
import sys, os
from typing import Dict
from flask import helpers
from pathlib import Path

if getattr(sys, 'frozen', False):
    # if this app is ran as a executable (compiled by pyinstaller)
    exe_path = os.path.dirname(sys.executable)
else:
    # if this app is ran on command line (python app.py)
    exe_path = os.path.dirname(os.path.abspath(__file__))

dll_path = os.path.join(exe_path, "static", "chainwaySDK", "ZPL_SDK_x64.dll")
    
def err_msg(func: str, err: str):
    return f"Chainway Printer SDK {func} Failed : Error Code {err}. Refer to Docs for more detail"

# This service is the facade to interact with the printer
# Refer to https://www.servopack.de/support/zebra/ZPLII-Prog.pdf for ZPL commands
# This module uses the ctypes library to interop with the SDK, which is a DLL in C
class PrinterService:
    
    __printer_handle = ctypes.c_void_p()
    __dll = ctypes.cdll.LoadLibrary(dll_path)
    __isConnected = False

    CUSTOMER_SAMPLE_STICKER_TEMPLATE = None

    def __init__(self) -> None:
        zpl_template_path =  os.path.join(exe_path, "static", "StickerTemplates", "CustomerSampleSticker.prn")
        self.CUSTOMER_SAMPLE_STICKER_TEMPLATE = Path(zpl_template_path).read_text()
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

    def printSimpleLabel(self, sampleid: str, copies = 1):
        # the ZPL to send to the printer, encoded in bytes
        # notice we also change the underlying RFID EPC of the hex of the sampleid
        # this is how the rfid is linked to the sample!
        zpl = f"""
        ^XA
            ^MMC
            ^PQ{copies},{copies},0,Y
            ^RS8
            ^RFW,A,,,A^FD{sampleid.lower()}^FS
            ^FO200,200^A0N,50,50^FD {sampleid}
        ^XZ
        """

        # Call WriteData function to send the ZPL commands to the printer
        ret = self.__dll.WriteData(self.__printer_handle, zpl.encode(), len(zpl))
        if(not ret == 0):
            print(err_msg("WriteData",ret))
            return err_msg("WriteData",ret)
        return None
    def printCustomerSampleLabel(self, sampleid: str, data: Dict[str, str], copies = 1):
        interpolated_zpl = self.CUSTOMER_SAMPLE_STICKER_TEMPLATE

        # remove zpl printer settings (we will use the printer's settings)
        # this removes every command above the first ^FT (text box)
        pattern = r'^(.|\n)*?\^FT'
        interpolated_zpl = re.sub(pattern, f'^XA\n^FT', interpolated_zpl)

        # interpolate given data
        for key, value in data.items():
            pattern = rf'\^FD.*?<{key}>.*?\^FS'
            interpolated_zpl = re.sub(pattern, f'^FD{value.strip().strip("*")}^FS', interpolated_zpl)

        # replace any non interpolated placeholders with ""
        matches = re.findall(re.compile("<.*>"), interpolated_zpl)
        for match in matches:
            interpolated_zpl = interpolated_zpl.replace(match, "")

        # remove ^PQ (this zpl command controls the number of copies to print, and cutting behaviour)
        # we will inject a custom ^PQ later!
        matches = re.findall(re.compile("\^PQ.*\n"), interpolated_zpl)
        for match in matches:
            interpolated_zpl = interpolated_zpl.replace(match, "")

        # remove ^MM (this zpl command controls the print mode)
        # we will inject a custom ^MM later!
        matches = re.findall(re.compile("\^MM.*\n"), interpolated_zpl)
        for match in matches:
            interpolated_zpl = interpolated_zpl.replace(match, "")
        

        # inject custom zpl commands just before ^XZ (end of zpl)
        interpolated_zpl = rreplace(interpolated_zpl, "^XZ", 
                                f"""^MMT
                                ^PQ{copies},{copies},0,Y
                                ^RS8
                                ^RFW,A,,,A^FD{sampleid.lower()}^FS
                                ^XZ""".replace(" ",""),
                                1)

        # interpolated_zpl = rreplace(interpolated_zpl, "^XZ", 
        #                         f"""^MMT
        #                         ^PQ{copies},{copies},0,Y
        #                         ^XZ""".replace(" ",""),
        #                         1)

        print(interpolated_zpl)
        ret = self.__dll.WriteData(self.__printer_handle, interpolated_zpl.encode(), len(interpolated_zpl))
        if(not ret == 0):
            print(err_msg("WriteData",ret))
            return err_msg("WriteData",ret)
        
    def isConnected(self):
        return self.__isConnected
    


# replace occurences of old with new in s starting from the back 
# ex rreplace("hellolo,"l", "s", 1) => "helloso"
def rreplace(s, old, new, occurrence):
    li = s.rsplit(old, occurrence)
    return new.join(li)