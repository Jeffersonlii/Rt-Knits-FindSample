from flask import Blueprint, render_template
from print_service import PrinterService
ps = PrinterService()
ps_err = ps.initialize()

chainwayApp = Blueprint('chainway', __name__)

@chainwayApp.route('/')
def home():
    return render_template('chainway_index.html', err = ps_err)

@chainwayApp.route('/print/<sampleid>', methods=['POST'])
def print_label(sampleid: str):
    ps.printLabel(sampleid)
    print(f"Sample ID: {sampleid}")
    return sampleid