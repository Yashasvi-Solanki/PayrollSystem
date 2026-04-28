package com.payroll.util;

import com.payroll.model.Employee;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PayslipGenerator {
    private static final String PAYSLIP_DIR = "payslips/";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static synchronized void generatePayslip(Employee employee, double grossSalary, double deductions, double netSalary) {
        String fileName = PAYSLIP_DIR + employee.getEmpId() + "_payslip.txt";
        String date = LocalDate.now().format(DATE_FORMATTER);

        double base = employee.getBaseSalary();
        double hra  = base * 0.20;
        double da   = base * 0.10;
        double pf   = base * 0.12;
        double tax  = grossSalary * 0.10;

        try (FileWriter fw = new FileWriter(fileName);
             BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write("========================================"); bw.newLine();
            bw.write("         PAYSLIP - " + date); bw.newLine();
            bw.write("========================================"); bw.newLine();
            bw.write(String.format("%-20s : %s%n", "Employee ID",   employee.getEmpId()));
            bw.write(String.format("%-20s : %s%n", "Name",          employee.getName()));
            bw.write(String.format("%-20s : %s%n", "Department",    employee.getDepartment()));
            bw.write(String.format("%-20s : %s%n", "Employee Type", employee.getEmployeeType()));
            bw.newLine();
            bw.write("----------  EARNINGS  ------------------"); bw.newLine();
            bw.write(String.format("%-20s : Rs. %10.2f%n", "Basic Salary", base));
            bw.write(String.format("%-20s : Rs. %10.2f%n", "HRA (20%)",    hra));
            bw.write(String.format("%-20s : Rs. %10.2f%n", "DA  (10%)",    da));
            bw.write(String.format("%-20s   Rs. %10.2f%n", "GROSS SALARY", grossSalary));
            bw.newLine();
            bw.write("----------  DEDUCTIONS  ----------------"); bw.newLine();
            bw.write(String.format("%-20s : Rs. %10.2f%n", "PF (12% Basic)", pf));
            bw.write(String.format("%-20s : Rs. %10.2f%n", "Income Tax(10%)", tax));
            bw.write(String.format("%-20s   Rs. %10.2f%n", "TOTAL DEDUCTIONS", deductions));
            bw.newLine();
            bw.write("========================================"); bw.newLine();
            bw.write(String.format("%-20s   Rs. %10.2f%n", "NET SALARY", netSalary));
            bw.write("========================================"); bw.newLine();

        } catch (IOException e) {
            Logger.error("Failed to generate payslip for " + employee.getEmpId(), e);
        }
    }
}
