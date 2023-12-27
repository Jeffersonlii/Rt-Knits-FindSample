# SampleFindr

This Android application allows users to utilize the RFID scanner on their devices to scan for the location of RFID tag. This app is made to be ran on a [ Chainway C5 UHF RFID Reader](https://www.chainway.net/Products/Info/142) OR
[Sunmi UHF Reader](https://www.sunmi.com/en-US/l2k/)

# Features

- SampleID input : The user is able to input the desired SampleID to locate.
  - The SampleID is translated to the RFID in the background
- Signal Strength Display : View the signal strength of the RFID tag to allow for location of the tag.

# Developement

- Clone repo
  `git clone https://github.com/Jeffersonlii/Rt-Knits-SampleFinder.git`
- Open Project in Android studio
  - Use Gradle JDK 17 (This project uses Gradle version `gradle-8.2`)
- Build and Run on emulator or device

# Usage

1. Open the SampleFindr app on your Android device.
2. Grant necessary permissions for RFID scanning if prompted.
3. Input the SampleID to be located
4. The app will display the RFID location signal in real-time on the screen.

# Media

<img src="https://github.com/Jeffersonlii/Rt-Knits-SampleFinder/assets/32963293/2a593336-7adc-4cd7-af3c-301a9563be5a" width="300">

[Screen_recording_20231221_172502.webm](https://github.com/Jeffersonlii/Rt-Knits-SampleFinder/assets/32963293/a7a573ce-b619-4d2c-b543-4d9fec6e1380)

# Contact

If you have any questions or suggestions, feel free to contact the me at ( jeffersonli2013@gmail.com ).

# MISC Knowledge

- This application is not connected to FileMaker. The RFID is simply the SampleID translated to hexadecimal
- The RFID tags on the sample should be printed via the [printer webapp](https://github.com/Jeffersonlii/Rt-Knits-RFIDPrinter)
  - [printer webapp](https://github.com/Jeffersonlii/Rt-Knits-RFIDPrinter) writes the RFID as the hex value of the sampleID during printing

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
