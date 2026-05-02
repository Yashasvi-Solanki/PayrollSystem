package com.payroll.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Dedicated Audit Logger — separate from the system Logger.
 *
 * Records every security-sensitive action (logins, salary changes, payroll
 * runs) to {@code logs/audit.log} in an append-only fashion.
 *
 * Thread-safe via synchronisation on the class monitor.
 */
public class AuditLogger {

    private static final String AUDIT_FILE = "logs/audit.log";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private AuditLogger() {}

    /**
     * Appends an audit entry.
     *
     * @param action  e.g. "LOGIN", "ADD_EMPLOYEE", "UPDATE_SALARY", "PAYROLL_RUN"
     * @param actor   username or "SYSTEM"
     * @param details free-form description
     */
    public static synchronized void log(String action, String actor, String details) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String entry = String.format("[%s] [AUDIT] [%-16s] actor=%-12s | %s",
                timestamp, action, actor, details);

        System.out.println(entry);

        try (PrintWriter pw = new PrintWriter(new FileWriter(AUDIT_FILE, true))) {
            pw.println(entry);
        } catch (IOException e) {
            System.err.println("AuditLogger: failed to write — " + e.getMessage());
        }
    }
}
