# SampleFindr

This Android application allows users to utilize the RFID scanner on their devices to scan for the location of RFID tag. This app is made to be ran on a [ Chainway C5 UHF RFID Reader](https://www.chainway.net/Products/Info/142)

# Download

The App can be downloaded by using the APK provided in `releases`

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
- NOTE !!! If building on the `RTK` network, dependency installation will be blocked. The quickest solution is to install the dependencies on the `RT_Guest` network

# Usage

1. Open the SampleFindr app on your Android device.
2. Grant necessary permissions for RFID scanning if prompted.
3. Input the SampleID to be located
4. The app will display the RFID location signal in real-time on the screen.

# Media

<img src="https://github.com/Jeffersonlii/Rt-Knits-FindSample/assets/32963293/cd59abee-5e71-497f-8fe5-08f7d9bbbb5f" width="300">

[Screen_recording_20231221_172502.webm](https://github.com/Jeffersonlii/Rt-Knits-FindSample/assets/32963293/ae9e3f55-08c0-474c-9f08-b8ee3389939a)

# Contact

If you have any questions or suggestions, feel free to contact the me at ( jeffersonli2013@gmail.com ).

# MISC Knowledge

- This application is not connected to FileMaker. The RFID is simply the SampleID translated to hexadecimal
- The RFID tags on the sample should be printed via the [printer webapp](https://github.com/Jeffersonlii/Rt-Knits-RFIDPrinter)
  - [printer webapp](https://github.com/Jeffersonlii/Rt-Knits-RFIDPrinter) writes the RFID as the hex value of the sampleID during printing
- SampleIDs are case insensitive, therefor the application searches for the `lowercase` of the sampleID. It is assumed that the printer writes the lowercase sampleID onto the RFID labels
