from flask import Flask, render_template, request
from print_service import PrinterService
ps = PrinterService()
ps_err = ps.initialize()
app = Flask(__name__)

@app.route('/')
def home():
    return render_template('index.html', err = ps_err)

@app.route('/print/<sampleid>', methods=['POST'])
def print_label(sampleid: str):
    ps.print(sampleid)
    print(f"Sample ID: {sampleid}")
    return sampleid

if __name__ == '__main__':
    app.run(debug=True)