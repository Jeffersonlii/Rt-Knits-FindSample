# Rt-Knits-RFIDPrinter

Simple Webapp to print RFID Labels based on the SampleId

# Media

![image](https://github.com/Jeffersonlii/Rt-Knits-FindSample/assets/32963293/54881243-e29d-4710-9b84-e9be636d9ba3)

# Functionality

- User can choose to print from Zebra or Chainway Printer
- User is prompted the success/failure of printer connection
- User can input a sample id into the website
- A RFID Label is then printed from the connected printer, with the RFID overridden as the input Sample ID
- User can then attach the sticker onto the garment

# App Structure

This webapp is a Python Flask webapp.

Python is used to interface with the Chainway ZPL SDK or Zebra BrowserPrint SDK.

Flask is used to spin up a simple webserver serving `html`.

## Styles

Tailwind CSS and Daisy are used to style the page.

Their imports are found in the `head` tag

## SDKs

- Chainway ZPL SDKs are utilized to interface with the Chainway Printer. The SDK is found under `/static/chainwaySDK`

- Zebra BrowserPrint SDKs are utilized to interface with the Zebra Printer. The SDKs are found under `/static/zebraSDK`

# Compatibility

This application is compatible with the printers **Chainway CP30** and **Zebra ZD612R**

# Exterior Requirements - Chainway

1. Please download and install the [Drivers](https://www.chainway.net/Support/Info/30) of the printer

# Exterior Requirements - Zebra

1. The Printer SDK works by talking to the [BrowserPrint Service](https://www.zebra.com/us/en/support-downloads/printer-software/by-request-software.html).

   This service runs on OS startup and exposes an http endpoint (`localhost:9100`) for the SDK to interop.
   <sub>Make sure the port `9100` is unoccupied!</sub>

2. The Service also needs the [Drivers](https://www.zebra.com/us/en/support-downloads/printers/desktop/zd621.html) of the printer.

**Both of these software must be installed and running for the ZEBRA PrinterSDK to work!**

# App Requirements

```
cd \Rt-Knits-RFIDPrinter
.venv\Scripts\activate
```

Activate the venv

```
pip install -r requirements.txt
```

Install the dependencies

# Running the app

Since this is a lightweight app meant to be used on prem, we can simply run the dev server instead of setting up complicated prod environments.

`python app.py`

# Misc Knowledge

- RFID's memory can only hold hexadecimal values. Therefore the written RFID is actually the hexidecial conversion of the sample id. (Any application consuming the RFID must convert it to ASCII to get the SampleID!)
- RFID's memory size depends on the chip but it is minimum 96-bits (12 ASCII Characters or 24 HEX Characters). Therefore wrtting more than that will result in VOIDs printed to the label. SampleIDs at RTKnits nicely fall below this character limit.
- The printer should be fully calibrated before using this application
- SampleIDs are case insensitive, therefor sampleIDs are converted to lowercase before processessing
