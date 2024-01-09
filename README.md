# RT Knits FindSample

This project is a collection of projects that enable users to locate garment samples using RFID Scanners.

The applications include:

- SampleFinder: An Android application that allows users to scan for the location of RFID tags using the RFID scanner on their devices. This app is compatible with Chainway C5 UHF RFID Reader and Sunmi UHF Reader.
- Rt-Knits-RFIDPrinter: A web application that allows users to print RFID labels based on the sample ID using the Chainway CP30 printer AND the Zebra ZD612R printer.

# WorkFlow

The workflow of using these applications is as follows:

**During creation of sample**

1. Print the RFID labels using either the Chainway or Zebra web application, depending on the printer. The web application will override the RFID tag with the hexadecimal value of the sample ID.
2. Attach the RFID label to the sample garment.

**When Sample is lost**

1. Open the SampleFindr app on RFID scanner and input the sample ID to be located.
2. The app will display the RFID location signal in real-time on the screen, allowing the user to find the sample.

# Misc Knowledge

- The RFID tags can only hold hexadecimal values, so the sample ID must be converted to hex before writing or reading the RFID tag.
- The RFID tags have a minimum memory size of 96 bits (12 ASCII characters or 24 hex characters), so the sample ID must not exceed this limit.
- The printers and the Android devices must be fully calibrated before using the applications.
- The Chainway Scanner has significantly better performance than the Sunmi

# Repos

The source code and documentation of each application can be found in the following repositories:

- Rt-Knits-SampleFinder
- Rt-Knits-RFIDPrinter
