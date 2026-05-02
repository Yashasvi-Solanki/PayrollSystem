package com.payroll.util;

import com.payroll.model.PayrollRecord;

import java.io.*;
import java.time.format.DateTimeFormatter;

/**
 * Generates a beautifully formatted plain-text payslip for each employee
 * after a payroll run.  Files are saved to the {@code payslips/} directory.
 *
 * Thread-safe: each call opens its own FileWriter; synchronisation is only
 * needed if two threads process the same employee (which the engine prevents).
 */
public class PayslipGenerator {

    private static final String PAYSLIP_DIR = "payslips/";
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    private PayslipGenerator() {}

    public static void generatePayslip(PayrollRecord r) {
        new File(PAYSLIP_DIR).mkdirs();
        String fileName = PAYSLIP_DIR + r.getEmpId() + "_payslip.txt";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {

            String hr = "═".repeat(52);
            String sep = "─".repeat(52);

            bw.write(hr); bw.newLine();
            bw.write(centerText("★  OFFICIAL PAY SLIP  ★", 52)); bw.newLine();
            bw.write(centerText("Period: " + r.getPayDate().format(DATE_FMT), 52)); bw.newLine();
            bw.write(hr); bw.newLine();
            bw.newLine();

            bw.write(row("Employee ID",   r.getEmpId()));
            bw.write(row("Name",          r.getEmpName()));
            bw.write(row("Department",    r.getDepartment()));
            bw.write(row("Employee Type", r.getEmpType()));
            bw.newLine();

            bw.write(sep); bw.newLine();
            bw.write(centerText("E A R N I N G S", 52)); bw.newLine();
            bw.write(sep); bw.newLine();
            bw.write(moneyRow("Basic Salary",      r.getBaseSalary()));
            bw.write(moneyRow("HRA",               r.getHra()));
            bw.write(moneyRow("Dearness Allowance",r.getDa()));
            bw.write(sep); bw.newLine();
            bw.write(moneyRow("GROSS SALARY",      r.getGrossSalary()));
            bw.newLine();

            bw.write(sep); bw.newLine();
            bw.write(centerText("D E D U C T I O N S", 52)); bw.newLine();
            bw.write(sep); bw.newLine();
            bw.write(moneyRow("Provident Fund",    r.getPfDeduction()));
            bw.write(moneyRow("Income Tax (TDS)",  r.getTaxDeduction()));
            bw.write(sep); bw.newLine();
            bw.write(moneyRow("TOTAL DEDUCTIONS",  r.getTotalDeductions()));
            bw.newLine();

            bw.write(hr); bw.newLine();
            bw.write(moneyRow("NET SALARY PAYABLE", r.getNetSalary()));
            bw.write(hr); bw.newLine();
            bw.newLine();
            bw.write(centerText("This is a system-generated payslip.", 52)); bw.newLine();

        } catch (IOException e) {
            Logger.error("Failed to generate payslip for " + r.getEmpId(), e);
        }
    }

    // ── Formatting helpers ───────────────────────────────────────────────────

    private static String row(String label, String value) {
        return String.format("  %-24s : %s%n", label, value);
    }

    private static String moneyRow(String label, double amount) {
        return String.format("  %-24s : Rs. %,12.2f%n", label, amount);
    }

    private static String centerText(String text, int width) {
        int pad = Math.max(0, (width - text.length()) / 2);
        return " ".repeat(pad) + text;
    }
}
