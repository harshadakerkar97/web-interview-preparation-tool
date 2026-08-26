@echo off
REM InterviewPrep Web - Mobile-friendly version
REM Access from your phone at http://<your-pc-ip>:8080

set PATH=C:\Users\harshada\Downloads\apache-maven-3.9.16-bin\apache-maven-3.9.16\bin;%PATH%
cd /d %~dp0

echo.
echo ============================================
echo   InterviewPrep Pro - Web/Mobile Version
echo ============================================
echo.

if not exist "target\interviewprep-web-1.0.0.jar" (
    echo Building the web app...
    call mvn package -DskipTests -q
    if %ERRORLEVEL% neq 0 (
        echo Build failed!
        pause
        exit /b 1
    )
    echo Build complete!
)

echo.
echo Starting server on port 8080...
echo.
echo   Local:   http://localhost:8080
echo   Mobile:  http://<YOUR-PC-IP>:8080
echo.
echo   To find your PC IP, run: ipconfig
echo   Look for "IPv4 Address" under your WiFi adapter.
echo   Make sure phone is on the same WiFi network.
echo.
echo   Press Ctrl+C to stop the server.
echo ============================================
echo.

java -jar target\interviewprep-web-1.0.0.jar
