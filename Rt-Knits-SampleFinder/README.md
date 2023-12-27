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
