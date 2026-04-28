package com.payroll.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final String LOG_FILE = "logs/error.log";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static synchronized void info(String message) {
        log("INFO", message);
    }

    public static synchronized void error(String message, Exception exception) {
        log("ERROR", message + " | Exception: " + exception.getMessage());
    }

    private static void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String entry = "[" + timestamp + "] [" + level + "] " + message;

        System.out.println(entry);

        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(entry);
        } catch (IOException e) {
            System.err.println("Logger failed to write to file: " + e.getMessage());
        }
    }
}
