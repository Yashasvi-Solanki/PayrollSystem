package com.payroll.service;

import com.payroll.model.Employee;
import com.payroll.model.PayrollRecord;

import java.time.LocalDate;

/**
 * Service Layer — Tax &amp; Deduction Engine.
 *
 * Implements tiered Indian income-tax brackets (simplified annual → monthly),
 * PF contributions, and allowance computation. All business logic lives here;
 * no SQL and no GUI code.
 *
 * Tax Slabs (Annual Gross, simplified):
 *   ≤ 2,50,000          →  0 %
 *   2,50,001 – 5,00,000 →  5 %
 *   5,00,001 – 7,50,000 → 10 %
 *   7,50,001 – 10,00,00 → 15 %
 *   > 10,00,000         → 20 %
 *
 * PF applicability:
 *   FULL_TIME → 12 % of basic (employee share)
 *   PART_TIME →  6 % of basic (partial)
 *   CONTRACT  →  0 % (not applicable)
 */
public class TaxCalculationService {

    // ── Allowance rates ──────────────────────────────────────────────────────
    private static final double HRA_FULL    = 0.20;  // 20% for full-time
    private static final double HRA_PART    = 0.10;  // 10% for part-time
    private static final double DA_FULL     = 0.10;  // 10% DA for full-time
    private static final double PF_FULL     = 0.12;
    private static final double PF_PART     = 0.06;

    /**
     * Computes a fully broken-down {@link PayrollRecord} for the given employee.
     */
    public PayrollRecord calculate(Employee employee) {
        double base  = employee.getBaseSalary();
        String type  = employee.getEmployeeType();

        // ── Allowances (type-specific) ────────────────────────────────────
        double hra = 0, da = 0;
        switch (type) {
            case "FULL_TIME" -> { hra = base * HRA_FULL; da = base * DA_FULL; }
            case "PART_TIME" -> { hra = base * HRA_PART; }
            case "CONTRACT"  -> { /* no allowances */ }
        }
        double gross = base + hra + da;

        // ── Provident Fund ────────────────────────────────────────────────
        double pf = switch (type) {
            case "FULL_TIME" -> base * PF_FULL;
            case "PART_TIME" -> base * PF_PART;
            default          -> 0.0;
        };

        // ── Tiered Income Tax (monthly portion of annual slab) ───────────
        double annualGross = gross * 12;
        double annualTax;

        if      (annualGross <= 250_000)  annualTax = 0;
        else if (annualGross <= 500_000)  annualTax = (annualGross - 250_000) * 0.05;
        else if (annualGross <= 750_000)  annualTax = 12_500 + (annualGross - 500_000) * 0.10;
        else if (annualGross <= 1_000_000) annualTax = 37_500 + (annualGross - 750_000) * 0.15;
        else                               annualTax = 75_000 + (annualGross - 1_000_000) * 0.20;

        double monthlyTax    = annualTax / 12.0;
        double totalDeduct   = pf + monthlyTax;
        double net           = gross - totalDeduct;

        return new PayrollRecord(
                employee.getEmpId(),
                employee.getName(),
                employee.getDepartment(),
                type,
                base, hra, da, gross,
                pf, monthlyTax, totalDeduct,
                net,
                LocalDate.now()
        );
    }
}
