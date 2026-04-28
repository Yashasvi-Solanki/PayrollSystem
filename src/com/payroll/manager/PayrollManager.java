package com.payroll.manager;

import com.payroll.model.Employee;
import com.payroll.thread.FileWriterThread;
import com.payroll.thread.PayrollWorker;
import com.payroll.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Manages payroll processing using a thread pool (ExecutorService).
 *
 * Demonstrates:
 *  - ExecutorService with fixed thread pool
 *  - Submitting Runnable tasks (PayrollWorker) per employee
 *  - Future list to track completion
 *  - awaitTermination() to wait for all threads
 *  - Separate FileWriterThread for summary
 */
public class PayrollManager {

    // Thread pool size — 5 concurrent payroll threads
    private static final int THREAD_POOL_SIZE = 5;

    private final EmployeeManager employeeManager;

    /**
     * @param employeeManager Source of employee list
     */
    public PayrollManager(EmployeeManager employeeManager) {
        this.employeeManager = employeeManager;
    }

    /**
     * Runs payroll for ALL employees concurrently.
     *
     * Steps:
     *  1. Create fixed thread pool
     *  2. Submit one PayrollWorker per employee
     *  3. Wait for all threads to finish
     *  4. Start FileWriterThread for summary
     *  5. Display payroll report
     */
    public void runPayroll() {
        List<Employee> employees = employeeManager.getAllEmployees();

        if (employees.isEmpty()) {
            System.out.println("\n  No employees to process. Add employees first.");
            return;
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("  PAYROLL PROCESSING STARTED");
        System.out.println("  Employees: " + employees.size()
                + " | Thread Pool Size: " + THREAD_POOL_SIZE);
        System.out.println("=".repeat(60));

        // Reset shared counter
        PayrollWorker.resetCounter();

        // Create a fixed thread pool
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        // Track all futures
        List<Future<?>> futures = new ArrayList<>();

        // Submit one PayrollWorker per employee
        for (Employee emp : employees) {
            Future<?> future = executor.submit(new PayrollWorker(emp));
            futures.add(future);
        }

        // Shutdown executor — no new tasks accepted
        executor.shutdown();

        // Wait for all threads to complete (max 60 seconds)
        try {
            boolean finished = executor.awaitTermination(60, TimeUnit.SECONDS);
            if (finished) {
                System.out.println("\n  All payroll threads completed successfully!");
            } else {
                System.out.println("\n  WARNING: Payroll timed out. Some records may be incomplete.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Payroll processing was interrupted", e);
        }

        // Calculate total payroll for summary
        double totalPayroll = calculateTotalPayroll(employees);

        // Start dedicated FileWriterThread for summary
        FileWriterThread summaryThread = new FileWriterThread(employees.size(), totalPayroll);
        summaryThread.start();

        // Wait for summary thread to finish
        try {
            summaryThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Summary thread was interrupted", e);
        }

        // Display report on console
        printPayrollSummary(employees.size(), totalPayroll);
    }

    /**
     * Computes the sum of all net salaries.
     *
     * @param employees List of employees
     * @return Total payroll amount
     */
    private double calculateTotalPayroll(List<Employee> employees) {
        double total = 0;
        for (Employee emp : employees) {
            double base        = emp.getBaseSalary();
            double hra         = base * 0.20;
            double da          = base * 0.10;
            double gross       = base + hra + da;
            double pf          = base * 0.12;
            double tax         = gross * 0.10;
            double net         = gross - pf - tax;
            total += net;
        }
        return total;
    }

    /**
     * Prints a payroll run summary to the console.
     *
     * @param totalEmployees Employees processed
     * @param totalPayroll   Total net salary payout
     */
    private void printPayrollSummary(int totalEmployees, double totalPayroll) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  PAYROLL RUN COMPLETE");
        System.out.printf("  Employees Processed : %d%n", totalEmployees);
        System.out.printf("  Total Payroll Cost  : Rs. %.2f%n", totalPayroll);
        System.out.println("  Payroll log saved   : data/payroll_log.txt");
        System.out.println("  Payslips saved      : payslips/");
        System.out.println("=".repeat(60));
    }

    /**
     * Generates a department-wise payroll breakdown report.
     */
    public void generateDepartmentReport() {
        List<Employee> employees = employeeManager.getAllEmployees();
        if (employees.isEmpty()) {
            System.out.println("  No data for report.");
            return;
        }

        // Use HashMap to group totals by department
        java.util.Map<String, Double> deptTotals = new java.util.HashMap<>();
        java.util.Map<String, Integer> deptCounts = new java.util.HashMap<>();

        for (Employee emp : employees) {
            double base  = emp.getBaseSalary();
            double gross = base + (base * 0.20) + (base * 0.10);
            double net   = gross - (base * 0.12) - (gross * 0.10);

            String dept = emp.getDepartment();
            deptTotals.put(dept, deptTotals.getOrDefault(dept, 0.0) + net);
            deptCounts.put(dept, deptCounts.getOrDefault(dept, 0) + 1);
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("  DEPARTMENT-WISE PAYROLL REPORT");
        System.out.println("=".repeat(60));
        System.out.printf("%-20s | %-10s | %s%n", "Department", "Employees", "Total Net Payroll");
        System.out.println("-".repeat(60));

        for (String dept : deptTotals.keySet()) {
            System.out.printf("%-20s | %-10d | Rs. %.2f%n",
                    dept, deptCounts.get(dept), deptTotals.get(dept));
        }
        System.out.println("=".repeat(60));
    }
}
