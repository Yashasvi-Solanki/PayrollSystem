package com.payroll.thread;

import com.payroll.model.Employee;
import com.payroll.model.PayrollRecord;
import com.payroll.service.TaxCalculationService;
import com.payroll.util.AuditLogger;
import com.payroll.util.Logger;
import com.payroll.util.PayslipGenerator;
import com.payroll.util.PayrollHistoryStore;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Runnable worker that processes a single employee's payroll.
 * Used by the PayrollProcessingEngine's thread pool.
 */
public class PayrollWorker implements Runnable {

    private static final AtomicInteger processedCount = new AtomicInteger(0);
    private final Employee employee;
    private final TaxCalculationService taxService = new TaxCalculationService();

    public PayrollWorker(Employee employee) {
        this.employee = employee;
    }

    public static void resetCounter() { processedCount.set(0); }
    public static int  getProcessedCount() { return processedCount.get(); }

    @Override
    public void run() {
        String thread = Thread.currentThread().getName();
        Logger.info("[" + thread + "] Processing: " + employee.getName());

        try {
            Thread.sleep(300);
            PayrollRecord record = taxService.calculate(employee);
            PayslipGenerator.generatePayslip(record);
            PayrollHistoryStore.getInstance().add(record);
            processedCount.incrementAndGet();
            Logger.info("[" + thread + "] Done: " + employee.getName()
                    + " | Net Rs." + String.format("%,.2f", record.getNetSalary()));
            AuditLogger.log("PAY_CALCULATED", thread, employee.getEmpId()
                    + " net Rs." + String.format("%,.2f", record.getNetSalary()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Interrupted for " + employee.getEmpId(), e);
        }
    }
}
