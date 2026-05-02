package com.payroll.manager;

import com.payroll.model.Employee;
import com.payroll.model.PayrollRecord;
import com.payroll.service.PayrollProcessingEngine;
import com.payroll.thread.FileWriterThread;
import com.payroll.util.Logger;

import java.util.List;
import java.util.function.Consumer;

/**
 * Thin orchestrator that bridges the GUI controller to the
 * {@link PayrollProcessingEngine} (Service Layer).
 *
 * Controllers never instantiate engine or threading objects directly.
 */
public class PayrollManager {

    private final EmployeeManager          employeeManager;
    private final PayrollProcessingEngine  engine;

    public PayrollManager(EmployeeManager employeeManager) {
        this.employeeManager = employeeManager;
        this.engine          = new PayrollProcessingEngine();
    }

    /**
     * Kicks off a batch payroll run.
     *
     * @param progressCallback receives integer 0–100 as each employee is processed
     * @return the completed list of {@link PayrollRecord}s
     */
    public List<PayrollRecord> runPayroll(Consumer<Integer> progressCallback)
            throws InterruptedException {

        List<Employee> employees = employeeManager.getAllEmployees();

        if (employees.isEmpty()) {
            Logger.info("No employees to process.");
            return List.of();
        }

        Logger.info("Payroll run started for " + employees.size() + " employees.");
        List<PayrollRecord> records = engine.runBatchPayroll(employees, progressCallback);

        double totalNet = records.stream().mapToDouble(PayrollRecord::getNetSalary).sum();

        // Background thread writes the summary log
        FileWriterThread summaryThread = new FileWriterThread(records.size(), totalNet);
        summaryThread.start();
        try { summaryThread.join(); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Logger.info("Payroll complete — " + records.size() + " records, total Rs."
                + String.format("%,.2f", totalNet));
        return records;
    }

    /** Department-wise breakdown — used by the Reports tab. */
    public void generateDepartmentReport() {
        List<Employee> employees = employeeManager.getAllEmployees();
        if (employees.isEmpty()) { System.out.println("  No data."); return; }

        java.util.Map<String, Double> totals = new java.util.LinkedHashMap<>();
        java.util.Map<String, Integer> counts = new java.util.LinkedHashMap<>();

        for (Employee e : employees) {
            double g   = e.calculateGrossPay();
            double pf  = switch (e.getEmployeeType()) {
                case "FULL_TIME" -> e.getBaseSalary() * 0.12;
                case "PART_TIME" -> e.getBaseSalary() * 0.06;
                default          -> 0.0;
            };
            double net = g - pf;
            totals.merge(e.getDepartment(), net, Double::sum);
            counts.merge(e.getDepartment(), 1,   Integer::sum);
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("  DEPARTMENT-WISE PAYROLL REPORT");
        System.out.println("=".repeat(60));
        System.out.printf("%-20s | %-10s | %s%n", "Department", "Employees", "Est. Net Payroll");
        System.out.println("-".repeat(60));
        totals.forEach((dept, total) ->
                System.out.printf("%-20s | %-10d | Rs. %,.2f%n", dept, counts.get(dept), total));
        System.out.println("=".repeat(60));
    }
}
