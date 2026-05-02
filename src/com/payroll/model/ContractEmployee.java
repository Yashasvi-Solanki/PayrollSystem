package com.payroll.model;

/**
 * Contract / Freelance Employee.
 * Paid purely on a flat base rate — no HRA or DA entitlement.
 * PF is also not applicable (handled in TaxCalculationService).
 */
public class ContractEmployee extends Employee {

    public ContractEmployee(String empId, String name, String department, double baseSalary) {
        super(empId, name, department, baseSalary);
    }

    @Override
    public String getEmployeeType() {
        return "CONTRACT";
    }

    /**
     * Gross = Base only (no allowances for contractors).
     */
    @Override
    public double calculateGrossPay() {
        return getBaseSalary(); // No additional allowances
    }
}
