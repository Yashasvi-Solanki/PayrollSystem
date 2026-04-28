package com.payroll;

import com.payroll.exception.DuplicateEmployeeException;
import com.payroll.exception.EmployeeNotFoundException;
import com.payroll.manager.EmployeeManager;
import com.payroll.manager.PayrollManager;
import com.payroll.model.*;
import com.payroll.util.Logger;

import java.util.Scanner;

/**
 * ╔══════════════════════════════════════════════════════╗
 * ║       MULTITHREADED PAYROLL MANAGEMENT SYSTEM        ║
 * ║                  OOP Project — Java                  ║
 * ╚══════════════════════════════════════════════════════╝
 *
 * Entry point. Runs a console-based menu loop.
 *
 * Concepts demonstrated here:
 *  - Object creation and usage
 *  - Exception handling (try-catch)
 *  - Input validation (NumberFormatException)
 *  - Calling multithreaded payroll run
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static EmployeeManager employeeManager;
    private static PayrollManager payrollManager;

    public static void main(String[] args) {

        // Initialize managers (loads existing data from file)
        employeeManager = new EmployeeManager();
        payrollManager  = new PayrollManager(employeeManager);

        Logger.info("Payroll Management System started.");
        System.out.println("\n  Welcome to the Multithreaded Payroll Management System");

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter choice: ");

            switch (choice) {
                case 1  -> addEmployee();
                case 2  -> viewAllEmployees();
                case 3  -> searchEmployee();
                case 4  -> updateEmployee();
                case 5  -> deleteEmployee();
                case 6  -> runPayroll();
                case 7  -> payrollManager.generateDepartmentReport();
                case 8  -> {
                    employeeManager.saveToFile();
                    System.out.println("\n  Data saved. Goodbye!");
                    Logger.info("Application exited by user.");
                    running = false;
                }
                default -> System.out.println("\n  Invalid choice. Please enter 1-8.");
            }
        }

        scanner.close();
    }

    // ─── Menu Printer ──────────────────────────────────────────────────────────

    private static void printMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("         PAYROLL MANAGEMENT SYSTEM");
        System.out.println("=".repeat(50));
        System.out.println("  1. Add Employee");
        System.out.println("  2. View All Employees");
        System.out.println("  3. Search Employee");
        System.out.println("  4. Update Employee");
        System.out.println("  5. Delete Employee");
        System.out.println("  6. Run Payroll (Multithreaded)");
        System.out.println("  7. Department-wise Payroll Report");
        System.out.println("  8. Save & Exit");
        System.out.println("=".repeat(50));
    }

    // ─── 1. Add Employee ───────────────────────────────────────────────────────

    private static void addEmployee() {
        System.out.println("\n--- ADD EMPLOYEE ---");
        System.out.print("  Employee ID   : ");
        String empId = scanner.nextLine().trim().toUpperCase();

        System.out.print("  Name          : ");
        String name = scanner.nextLine().trim();

        System.out.print("  Department    : ");
        String dept = scanner.nextLine().trim();

        System.out.println("  Employee Type :");
        System.out.println("    1. Full-Time");
        System.out.println("    2. Part-Time");
        System.out.println("    3. Contract");
        int typeChoice = readInt("  Choose type (1-3): ");

        double salary = readDouble("  Base Salary (Rs.): ");

        // Build correct Employee subclass based on type
        Employee emp;
        switch (typeChoice) {
            case 1  -> emp = new FullTimeEmployee(empId, name, dept, salary);
            case 2  -> emp = new PartTimeEmployee(empId, name, dept, salary);
            case 3  -> emp = new ContractEmployee(empId, name, dept, salary);
            default -> {
                System.out.println("  Invalid type. Defaulting to Full-Time.");
                emp = new FullTimeEmployee(empId, name, dept, salary);
            }
        }

        // Handle custom exception
        try {
            employeeManager.addEmployee(emp);
            System.out.println("\n  ✔ Employee added successfully: " + empId);
        } catch (DuplicateEmployeeException e) {
            System.out.println("\n  ✘ ERROR: " + e.getMessage());
        }
    }

    // ─── 2. View All ───────────────────────────────────────────────────────────

    private static void viewAllEmployees() {
        System.out.println("\n--- ALL EMPLOYEES ---");
        employeeManager.displayAllEmployees();
    }

    // ─── 3. Search ─────────────────────────────────────────────────────────────

    private static void searchEmployee() {
        System.out.println("\n--- SEARCH EMPLOYEE ---");
        System.out.println("  1. Search by ID");
        System.out.println("  2. Search by Name");
        int choice = readInt("  Choose (1-2): ");

        if (choice == 1) {
            System.out.print("  Enter Employee ID: ");
            String empId = scanner.nextLine().trim().toUpperCase();
            try {
                Employee emp = employeeManager.getEmployeeById(empId);
                System.out.println("\n  Found:");
                System.out.println("  " + emp);
            } catch (EmployeeNotFoundException e) {
                System.out.println("\n  ✘ " + e.getMessage());
            }

        } else if (choice == 2) {
            System.out.print("  Enter Name to search: ");
            String query = scanner.nextLine().trim();
            employeeManager.searchByName(query);
        } else {
            System.out.println("  Invalid choice.");
        }
    }

    // ─── 4. Update ─────────────────────────────────────────────────────────────

    private static void updateEmployee() {
        System.out.println("\n--- UPDATE EMPLOYEE ---");
        System.out.print("  Enter Employee ID to update: ");
        String empId = scanner.nextLine().trim().toUpperCase();

        try {
            // Show current details first
            Employee current = employeeManager.getEmployeeById(empId);
            System.out.println("  Current: " + current);

            System.out.print("  New Name       [" + current.getName() + "]: ");
            String newName = scanner.nextLine().trim();
            if (newName.isEmpty()) newName = current.getName();

            System.out.print("  New Department [" + current.getDepartment() + "]: ");
            String newDept = scanner.nextLine().trim();
            if (newDept.isEmpty()) newDept = current.getDepartment();

            double newSalary = readDouble("  New Base Salary [" + current.getBaseSalary() + "]: ");

            employeeManager.updateEmployee(empId, newName, newDept, newSalary);
            System.out.println("\n  ✔ Employee updated successfully.");

        } catch (EmployeeNotFoundException e) {
            System.out.println("\n  ✘ " + e.getMessage());
        }
    }

    // ─── 5. Delete ─────────────────────────────────────────────────────────────

    private static void deleteEmployee() {
        System.out.println("\n--- DELETE EMPLOYEE ---");
        System.out.print("  Enter Employee ID to delete: ");
        String empId = scanner.nextLine().trim().toUpperCase();

        System.out.print("  Confirm delete '" + empId + "'? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("yes")) {
            try {
                employeeManager.deleteEmployee(empId);
                System.out.println("\n  ✔ Employee " + empId + " deleted.");
            } catch (EmployeeNotFoundException e) {
                System.out.println("\n  ✘ " + e.getMessage());
            }
        } else {
            System.out.println("  Delete cancelled.");
        }
    }

    // ─── 6. Run Payroll ────────────────────────────────────────────────────────

    private static void runPayroll() {
        System.out.println("\n  Starting multithreaded payroll run...");
        payrollManager.runPayroll();
    }

    // ─── Input Helpers ─────────────────────────────────────────────────────────

    /**
     * Reads an integer from console with validation.
     * Demonstrates: NumberFormatException handling.
     *
     * @param prompt Message to display
     * @return Valid integer input
     */
    private static int readInt(String prompt) {
        while (true) {
            System.out.print("  " + prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  ✘ Invalid input. Please enter a number.");
            }
        }
    }

    /**
     * Reads a double from console with validation.
     * Demonstrates: NumberFormatException handling.
     *
     * @param prompt Message to display
     * @return Valid double input
     */
    private static double readDouble(String prompt) {
        while (true) {
            System.out.print("  " + prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  ✘ Invalid input. Please enter a valid number.");
            }
        }
    }
}
