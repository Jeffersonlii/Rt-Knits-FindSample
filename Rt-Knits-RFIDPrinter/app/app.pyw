import subprocess
from flask import Flask, jsonify, render_template, request
from print_service import PrinterService
import sys, os

ps = PrinterService()
ps_err = ps.initialize()

if getattr(sys, 'frozen', False):
    # if this app is ran as a executable (compiled by pyinstaller)
    exe_path = os.path.dirname(sys.executable)

    # manually link the templates and statics
    template_folder = os.path.join(exe_path, 'templates')
    static_folder = os.path.join(exe_path, 'static')
    app = Flask(__name__, template_folder=template_folder, static_folder=static_folder)
else:
    app = Flask(__name__)


@app.route('/')
def home():
    return render_template('index.html')
@app.route('/zebra')
def zebra():
    return render_template('zebra_index.html')

# this endpoint tries to print to the chainway RFID printer. 
# meant to be used in a FileMaker App
# FileMaker can then use the "Insert from URL" function to call this endpoint
# and directly print without using our UI
# ex. 
#   Set Variable [ $url ; Value: "http://127.0.0.1:8003/RFIDprint/mySampleId?copies=2" ]
#   Insert from URL [ Select ; With dialog: Off ; Target:$_ ; $result ; $url ; cURL options: "-X POST" ]
@app.route('/RFIDprint/<sampleid>', methods=['POST'])
def print_label(sampleid: str):
    copies = request.args.get('copies', default = 1, type = int)

    err = ps.printLabel(sampleid, copies=copies)
    
    respObj = {
        "chainway_status" : "fail" if ps_err or err else "success",
        "chainway_error_code" : f'{ps_err} \n {err}'
    }
    return jsonify(respObj)

if __name__ == '__main__':
    if getattr(sys, 'frozen', False):
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
    app.run(port=8003, debug=False)

 