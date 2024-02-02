# Rt-Knits-RFIDPrinter

Simple Webapp to print RFID Labels based on the SampleId

# App Structure

This webapp is a Python Flask webapp.

Python is used to interface with the Chainway ZPL SDK.

`Flask` is used to spin up a simple webserver.

`pyinstaller` is used to package this app in production as an executable

# Exterior Requirements

1. Please download and install the [Drivers](https://www.chainway.net/Support/Info/30) of the printer

# Running the App

1. install python3

2. Activate the venv
   ```
   cmd> cd \Rt-Knits-RFIDPrinter\app
   cmd> .venv\Scripts\activate
   ```
3. Install the dependencies
   ```
   cmd> pip install -r requirements.txt
   ```
4. Run the App
   ```
   cmd> python3 app/app/pyw
   ```
   or
   ```
   cmd> run_printer_service.bat
   ```

# Building the app

use `build_app.bat` to build the flask app into an executable that can be deployed
