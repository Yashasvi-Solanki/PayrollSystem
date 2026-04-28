package com.payroll.thread;

import com.payroll.model.Employee;
import com.payroll.util.Logger;
import com.payroll.util.PayslipGenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Runnable task representing payroll calculation for ONE employee.
 *
 * Each instance runs in its own thread (submitted via ExecutorService).
 * Demonstrates:
 *  - Runnable interface implementation
 *  - Thread.sleep() for simulated delay
 *  - synchronized file writing to prevent data corruption
 *  - AtomicInteger for thread-safe counter
 */
public class PayrollWorker implements Runnable {

    // Shared atomic counter across all threads
    private static final AtomicInteger processedCount = new AtomicInteger(0);

    // Shared payroll log file path
    private static final String PAYROLL_LOG = "data/payroll_log.txt";

    private final Employee employee;

    /**
     * @param employee The employee whose salary will be processed
     */
    public PayrollWorker(Employee employee) {
        this.employee = employee;
    }

    /**
     * Resets the processed counter before a new payroll run.
     */
    public static void resetCounter() {
        processedCount.set(0);
    }

    /**
     * Gets how many employees have been processed so far.
     *
     * @return count of processed employees
     */
    public static int getProcessedCount() {
        return processedCount.get();
    }

    /**
     * Core thread execution: calculates salary and writes results.
     * Demonstrates: run(), sleep(), synchronized block
     */
    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        Logger.info("[" + threadName + "] Processing payroll for: " + employee.getName());

        try {
            // Simulate real-world processing delay
            Thread.sleep(500);

            // ─── Salary Calculation ───────────────────────────────────────────
            double base        = employee.getBaseSalary();
            double hra         = base * 0.20;          // House Rent Allowance
            double da          = base * 0.10;           // Dearness Allowance
            double grossSalary = base + hra + da;
            double pf          = base * 0.12;          // Provident Fund
            double tax         = grossSalary * 0.10;   // Income Tax
            double deductions  = pf + tax;
            double netSalary   = grossSalary - deductions;
            // ─────────────────────────────────────────────────────────────────

            // Write to shared log file — synchronized to prevent corruption
            writeToPayrollLog(employee, grossSalary, deductions, netSalary);

            // Generate individual payslip
            PayslipGenerator.generatePayslip(employee, grossSalary, deductions, netSalary);

            // Increment shared counter thread-safely
            processedCount.incrementAndGet();

            Logger.info("[" + threadName + "] Done: " + employee.getName()
                    + " | Net Salary: Rs. " + String.format("%.2f", netSalary));

        } catch (InterruptedException e) {
            // Properly handle thread interruption
            Thread.currentThread().interrupt();
            Logger.error("Thread interrupted for employee: " + employee.getEmpId(), e);
        }
    }

    /**
     * Writes a payroll result line to the shared payroll_log.txt.
     * synchronized ensures only one thread writes at a time.
     *
     * @param emp         Employee processed
     * @param gross       Gross salary
     * @param deductions  Total deductions
     * @param net         Net salary
     */
    private static synchronized void writeToPayrollLog(Employee emp,
                                                        double gross,
                                                        double deductions,
                                                        double net) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PAYROLL_LOG, true))) {
            pw.printf("%-10s | %-20s | %-15s | Gross: %10.2f | Deductions: %8.2f | Net: %10.2f%n",
                    emp.getEmpId(), emp.getName(), emp.getDepartment(), gross, deductions, net);
        } catch (IOException e) {
            Logger.error("Failed to write payroll log for: " + emp.getEmpId(), e);
        }
    }
}
