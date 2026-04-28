package com.payroll.manager;

import com.payroll.model.Employee;
import com.payroll.thread.FileWriterThread;
import com.payroll.thread.PayrollWorker;
import com.payroll.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class PayrollManager {
    private static final int THREAD_POOL_SIZE = 5;
    private final EmployeeManager employeeManager;

    public PayrollManager(EmployeeManager employeeManager) {
        this.employeeManager = employeeManager;
    }

    public void runPayroll() {
        List<Employee> employees = employeeManager.getAllEmployees();

        if (employees.isEmpty()) {
            System.out.println("\n  No employees to process. Add employees first.");
            return;
        }

        System.out.println("\n============================================================");
        System.out.println("  PAYROLL PROCESSING STARTED");
        System.out.println("  Employees: " + employees.size() + " | Thread Pool Size: " + THREAD_POOL_SIZE);
        System.out.println("============================================================");

        PayrollWorker.resetCounter();
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        List<Future<?>> futures = new ArrayList<>();

        for (Employee emp : employees) {
            futures.add(executor.submit(new PayrollWorker(emp)));
        }

        executor.shutdown();

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

        double totalPayroll = calculateTotalPayroll(employees);

        FileWriterThread summaryThread = new FileWriterThread(employees.size(), totalPayroll);
        summaryThread.start();

        try {
            summaryThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Summary thread was interrupted", e);
        }

        printPayrollSummary(employees.size(), totalPayroll);
    }

    private double calculateTotalPayroll(List<Employee> employees) {
        double total = 0;
        for (Employee emp : employees) {
            double base  = emp.getBaseSalary();
            double gross = base + (base * 0.20) + (base * 0.10);
            double net   = gross - (base * 0.12) - (gross * 0.10);
            total += net;
        }
        return total;
    }

    private void printPayrollSummary(int totalEmployees, double totalPayroll) {
        System.out.println("\n============================================================");
        System.out.println("  PAYROLL RUN COMPLETE");
        System.out.printf("  Employees Processed : %d%n", totalEmployees);
        System.out.printf("  Total Payroll Cost  : Rs. %.2f%n", totalPayroll);
        System.out.println("  Payroll log saved   : data/payroll_log.txt");
        System.out.println("  Payslips saved      : payslips/");
        System.out.println("============================================================");
    }

    public void generateDepartmentReport() {
        List<Employee> employees = employeeManager.getAllEmployees();
        if (employees.isEmpty()) {
            System.out.println("  No data for report.");
            return;
        }

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

        System.out.println("\n============================================================");
        System.out.println("  DEPARTMENT-WISE PAYROLL REPORT");
        System.out.println("============================================================");
        System.out.printf("%-20s | %-10s | %s%n", "Department", "Employees", "Total Net Payroll");
        System.out.println("------------------------------------------------------------");

        for (String dept : deptTotals.keySet()) {
            System.out.printf("%-20s | %-10d | Rs. %.2f%n", dept, deptCounts.get(dept), deptTotals.get(dept));
        }
        System.out.println("============================================================");
    }
}
