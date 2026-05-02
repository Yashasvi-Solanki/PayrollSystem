package com.payroll.util;

import com.payroll.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV-based persistence for Employee records.
 * Now delegates object creation to {@link EmployeeFactory}
 * (Factory Pattern) instead of switch-based direct instantiation.
 */
public class FileHandler {

    private static final String DATA_FILE = "data/employees.csv";

    private FileHandler() {}

    public static void saveEmployees(List<Employee> employees) {
        new File("data").mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (Employee emp : employees) {
                bw.write(emp.toCSV());
                bw.newLine();
            }
            Logger.info("Employee data saved to " + DATA_FILE);
        } catch (IOException e) {
            Logger.error("Failed to save employee data", e);
        }
    }

    public static List<Employee> loadEmployees() {
        List<Employee> employees = new ArrayList<>();
        File file = new File(DATA_FILE);

        if (!file.exists()) {
            Logger.info("No existing data file found. Starting fresh.");
            return employees;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Employee emp = parseLine(line);
                if (emp != null) employees.add(emp);
            }
            Logger.info("Loaded " + employees.size() + " employees from " + DATA_FILE);
        } catch (IOException e) {
            Logger.error("Error reading data file", e);
        }

        return employees;
    }

    private static Employee parseLine(String line) {
        try {
            String[] parts   = line.split(",", 5);
            String empId     = parts[0].trim();
            String name      = parts[1].trim();
            String dept      = parts[2].trim();
            String type      = parts[3].trim();
            double salary    = Double.parseDouble(parts[4].trim());
            return EmployeeFactory.create(type, empId, name, dept, salary);
        } catch (Exception e) {
            Logger.error("Skipping malformed CSV line: " + line, e);
            return null;
        }
    }
}
