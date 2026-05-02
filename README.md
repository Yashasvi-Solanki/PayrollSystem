<div align="center">

![Payroll System Banner](./cover_bg.png)

# 💼 Multithreaded Payroll Management System

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](#)
[![OOP](https://img.shields.io/badge/Object_Oriented_Programming-007396?style=for-the-badge&logo=java&logoColor=white)](#)
[![Multithreading](https://img.shields.io/badge/Multithreading-2C2255?style=for-the-badge&logo=react&logoColor=white)](#)

A complete **console-based Java application** demonstrating core Object-Oriented Programming, Multithreading, Collections, File I/O, Exception Handling, and JDBC concepts.
</div>

---

## 🚀 Features

- 👥 **Manage Employees:** Add, Update, Delete, and Search for employees dynamically.
- ⚡ **Multithreaded Payroll Processing:** Processes each employee's payroll in a separate thread using `ExecutorService` for maximum performance.
- 🔒 **Thread-Safe Operations:** Guarantees concurrent-safe file writing and data structures using `synchronized` blocks and `Collections.synchronizedMap()`.
- 💾 **File Persistence:** Automatically saves employee records to CSV and loads them seamlessly on startup.
- 📄 **Automated Payslips:** Generates individualized payslip text files for every employee.
- ⚠️ **Robust Error Handling:** Custom exceptions including `EmployeeNotFoundException` and `DuplicateEmployeeException`.
- 📊 **Comprehensive Reporting:** Generates a detailed department-wise payroll summary report.
- 🗄️ **Database Integration:** Optional MySQL/JDBC connection via a Singleton `DBConnection` and DAO pattern.

---

## 🧠 Java Concepts Demonstrated

| Concept | Implementation in Project |
|:---|:---|
| **Abstract Class & Inheritance** | `Employee`, `FullTimeEmployee`, `PartTimeEmployee`, `ContractEmployee` |
| **Encapsulation** | Private fields with strict getters/setters in `Employee` |
| **Polymorphism** | `Employee` references seamlessly hold subclass objects throughout the application |
| **ArrayList & HashMap** | `EmployeeManager` utilizes ordered lists with O(1) ID lookup |
| **Thread-Safe Collections** | `Collections.synchronizedMap()` deployed in `EmployeeManager` |
| **`Runnable` Interface** | `PayrollWorker` assigns one thread per employee |
| **Extending `Thread`** | `FileWriterThread` dedicated to writing summary reports |
| **`ExecutorService`** | `PayrollManager` utilizes a thread pool to manage concurrent execution |
| **Concurrency Simulation** | `Thread.sleep()` used to simulate realistic processing delays in `PayrollWorker` |
| **Synchronization** | `synchronized` blocks protect file writes in `PayrollWorker`, `Logger`, and `PayslipGenerator` |
| **Thread-Safe Counters** | `AtomicInteger` utilized for reliable counting in `PayrollWorker` |
| **File I/O Streams** | `FileWriter` & `BufferedReader` handle read/write in `FileHandler`, `Logger`, and `PayslipGenerator` |
| **Custom Exceptions** | `EmployeeNotFoundException`, `DuplicateEmployeeException` enforce strict business logic |
| **Exception Handling** | Extensive `try-catch` blocks manage `NumberFormatException`, `IOException`, and `InterruptedException` |
| **JDBC Architecture** | `DBConnection` and `EmployeeDAO` showcase database integration via `PreparedStatement` |

---

## 📁 Project Structure

```text
PayrollSystem/
├── src/com/payroll/
│   ├── Main.java                          ← Entry point, interactive console menu
│   ├── model/                             ← Entities and inheritance hierarchy
│   │   ├── Employee.java                  
│   │   ├── FullTimeEmployee.java
│   │   ├── PartTimeEmployee.java
│   │   └── ContractEmployee.java
│   ├── manager/                           ← Business logic and concurrency
│   │   ├── EmployeeManager.java           
│   │   └── PayrollManager.java            
│   ├── thread/                            ← Thread workers and runnables
│   │   ├── PayrollWorker.java             
│   │   └── FileWriterThread.java          
│   ├── exception/                         ← Custom exception classes
│   │   ├── EmployeeNotFoundException.java
│   │   └── DuplicateEmployeeException.java
│   ├── util/                              ← Helper utilities for I/O and logging
│   │   ├── Logger.java
│   │   ├── FileHandler.java
│   │   └── PayslipGenerator.java
│   └── db/                                ← Database access layer
│       ├── DBConnection.java              
│       └── EmployeeDAO.java               
├── data/employees.csv                     ← Persistent data storage
├── payslips/                              ← Output directory for generated payslips
├── logs/error.log                         ← System logs
├── compile.bat                            ← Windows compilation script
└── run.bat                                ← Windows execution script
```

---

## ▶️ How to Run

### 🛠️ Compilation
```bat
javac -d out -sourcepath src src\com\payroll\Main.java src\com\payroll\model\*.java src\com\payroll\manager\*.java src\com\payroll\thread\*.java src\com\payroll\exception\*.java src\com\payroll\util\*.java src\com\payroll\db\*.java
```

### 🚀 Execution
```bat
java -cp out com.payroll.Main
```

> **💡 Tip:** On Windows, you can simply double-click **`compile.bat`** followed by **`run.bat`** to start the application instantly!

---

## 💰 Salary Formula

```math
Gross = Base + HRA (20%) + DA (10%)
```
```math
Deductions = PF (12% of Base) + Tax (10% of Gross)
```
```math
Net Salary = Gross - Deductions
```

---

## 📄 Documentation

For a complete, file-by-file explanation of every class and an in-depth breakdown of the Java concepts used, please view the interactive documentation:

👉 **[ProjectDoc.html](./ProjectDoc.html)**
