package com.payroll.util;

import com.payroll.model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private static final String DATA_FILE = "data/employees.csv";

    public static void saveEmployees(List<Employee> employees) {
        try (FileWriter fw = new FileWriter(DATA_FILE);
             BufferedWriter bw = new BufferedWriter(fw)) {

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
                Employee emp = parseCSVLine(line);
                if (emp != null) {
                    employees.add(emp);
                }
            }
            Logger.info("Loaded " + employees.size() + " employees from file.");

        } catch (FileNotFoundException e) {
            Logger.error("Data file not found", e);
        } catch (IOException e) {
            Logger.error("Error reading data file", e);
        }

        return employees;
    }

    private static Employee parseCSVLine(String line) {
        try {
            String[] parts = line.split(",");
            String empId      = parts[0].trim();
            String name       = parts[1].trim();
            String department = parts[2].trim();
            String type       = parts[3].trim();
            double baseSalary = Double.parseDouble(parts[4].trim());

            return switch (type) {
                case "FULL_TIME" -> new FullTimeEmployee(empId, name, department, baseSalary);
                case "PART_TIME" -> new PartTimeEmployee(empId, name, department, baseSalary);
                case "CONTRACT"  -> new ContractEmployee(empId, name, department, baseSalary);
                default -> {
                    Logger.info("Unknown employee type in file: " + type);
                    yield null;
                }
            };
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            Logger.error("Malformed CSV line: " + line, e);
            return null;
        }
    }
}
