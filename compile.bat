@echo off
echo Compiling Payroll Management System (Full Architecture)...
echo.

if not exist "out"      mkdir out
if not exist "data"     mkdir data
if not exist "logs"     mkdir logs
if not exist "payslips" mkdir payslips

javac -d out -sourcepath src ^
  src\com\payroll\gui\PayrollApp.java ^
  src\com\payroll\gui\LoginScreen.java ^
  src\com\payroll\gui\MainDashboard.java ^
  src\com\payroll\model\Employee.java ^
  src\com\payroll\model\FullTimeEmployee.java ^
  src\com\payroll\model\PartTimeEmployee.java ^
  src\com\payroll\model\ContractEmployee.java ^
  src\com\payroll\model\EmployeeFactory.java ^
  src\com\payroll\model\PayrollRecord.java ^
  src\com\payroll\service\TaxCalculationService.java ^
  src\com\payroll\service\AuthenticationService.java ^
  src\com\payroll\service\PayrollProcessingEngine.java ^
  src\com\payroll\manager\EmployeeManager.java ^
  src\com\payroll\manager\PayrollManager.java ^
  src\com\payroll\thread\PayrollWorker.java ^
  src\com\payroll\thread\FileWriterThread.java ^
  src\com\payroll\util\Logger.java ^
  src\com\payroll\util\AuditLogger.java ^
  src\com\payroll\util\FileHandler.java ^
  src\com\payroll\util\PayslipGenerator.java ^
  src\com\payroll\util\PayrollHistoryStore.java ^
  src\com\payroll\exception\DuplicateEmployeeException.java ^
  src\com\payroll\exception\EmployeeNotFoundException.java ^
  src\com\payroll\Main.java

if %errorlevel% neq 0 (
    echo.
    echo COMPILATION FAILED. Check errors above.
    pause
    exit /b 1
)

echo.
echo Compilation successful!
echo Run the GUI with:     run.bat
echo Run the CLI with:     run_cli.bat
pause
