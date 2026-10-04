package com.seep.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Very small file-based logger. Demonstrates File Handling: every call
 * appends a timestamped line to logs/app.log using try-with-resources so
 * the file handle is always closed, and any IOException is caught so a
 * logging failure never crashes the application.
 */
public final class AppLogger {

    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE = LOG_DIR + "/app.log";
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private AppLogger() {
    }

    public static void info(String message) {
        write("INFO", message);
    }

    public static void warn(String message) {
        write("WARN", message);
    }

    public static void error(String message, Throwable t) {
        write("ERROR", message + (t != null ? " | " + t.getMessage() : ""));
    }

    private static synchronized void write(String level, String message) {
        java.io.File dir = new java.io.File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String line = String.format("[%s] %-5s %s", LocalDateTime.now().format(FORMAT), level, message);

        // Also echo to console so it's visible while running inside VS Code.
        System.out.println(line);

        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(line);
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
}
