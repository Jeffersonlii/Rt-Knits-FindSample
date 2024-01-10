@REM Kills the printer service by killing all services listening on port 8003

@echo off
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8003') do (
    echo Killing PID: %%a
    taskkill /F /PID %%a
)
