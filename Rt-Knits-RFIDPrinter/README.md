# RFID Printer Service

This python flask app allows for print of RFID Labels for Samples at RT Knits

![image](https://github.com/Jeffersonlii/Rt-Knits-FindSample/assets/32963293/54881243-e29d-4710-9b84-e9be636d9ba3)

# Installation

1. Download application `RFIDPrinter.tar.gz`
1. Install `python3`
1. run service by executing `RFIDPrinterService.exe`
1. Configure the service to run on startup
   1. Create shortcut for `RFIDPrinterService.exe`
   1. put shortcut in `C:\ProgramData\Microsoft\Windows\Start Menu\Programs\Startup`

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

# Functionality

This app is a flask service that serves a easy to use web interface, as well as an API

### Webapp

- Available at http://127.0.0.1:8003/
- User can choose to print from Zebra or Chainway Printer
- User can input a sample id into the website
- A RFID Label is then printed from the connected printer, with the RFID overridden as the input Sample ID
- User can then attach the sticker onto the garment

### Printing API

- Available at http://127.0.0.1:8003/RFIDprint/{SAMPLEID}?copies={COPIES}
- External Apps (ex. Filemaker) can use this endpoint to print RFID Labels on the chainway Printer

**For development information, view `app/README.md`**

# Batch Scripts

This application comes with 3 windows batch scripts to help with development

**build_app.bat**

- Builds the app as an `.EXE` to `./RFIDPrinter.tar.gz`
- In production, the EXE should be ran on windows startup
- This service can be ran on windows startup by putting `RFIDPrinterService.exe` in
  `C:\ProgramData\Microsoft\Windows\Start Menu\Programs\Startup`
 
**run_printer_service.bat**

- Runs the app as a flask web service, appropriate for debugging

**kill_printer_service.bat**

- kills all active instances of the RFID Printer service

# Misc Knowledge

- RFID's memory can only hold hexadecimal values. Therefore the written RFID is actually the hexidecial conversion of the sample id. (Any application consuming the RFID must convert it to ASCII to get the SampleID!)
- RFID's memory size depends on the chip but it is minimum 96-bits (12 ASCII Characters or 24 HEX Characters). Therefore wrtting more than that will result in VOIDs printed to the label. SampleIDs at RTKnits nicely fall below this character limit.
- The printer should be fully calibrated before using this application
- SampleIDs are case insensitive, therefor sampleIDs are converted to lowercase before processessing
