package com.payroll.service;

import com.payroll.model.Employee;
import com.payroll.model.PayrollRecord;
import com.payroll.util.AuditLogger;
import com.payroll.util.Logger;
import com.payroll.util.PayslipGenerator;
import com.payroll.util.PayrollHistoryStore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Payroll Processing Engine — Concurrency Hub.
 *
 * Producer-Consumer pattern:
 *   • The calling thread acts as Producer: it stuffs employees into a
 *     {@link LinkedBlockingQueue}.
 *   • A fixed thread-pool of Consumer workers drains the queue, calculates
 *     tax, writes payslips, and records the results.
 *
 * Thread safety:
 *   • {@link AtomicInteger} for progress counter (lock-free).
 *   • {@link ConcurrentLinkedQueue} to accumulate results safely.
 *   • {@link CountDownLatch} so the engine knows when all workers are done.
 */
public class PayrollProcessingEngine {

    private static final int THREAD_POOL_SIZE = 5;

    private final TaxCalculationService taxService = new TaxCalculationService();

    /**
     * Runs payroll for the given employee list.
     *
     * @param employees       list to process
     * @param progressCallback called with the percentage complete (0–100) after each employee
     * @return list of completed {@link PayrollRecord}s
     */
    public List<PayrollRecord> runBatchPayroll(List<Employee> employees,
                                               Consumer<Integer> progressCallback)
            throws InterruptedException {

        int total = employees.size();
        if (total == 0) return new ArrayList<>();

        // Shared structures
        BlockingQueue<Employee>           jobQueue    = new LinkedBlockingQueue<>(employees);
        ConcurrentLinkedQueue<PayrollRecord> results  = new ConcurrentLinkedQueue<>();
        AtomicInteger                     processed   = new AtomicInteger(0);
        CountDownLatch                    latch       = new CountDownLatch(total);

        ExecutorService pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE,
                r -> {
                    Thread t = new Thread(r);
                    t.setName("PayrollWorker-" + t.getId());
                    t.setDaemon(true);
                    return t;
                });

        // Submit one task per employee (consumer workers)
        for (int i = 0; i < total; i++) {
            pool.submit(() -> {
                Employee emp = null;
                try {
                    emp = jobQueue.poll();
                    if (emp == null) return;

                    String threadName = Thread.currentThread().getName();
                    Logger.info("[" + threadName + "] Calculating pay for: " + emp.getName());

                    // Simulate realistic processing delay
                    Thread.sleep(300);

                    PayrollRecord record = taxService.calculate(emp);
                    results.add(record);

                    // Write payslip
                    PayslipGenerator.generatePayslip(record);
                    // Record in history
                    PayrollHistoryStore.getInstance().add(record);

                    int done    = processed.incrementAndGet();
                    int percent = (int) ((done / (double) total) * 100);
                    if (progressCallback != null) progressCallback.accept(percent);

                    Logger.info("[" + threadName + "] Done: " + emp.getName()
                            + " | Net: Rs. " + String.format("%,.2f", record.getNetSalary()));

                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    if (emp != null)
                        Logger.error("Thread interrupted for " + emp.getEmpId(), ie);
                } finally {
                    latch.countDown();
                }
            });
        }

        pool.shutdown();
        latch.await(120, TimeUnit.SECONDS);

        List<PayrollRecord> finalList = new ArrayList<>(results);
        double totalNet = finalList.stream().mapToDouble(PayrollRecord::getNetSalary).sum();
        AuditLogger.log("PAYROLL_RUN", "SYSTEM",
                "Batch complete — " + total + " employees, total net Rs. " + String.format("%,.2f", totalNet));

        return finalList;
    }
}
