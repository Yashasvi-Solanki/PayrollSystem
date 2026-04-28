# ☕ Multithreaded Payroll Management System

A complete **console-based Java application** demonstrating core Object-Oriented Programming, Multithreading, Collections, File I/O, Exception Handling, and JDBC concepts.

> **OOP Project | Java (JDK 17+)**

---

## 🚀 Features

- **Add / Update / Delete / Search** employees
- **Multithreaded Payroll Processing** — one thread per employee using `ExecutorService`
- **Concurrent-safe file writing** using `synchronized` blocks
- **File Persistence** — employees saved to CSV, auto-loaded on startup
- **Individual Payslips** generated per employee
- **Custom Exceptions** — `EmployeeNotFoundException`, `DuplicateEmployeeException`
- **Department-wise Payroll Report**
- **MySQL/JDBC** integration (optional)

---

## 🧠 Java Concepts Demonstrated

| Concept | Where Used |
|---|---|
| Abstract Class & Inheritance | `Employee`, `FullTimeEmployee`, `PartTimeEmployee`, `ContractEmployee` |
| Encapsulation | Private fields + getters/setters in `Employee` |
| Polymorphism | `Employee` references hold subclass objects throughout |
| ArrayList + HashMap | `EmployeeManager` — ordered list + O(1) ID lookup |
| `Collections.synchronizedMap()` | Thread-safe HashMap in `EmployeeManager` |
| `Runnable` interface | `PayrollWorker` — one thread per employee |
| `extends Thread` | `FileWriterThread` — summary writer |
| `ExecutorService` + Thread Pool | `PayrollManager` — manages concurrent payroll |
| `Thread.sleep()` | Simulated processing delay in `PayrollWorker` |
| `synchronized` | File write protection in `PayrollWorker`, `Logger`, `PayslipGenerator` |
| `AtomicInteger` | Thread-safe counter in `PayrollWorker` |
| `FileWriter` + `BufferedReader` | File I/O in `FileHandler`, `Logger`, `PayslipGenerator` |
| Custom Exceptions | `EmployeeNotFoundException`, `DuplicateEmployeeException` |
| `try-catch` | `NumberFormatException`, `IOException`, `InterruptedException` |
| JDBC + `PreparedStatement` | `DBConnection`, `EmployeeDAO` |

---

## 📁 Project Structure

```
PayrollSystem/
├── src/com/payroll/
│   ├── Main.java                          ← Entry point, console menu
│   ├── model/
│   │   ├── Employee.java                  ← Abstract base class
│   │   ├── FullTimeEmployee.java
│   │   ├── PartTimeEmployee.java
│   │   └── ContractEmployee.java
│   ├── manager/
│   │   ├── EmployeeManager.java           ← CRUD with ArrayList + HashMap
│   │   └── PayrollManager.java            ← ExecutorService thread pool
│   ├── thread/
│   │   ├── PayrollWorker.java             ← Runnable per employee
│   │   └── FileWriterThread.java          ← Extends Thread
│   ├── exception/
│   │   ├── EmployeeNotFoundException.java
│   │   └── DuplicateEmployeeException.java
│   ├── util/
│   │   ├── Logger.java
│   │   ├── FileHandler.java
│   │   └── PayslipGenerator.java
│   └── db/
│       ├── DBConnection.java              ← JDBC Singleton
│       └── EmployeeDAO.java               ← MySQL CRUD
├── data/employees.csv
├── payslips/
├── logs/error.log
├── compile.bat
└── run.bat
```

---

## ▶️ How to Run

### Compile
```bat
javac -d out -sourcepath src src\com\payroll\Main.java src\com\payroll\model\*.java src\com\payroll\manager\*.java src\com\payroll\thread\*.java src\com\payroll\exception\*.java src\com\payroll\util\*.java src\com\payroll\db\*.java
```

### Run
```bat
java -cp out com.payroll.Main
```

Or double-click **`compile.bat`** → **`run.bat`**

---

## 💰 Salary Formula

```
Gross  = Base + HRA (20%) + DA (10%)
Deduct = PF (12% of Base) + Tax (10% of Gross)
Net    = Gross - Deductions
```

---

## 📄 Documentation

See [`ProjectDoc.html`](./ProjectDoc.html) for full file-by-file explanation of every class and the Java concepts used.
