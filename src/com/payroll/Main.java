package com.payroll;

import com.payroll.exception.DuplicateEmployeeException;
import com.payroll.exception.EmployeeNotFoundException;
import com.payroll.manager.EmployeeManager;
import com.payroll.manager.PayrollManager;
import com.payroll.model.*;
import com.payroll.util.Logger;

import java.util.Scanner;

/**
 * CLI entry point (alternative to the GUI).
 * Launch with: java -cp out com.payroll.Main
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static EmployeeManager employeeManager;
    private static PayrollManager  payrollManager;

    public static void main(String[] args) {
        employeeManager = new EmployeeManager();
        payrollManager  = new PayrollManager(employeeManager);
        Logger.info("Payroll CLI started.");
        System.out.println("\n  Welcome to the Payroll Management System (CLI)");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter choice: ");
            switch (choice) {
                case 1  -> addEmployee();
                case 2  -> employeeManager.displayAllEmployees();
                case 3  -> searchEmployee();
                case 4  -> updateEmployee();
                case 5  -> deleteEmployee();
                case 6  -> runPayroll();
                case 7  -> payrollManager.generateDepartmentReport();
                case 8  -> { employeeManager.saveToFile(); running = false;
                             System.out.println("  Saved. Goodbye!"); }
                default -> System.out.println("  Invalid choice (1–8).");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n==================================================");
        System.out.println("         PAYROLL MANAGEMENT SYSTEM — CLI");
        System.out.println("==================================================");
        System.out.println("  1. Add Employee        5. Delete Employee");
        System.out.println("  2. View All Employees  6. Run Payroll (Multi-threaded)");
        System.out.println("  3. Search Employee     7. Department Report");
        System.out.println("  4. Update Employee     8. Save & Exit");
        System.out.println("==================================================");
    }

    private static void addEmployee() {
        System.out.print("  ID: ");          String id   = scanner.nextLine().trim().toUpperCase();
        System.out.print("  Name: ");        String name = scanner.nextLine().trim();
        System.out.print("  Department: "); String dept = scanner.nextLine().trim();
        System.out.println("  Type: 1=Full-Time  2=Part-Time  3=Contract");
        int t = readInt("  Choose: ");
        double sal = readDouble("  Base Salary: ");
        String type = switch (t) { case 2 -> "PART_TIME"; case 3 -> "CONTRACT"; default -> "FULL_TIME"; };
        try {
            Employee emp = EmployeeFactory.create(type, id, name, dept, sal);
            employeeManager.addEmployee(emp);
            System.out.println("  Added: " + id);
        } catch (DuplicateEmployeeException e) { System.out.println("  ERROR: " + e.getMessage()); }
    }

    private static void searchEmployee() {
        System.out.print("  Search by name: ");
        employeeManager.searchByName(scanner.nextLine().trim());
    }

    private static void updateEmployee() {
        System.out.print("  Employee ID: ");
        String id = scanner.nextLine().trim().toUpperCase();
        try {
            Employee e = employeeManager.getEmployeeById(id);
            System.out.print("  Name [" + e.getName() + "]: ");
            String n = scanner.nextLine().trim();
            if (n.isEmpty()) n = e.getName();
            System.out.print("  Dept [" + e.getDepartment() + "]: ");
            String d = scanner.nextLine().trim();
            if (d.isEmpty()) d = e.getDepartment();
            double s = readDouble("  Salary [" + e.getBaseSalary() + "]: ");
            employeeManager.updateEmployee(id, n, d, s);
            System.out.println("  Updated.");
        } catch (EmployeeNotFoundException ex) { System.out.println("  " + ex.getMessage()); }
    }

    private static void deleteEmployee() {
        System.out.print("  Employee ID: ");
        String id = scanner.nextLine().trim().toUpperCase();
        System.out.print("  Confirm delete? (yes/no): ");
        if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
            try { employeeManager.deleteEmployee(id); System.out.println("  Deleted."); }
            catch (EmployeeNotFoundException ex) { System.out.println("  " + ex.getMessage()); }
        }
    }

    private static void runPayroll() {
        System.out.println("  Starting payroll run (multi-threaded)…");
        try {
            payrollManager.runPayroll(pct ->
                System.out.print("\r  Progress: " + pct + "%  "));
            System.out.println("\n  Done! Check payslips/ and logs/audit.log");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("  Interrupted.");
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print("  " + prompt);
            try { return Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("  Enter a number."); }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print("  " + prompt);
            try { return Double.parseDouble(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("  Enter a valid number."); }
        }
    }
}
