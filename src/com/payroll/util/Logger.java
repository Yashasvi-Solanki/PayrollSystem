package com.payroll.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for logging errors and system events to a log file.
 * Uses synchronized writing to be thread-safe.
 */
public class Logger {

    private static final String LOG_FILE = "logs/error.log";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Logs a message with INFO level.
     *
     * @param message The message to log
     */
    public static synchronized void info(String message) {
        log("INFO", message);
    }

    /**
     * Logs a message with ERROR level.
     *
     * @param message   The error message
     * @param exception The exception that occurred
     */
    public static synchronized void error(String message, Exception exception) {
        log("ERROR", message + " | Exception: " + exception.getMessage());
    }

    /**
     * Core logging method — appends a timestamped entry to error.log.
     *
     * @param level   Log level (INFO/ERROR)
     * @param message Message body
     */
    private static void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String entry = "[" + timestamp + "] [" + level + "] " + message;

        // Print to console
        System.out.println(entry);

        // Append to log file
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(entry);
        } catch (IOException e) {
            System.err.println("Logger failed to write to file: " + e.getMessage());
        }
    }
}
