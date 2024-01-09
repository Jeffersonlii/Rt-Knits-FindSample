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

@app.route('/FMprint/<sampleid>', methods=['POST'])
def print_label(sampleid: str):
    repeat = request.args.get('repeat', default = 1, type = int)

    # try chainway printer
    ps.printLabel(sampleid, repeat=repeat)

    # try zebra printer
    render_template('zebra_print_service.html',
                           sampleID = sampleid,
                           repeat = repeat)
    return sampleid

if __name__ == '__main__':
    app.run(debug=True, port=8003)