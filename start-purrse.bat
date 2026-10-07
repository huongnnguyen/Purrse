@echo off
REM Starts Purrse and opens it in its own window. Requires PostgreSQL running with the purrse_dev database.
REM If your Postgres password isn't empty, set it first:  set PURRSE_DB_PASSWORD=yourpassword
REM (or put spring.datasource.password=... in backend\application-local.properties)

cd /d "%~dp0backend"

REM Wait for the server in the background, then open the app window.
start "" /min powershell -NoProfile -Command "for($i=0;$i -lt 120;$i++){ try { Invoke-WebRequest -UseBasicParsing http://localhost:8080/api/dashboard -TimeoutSec 2 | Out-Null; try { Start-Process msedge '--app=http://localhost:8080' } catch { Start-Process 'http://localhost:8080' }; break } catch { Start-Sleep 1 } }"

echo Starting Purrse... close this window to stop it.
call gradlew.bat bootRun
pause
