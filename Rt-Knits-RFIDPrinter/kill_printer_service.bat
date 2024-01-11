@REM --------------- kill_printer_service.bat ---------------
@REM this script kills all instances of the RFID Printer Service 
@REM kills in 2 steps 
@REM Step 1. kills all services listening on port 8003 (our designated port for the service)
@REM Step 2. kills all EXE with the name RFIDPrinterService (our designated name for the service)

@REM Kills the printer service by killing all services listening on port 8003
for /f "tokens=5" %%a in ('netstat -aon ^| findstr "LISTENING" ^| findstr :8003') do (
    echo Killing PID: %%a
    taskkill /F /PID %%a
    wmic process where processid="%%a" call terminate
)

@REM kill the services running as EXE
taskkill /F /im RFIDPrinterService.exe

cmd /k