@echo off
echo Compiling Payroll Management System...

:: Create output directory
if not exist "out" mkdir out

:: Compile all Java files
javac -d out -sourcepath src src\com\payroll\Main.java src\com\payroll\gui\*.java src\com\payroll\model\*.java src\com\payroll\manager\*.java src\com\payroll\thread\*.java src\com\payroll\exception\*.java src\com\payroll\util\*.java src\com\payroll\db\*.java

if %errorlevel% neq 0 (
    echo.
    echo COMPILATION FAILED. Check errors above.
    pause
    exit /b 1
)

echo.
echo Compilation successful!
echo Run the app with: run.bat
pause
