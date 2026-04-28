package com.payroll.thread;

import com.payroll.model.Employee;
import com.payroll.util.Logger;
import com.payroll.util.PayslipGenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

public class PayrollWorker implements Runnable {
    private static final AtomicInteger processedCount = new AtomicInteger(0);
    private static final String PAYROLL_LOG = "data/payroll_log.txt";
    private final Employee employee;

    public PayrollWorker(Employee employee) {
        this.employee = employee;
    }

    public static void resetCounter() {
        processedCount.set(0);
    }

    public static int getProcessedCount() {
        return processedCount.get();
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        Logger.info("[" + threadName + "] Processing payroll for: " + employee.getName());

        try {
            Thread.sleep(500);

            double base        = employee.getBaseSalary();
            double hra         = base * 0.20;
            double da          = base * 0.10;
            double grossSalary = base + hra + da;
            double pf          = base * 0.12;
            double tax         = grossSalary * 0.10;
            double deductions  = pf + tax;
            double netSalary   = grossSalary - deductions;

            writeToPayrollLog(employee, grossSalary, deductions, netSalary);
            PayslipGenerator.generatePayslip(employee, grossSalary, deductions, netSalary);
            processedCount.incrementAndGet();

            Logger.info("[" + threadName + "] Done: " + employee.getName() + " | Net Salary: Rs. " + String.format("%.2f", netSalary));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Thread interrupted for employee: " + employee.getEmpId(), e);
        }
    }

    private static synchronized void writeToPayrollLog(Employee emp, double gross, double deductions, double net) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PAYROLL_LOG, true))) {
            pw.printf("%-10s | %-20s | %-15s | Gross: %10.2f | Deductions: %8.2f | Net: %10.2f%n",
                    emp.getEmpId(), emp.getName(), emp.getDepartment(), gross, deductions, net);
        } catch (IOException e) {
            Logger.error("Failed to write payroll log for: " + emp.getEmpId(), e);
        }
    }
}
