from flask import Blueprint, render_template

zebraApp = Blueprint('zebra', __name__)

@zebraApp.route('/')
def home():
    return render_template('zebra_index.html')