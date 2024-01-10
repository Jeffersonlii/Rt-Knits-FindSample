from flask import Flask, render_template, request
from endpoints.chainway import chainwayApp
from endpoints.zebra import zebraApp
from print_service import PrinterService
ps = PrinterService()
ps_err = ps.initialize()

app = Flask(__name__)
app.register_blueprint(chainwayApp, url_prefix='/chainway')
app.register_blueprint(zebraApp, url_prefix='/zebra')

@app.route('/')
def home():
    return render_template('choose.html')

# this endpoint tries to print to any RFID printer it can find, 
# meant to be used in a FileMaker App
# FileMaker can then use the "Insert from URL" function to call this endpoint
# and directly print without using our UI
# ex. 
#   Set Variable [ $url ; Value: "http://127.0.0.1:8003/FMprint/mySampleId?copies=2" ]
#   Insert from URL [ Select ; With dialog: Off ; Target:$_ ; $result ; $url ; cURL options: "-X POST" ]
@app.route('/FMprint/<sampleid>', methods=['POST'])
def print_label(sampleid: str):
    copies = request.args.get('copies', default = 1, type = int)

    # try chainway printer
    ps.printLabel(sampleid, copies=copies)

    # try zebra printer
    render_template('zebra_print_service.html',
                           sampleID = sampleid,
                           copies = copies)
    return sampleid

if __name__ == '__main__':
    print(
    """
            RFID Printer Service
    ----------------------------------
    This service exposes an endpoint for the Filemaker App 'Printer Sample Sticker' under 'Sample Room SAM'.
    It runs on localhost:8003 and should run on startup.

    Docs : https://github.com/Jeffersonlii/Rt-Knits-FindSample/tree/main/Rt-Knits-RFIDPrinter
    ----------------------------------
    """)
    app.run(port=8003)

 