from flask import Flask, render_template, request
from endpoints.chainway import chainwayApp
from endpoints.zebra import zebraApp

app = Flask(__name__)
app.register_blueprint(chainwayApp, url_prefix='/chainway')
app.register_blueprint(zebraApp, url_prefix='/zebra')

@app.route('/')
def home():
    return render_template('choose.html')

@app.route('/print/<sampleid>', methods=['POST'])
def print_label(sampleid: str):
    ps.print(sampleid)
    print(f"Sample ID: {sampleid}")
    return sampleid

if __name__ == '__main__':
    app.run(debug=True)