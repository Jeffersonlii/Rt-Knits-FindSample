# Rt-Knits-RFIDPrinter-Chainway

Simple Webapp to print RFID Labels based on the SampleId

# Functionality

- User is prompted the success/failure of printer connection
- User can input a sample id into the website
- A RFID Label is then printed from the connected printer, with the RFID overridden as the input Sample ID
- User can then attach the sticker onto the garment

# App Structure

This webapp is a Python Flask webapp.

Python is used to interface with the Chainway ZPL SDK.

Flask is used to spin up a simple webserver serving `index.html`.

## Styles

Tailwind CSS and Daisy are used to style the page.

Their imports are found in the `head` tag

## SDKs

Chainway ZPL SDKs are utilized to interface with the Chainway Printer. The SDK is found under `/sdk`

# Compatibility

This application is written to be used with the printer **Chainway CP30** in mind.

# Exterior Requirements

1. Please download and install the [Drivers](https://www.chainway.net/Support/Info/30) of the printer

# App Requirements

```
cd \Rt-Knits-RFIDPrinter-Chainway
.venv\Scripts\activate
```

Activate the venv

```
pip install -r requirements.txt
```

Install the dependencies

# Running the app

Since this is a lightweight app meant to be used on prem, we can simply run the dev server instead of setting up complicated prod environments.

`flask --app flask_app run`

# Misc Knowledge

- RFID's memory can only hold hexadecimal values. Therefore the written RFID is actually the hexidecial conversion of the sample id. (Any application consuming the RFID must convert it to ASCII to get the SampleID!)
- RFID's memory size depends on the chip but it is minimum 96-bits (12 ASCII Characters or 24 HEX Characters). Therefore wrtting more than that will result in VOIDs printed to the label. SampleIDs at RTKnits nicely fall below this character limit.
- The printer should be fully calibrated before using this application
