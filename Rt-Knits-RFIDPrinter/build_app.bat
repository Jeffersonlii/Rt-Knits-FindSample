@REM ------ build_app.bat --------
@REM this script builds the flask app into an executable using PyInstaller 
@REM the output EXE is generated in the next to this file as RFIDPrinter.tar.gz
@REM the service can then be started by executing the executable!

@REM remove running instances
taskkill /F /im RFIDPrinterService.exe

@REM clean the directory
rmdir .\build /s /q
mkdir build

cd build

@REM Build the app using pyinstaller
python -m PyInstaller ..\app\app.pyw --onefile --name=RFIDPrinterService

@REM copy over the static file and templates file
@REM as well as the kill service util
xcopy /E /I ..\app\static dist\static
xcopy /E /I ..\app\templates dist\templates
xcopy ..\kill_printer_service.bat dist\
xcopy ..\README.md dist\
xcopy ..\README.md dist\static\assets


@REM zip it up!
tar.exe -czvf ../RFIDPrinter.tar.gz ./dist

echo done building!
echo The Entire App is compiled as a portable zip file in RFIDPrinter.tar
cmd /k