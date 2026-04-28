package com.payroll.thread;

import com.payroll.util.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A dedicated thread that writes the payroll run summary to a file.
 * Runs AFTER all PayrollWorker threads have completed.
 *
 * Demonstrates:
 *  - Thread class (extends Thread)
 *  - Separate I/O thread pattern (non-blocking main thread)
 */
public class FileWriterThread extends Thread {

    private static final String PAYROLL_LOG = "data/payroll_log.txt";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final int totalEmployees;
    private final double totalPayroll;

    /**
     * @param totalEmployees Number of employees processed
     * @param totalPayroll   Sum of all net salaries
     */
    public FileWriterThread(int totalEmployees, double totalPayroll) {
        super("FileWriter-Thread");
        this.totalEmployees = totalEmployees;
        this.totalPayroll   = totalPayroll;
    }

    /**
     * Appends a summary footer to the payroll log.
     * Demonstrates: Thread.run() override via extends Thread
     */
    @Override
    public void run() {
        String timestamp = LocalDateTime.now().format(FORMATTER);

        try (PrintWriter pw = new PrintWriter(new FileWriter(PAYROLL_LOG, true))) {
            pw.println("=".repeat(90));
            pw.println("PAYROLL RUN SUMMARY");
            pw.println("Run Date      : " + timestamp);
            pw.println("Total Employees Processed : " + totalEmployees);
            pw.printf("Total Payroll Cost        : Rs. %.2f%n", totalPayroll);
            pw.println("=".repeat(90));
            pw.println();
            Logger.info("Payroll summary written by " + getName());
        } catch (IOException e) {
            Logger.error("FileWriterThread failed to write summary", e);
        }
    }
}
