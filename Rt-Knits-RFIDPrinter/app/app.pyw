# ----------------------------------------------------------------------
# Author : Jefferson Li
# Contact Email : jeffersonli2013@gmail.com
# Date : 2024-01-29
# Git Repo : https://github.com/Jeffersonlii/Rt-Knits-FindSample
# ----------------------------------------------------------------------

import subprocess
from flask import Flask, jsonify, render_template, request
from services.cw_print_service import CWPrinterService
from services.sticker_service import generateZPL
from services.zebra_print_service import ZebraPrinterService
from util import getAppPath, isRunningAsExecutable
import sys, os


isRunningAsExecutable = isRunningAsExecutable()
app_path = getAppPath()

# if this app is ran as a executable (compiled by pyinstaller)
if isRunningAsExecutable:
    # manually link the templates and statics
    static_folder = os.path.join(app_path, 'static')
    templates_folder = os.path.join(app_path, 'templates')
    app = Flask(__name__, static_folder=static_folder, template_folder=templates_folder)
else:
    app = Flask(__name__)

cw_ps = CWPrinterService()
z_ps = ZebraPrinterService()

# ------------ WEB ENDPOINTS -----------------
@app.route('/')
def home():
    return render_template('index.html')
# ------------ Printer ENDPOINTS -----------------

# Prints a Customer Sample Sticker. 
# The template of the sticker is found at `static/StickerTemplates/CustomerSampleSticker.prn`
# This template contains raw ZPL code and can be generated by using CustomerSampleStickerDesign.nlbl (opened by ZebraDesigner)
# The Template file follows the following convention for variables
#   the field <SampleID> in the nlbl file may be updated to value "12345SG" by including the following pair in the json body to this request
#   {... , "SampleID" : "12345SG"}
@app.route('/customerStickerPrint/<sampleid>', methods=['POST'])
def customer_sticker_print(sampleid: str):
    copies = request.args.get('copies', default = 1, type = int)
    customer_sticker_data = request.get_json()
    zpl = generateZPL(sampleid, customer_sticker_data, copies=copies)

    # validate input
    try:
        if(not int(copies) > 0):
            copies = 1
    except:
        copies = 1

    # try to connect to both printers
    cw_connect_err = cw_ps.connect()
    z_connect_err = z_ps.connect()

    cw_write_err, z_write_err  = None, None
    print(cw_connect_err)
    if(cw_ps.isConnected()):
        # try chainway printer
        cw_write_err = cw_ps.writeZPL(zpl)
        pass
    if(z_ps.isConnected()):
        # try zebra printer
        z_write_err = z_ps.writeZPL(zpl)
        pass
    # print is successful if a connection was made and a print succeeded
    is_print_successful = ((not cw_connect_err) or (not z_connect_err)) and  ((not cw_write_err) or (not z_write_err))

    resp = {
        "print_attempt_status": is_print_successful,
        "errors" : [] if is_print_successful else [x for x in [cw_connect_err,z_connect_err,cw_write_err,z_write_err] if x is not None]
    }
    print(resp)
    return jsonify(resp)

# ------------ main -----------------
if __name__ == '__main__':
    if isRunningAsExecutable:
        cms = ' && '.join([
            "echo RFID Printer Service Started, check http://127.0.0.1:8003 to confirm",
            "echo Docs : https://github.com/Jeffersonlii/Rt-Knits-FindSample/tree/main/Rt-Knits-RFIDPrinter",
            "echo You may stop/kill this service by running kill_printer_service.bat"
        ])
        subprocess.Popen(f'cmd /k "{cms}"', creationflags=subprocess.CREATE_NEW_CONSOLE)
    else:
        welcome = """
                RFID Printer Service
            ----------------------------------
            This service exposes an endpoint for the Filemaker App 'Printer Sample Sticker' under 'Sample Room SAM'.
            It runs on localhost:8003 and should run on startup.

            Docs : https://github.com/Jeffersonlii/Rt-Knits-FindSample/tree/main/Rt-Knits-RFIDPrinter
            ----------------------------------
        """
        print(welcome)
    app.run(port=8003, debug=not isRunningAsExecutable)

 