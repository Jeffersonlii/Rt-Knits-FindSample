# Rt-Knits-RFIDPrinter-Zebra

Simple Webapp to print RFID Labels based on the SampleId

# Functionality

- User can switch between different detected printers
- User can input a sample id into the website
- A RFID Label is then printed from the connected printer, with the RFID overridden as the input Sample ID
- User can then attach the sticker onto the garment

# App Structure

This webapp is a simple html file, containing HTML and JS

## Styles

Tailwind CSS and Daisy are used to style the page.

Their imports are found in the `head` tag

## SDKs

Zebra BrowserPrint SDKs are utilized to interface with the Zebra Printer. The SDKs are found under `/PrinterSDK`

# Compatibility

This application is written to be used with the printer **Zebra ZD621R** in mind. And to be ran in a **_chromium_** browser in **_Windows_**.

# Exterior Requirements

1. The Printer SDK works by talking to the [BrowserPrint Service](https://www.zebra.com/us/en/support-downloads/printer-software/by-request-software.html).

   This service runs on OS startup and exposes an http endpoint (`localhost:9100`) for the SDK to interop.
   <sub>Make sure the port `9100` is unoccupied!</sub>

2. The Service also needs the [Drivers](https://www.zebra.com/us/en/support-downloads/printers/desktop/zd621.html) of the printer.

**Both of these software must be installed and running for the PrinterSDK to work!**

# Running the app

THe entire app is a single `index.html` file, and can be easily ran by simply openening the file in a browser. **However, the printer SDK requires the app to be ran within a webserver.**

The easiest way to run the app in a webserver is running the following in CMD.

```
cd /Rt-Knits-RFIDPrinter
python3 -m http.server
```

<sub>Requires Python3</sub>

Please remember to also have the **printer drivers** and **BrowserPrint Service** installed and running!

# Misc Knowledge

- RFID's memory can only hold hexadecimal values. Therefore the written RFID is actually the hexidecial conversion of the sample id. (Any application consuming the RFID must convert it to ASCII to get the SampleID!)
- RFID's memory size depends on the chip but it is minimum 96-bits (12 ASCII Characters or 24 HEX Characters). Therefore wrtting more than that will result in VOIDs printed to the label. SampleIDs at RTKnits nicely fall below this character limit.
- The printer should be fully calibrated before using this application
