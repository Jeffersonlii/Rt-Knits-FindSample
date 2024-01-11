# Rt-Knits-RFIDPrinter

Simple Webapp to print RFID Labels based on the SampleId

# App Structure

This webapp is a Python Flask webapp.

Python is used to interface with the Chainway ZPL SDK or Zebra BrowserPrint SDK.

Flask is used to spin up a simple webserver

## Styles

Tailwind CSS and Daisy are used to style the page.

Their imports are found in the `head` tag

## SDKs

- Chainway ZPL SDKs are utilized to interface with the Chainway Printer. The SDK is found under `/static/chainwaySDK`

- Zebra BrowserPrint SDKs are utilized to interface with the Zebra Printer. The SDKs are found under `/static/zebraSDK`

# Compatibility

This application is compatible with the printers **Chainway CP30** and **Zebra ZD612R**

This application is meant to be ran on **windows**

# Exterior Requirements - Chainway

1. Please download and install the [Drivers](https://www.chainway.net/Support/Info/30) of the printer

# Exterior Requirements - Zebra

1. The Printer SDK works by talking to the [BrowserPrint Service](https://www.zebra.com/us/en/support-downloads/printer-software/by-request-software.html).

   This service runs on OS startup and exposes an http endpoint (`localhost:9100`) for the SDK to interop.
   <sub>Make sure the port `9100` is unoccupied!</sub>

2. The Service also needs the [Drivers](https://www.zebra.com/us/en/support-downloads/printers/desktop/zd621.html) of the printer.

**Both of these software must be installed and running for the ZEBRA PrinterSDK to work!**

# App Requirements

install python3

```
cd \Rt-Knits-RFIDPrinter\app
.venv\Scripts\activate
```

Activate the venv

```
pip install -r requirements.txt
```

Install the dependencies

# Running the app

use `run_printer_service.bat` to spin up the flask server

# Building the app

use `build_app.bat` to build the flask app into an executable that can be deployed
