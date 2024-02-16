import re
import sys, os
from typing import Dict
from util import getAppPath, rreplace
from pathlib import Path

app_path = getAppPath()
zpl_template_path =  os.path.join(app_path, "static", "StickerTemplates", "CustomerSampleSticker.prn")
CUSTOMER_SAMPLE_STICKER_TEMPLATE = Path(zpl_template_path).read_text()

# This function modifies the ZPL template to include user variables. 
# By convention, the field <SampleID> template may be updated to value "12345SG" by including the following pair in the DATA Dict
#     [...,  "SampleID": "12345SG" ...]
def generateCustomerZPL(sampleid: str, data: Dict[str, str], copies = 1):
    interpolated_zpl = CUSTOMER_SAMPLE_STICKER_TEMPLATE

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
                            f"""^PQ{copies},{copies},0,Y
                            ^RS8
                            ^RFW,A,,,A^FD{sampleid.lower()}^FS
                            ^XZ""".replace(" ",""),
                            1)

    # interpolated_zpl = rreplace(interpolated_zpl, "^XZ", 
    #                         f"""^PQ{copies},{copies},0,Y
    #                         ^XZ""".replace(" ",""),
    #                         1)

    return interpolated_zpl

# Returns a simple RFID label, with epcData encoded in label
def generateSimpleZPL(epcData: str, header: str, copies = 1):
    return f"""
     ^XA
            ^PQ{copies},{copies},0,Y
            ^RS8
            ^RFW,A,,,A^FD{epcData.lower()}^FS
            ^FT79,150^A0N,50,51^FH\^CI28^FD{header} :^FS^CI27
            ^FT0,380^A0N,58,58^FB703,1,15,C^FH\^CI28^FD{epcData}^FS^CI27
            ^FO74,322^GB555,83,4^FS
            ^FT419,310^BQN,2,10
            ^FH\^FDLA,{epcData}^FS
        ^XZ
    """
