# RFID Printer Service

This python flask app allows for print of RFID Labels for Samples at RT Knits

# Installation

1. Download application `RFIDPrinter.tar.gz`
1. Install `python3`
1. run service by executing `RFIDPrinterService.exe`
1. Configure the service to run on startup
   1. Create shortcut for `RFIDPrinterService.exe`
   1. put shortcut in `C:\ProgramData\Microsoft\Windows\Start Menu\Programs\Startup`
1. Confirm the service is active by visiting `localhost:8003`

# Compatibility

This application is compatible with the printers **Chainway CP30**

This application is meant to be ran on **windows**

## Exterior Requirements

1. Please download and install the [Drivers](https://www.chainway.net/Support/Info/30) of the printer

# Functionality

This app exposes an API at `localhost:8003` to print RFID Sample Garment stickers at RT Knits. The software must be ran on the sample computer which is connected to the Chainway CP30 Printer

### [GET] http://127.0.0.1:8003/customerStickerPrint/{SAMPLEID}?copies={COPIES}

Prints the RFID Sticker Label `COPIES` times. The Hexadecimal of `SAMPLEID` is written to the EPC Field of the RFID Chip. The design of the label is defined by `app/static/StickerTemplates/CustomerSampleSticker.prn`.

#### Body

The body of the request must be a JSON object following the following convention.

- the field `<SampleID>` in the nlbl file may be updated to value `12345SG` by including the following pair in the JSON body
- `{... , "SampleID" : "12345SG", ...}`

#### Notes

1. Host computer must be connected to the RFID Printer
2. **For development information, view `app/README.md`**

# Making Changes to the Label Design

1. Open `static/StickerTemplates/CustomerSampleStickerDesign.nlbl` in Zebra Designer
2. Make Changes
3. Save as `CustomerSampleSticker.prn` in `app/static/StickerTemplates/`

### Limitations

1. variable names **cannot** be multilined
   ```
   ex.
   <multiline
   dvariable>
   ```
2. no word/sentence wrapping functionality

# SImple Label

### [GET] http://127.0.0.1:8003/simpleRFIDPrint/{SAMPLEID}?copies={COPIES}

This app also support printing simple labels, with a simple design with only the `SampleID`.

A webpage at http://127.0.0.1:8003/print provides a simple interface to print this sticker.

Also, http://127.0.0.1:8003/printFromBarcode provides a quick method print stickers for prexisting samples.
It accepts the `Item Ref` of the sample, which is written on the `barcode` of the pre-existing sample sticker.

The mapping from `Item Ref` to `SampleID` is stored in memory as a dictionary, and the data is loaded in from `/static/mapping.xlsx`.
This method of printing is primarily to accomodate printing of the pre-existing samples. As time passes, this method would not be used as RFID stickers should be printed at the time of sample creation.

# Helpful Batch Scripts

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

# Troubleshooting

- Restart the Printer, Followed by the app
- when using the 100 by 120 mm stickers, be sure to calibrate the chainway printer by going to `Setting->Label Setup -> Advanced Learning -> 120 mm Paper Length`

# Misc Knowledge

- **The Endpoint is not meant to be called manually, you may find the Script `Print SampleSticker(marketingStore) Copy` in the `Garment` Database that uses this service**
- RFID's memory can only hold hexadecimal values. Therefore the written RFID is actually the hexidecial conversion of the sample id. (Any application consuming the RFID must convert it to ASCII to get the SampleID!)
- RFID's memory size depends on the chip but it is minimum 96-bits (12 ASCII Characters or 24 HEX Characters). Therefore wrtting more than that will result in VOIDs printed to the label. SampleIDs at RTKnits nicely fall below this character limit.
- The printer should be fully calibrated before using this application
- SampleIDs are case insensitive, therefor sampleIDs are converted to lowercase before processessing
- To write to the EPC field, a ZPL command must be send to the printer, therefor any designs must also be specified in the ZPL command. (This is why we must send ZPL to the printer, instead of simply printing a page)
