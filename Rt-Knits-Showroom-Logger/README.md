# RT Knits Showrooom Logger

This Android application allows users to `check in / out` garments in the showroom. This app is made to be ran on a [ Chainway C5 UHF RFID Reader](https://www.chainway.net/Products/Info/142)

The usecase for this application is to track the garments in the sampleroom, and is used in conjunction with the `ShowroomLogger` filemaker layout in the `Marketing Store` database.

# Dev

in `local.properties`,
add these 2 fields

```
FM_user="{YOUR FILEMAKER USERNAME}"
FM_pass="{YOUR FILEMAKER PASSWORD}"
```

Note that the FileMaker user must have the `fmrest`` extended privilege

# Download

The App can be downloaded by using the APK provided in `releases`

# Features

- Scan for Samples : Users can scan for nearby RFID tagged sample
- Operation Selection : Users can select their scanned garments to be `added` or `removed` from the sample room
- Database communication : App uses `Filemaker Data API` to query the database, and is able to display garment information of the scanned samples

# Developement

- Clone repo
  `git clone https://github.com/Jeffersonlii/Rt-Knits-SampleFinder.git`
- Open Project in Android studio
  - Use Gradle JDK 17 (This project uses Gradle version `gradle-8.2`)
- Build and Run on emulator or device
- NOTE !!! If building on the `RTK` network, dependency installation will be blocked. The quickest solution is to install the dependencies on the `RT_Guest` network

### Database connection

This application uses `Filemaker DATA API` which allows 3rd party apps to connect to the database through a priveledged user.

in `local.properties`,
add these 2 fields

```
FM_user="{YOUR FILEMAKER USERNAME}"
FM_pass="{YOUR FILEMAKER PASSWORD}"
```

Note that the FileMaker user must have the `fmrest`` extended privilege

# Media


# Contact

If you have any questions or suggestions, feel free to contact the me at ( jeffersonli2013@gmail.com ).

# MISC Knowledge

- This application is not connected to FileMaker. The RFID is simply the SampleID translated to hexadecimal
- The RFID tags on the sample should be printed via the [printer webapp](https://github.com/Jeffersonlii/Rt-Knits-RFIDPrinter)
  - [printer webapp](https://github.com/Jeffersonlii/Rt-Knits-RFIDPrinter) writes the RFID as the hex value of the sampleID during printing
- SampleIDs are case insensitive, therefor the application searches for the `lowercase` of the sampleID. It is assumed that the printer writes the lowercase sampleID onto the RFID labels
