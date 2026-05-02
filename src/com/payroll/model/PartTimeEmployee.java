package com.payroll.model;

/**
 * Part-Time Employee.
 * Eligible for a reduced HRA (10 %) only — no DA entitlement.
 */
public class PartTimeEmployee extends Employee {

    public PartTimeEmployee(String empId, String name, String department, double baseSalary) {
        super(empId, name, department, baseSalary);
    }

    @Override
    public String getEmployeeType() {
        return "PART_TIME";
    }

    /**
     * Gross = Base + HRA (10%)
     */
    @Override
    public double calculateGrossPay() {
        double base = getBaseSalary();
        return base + (base * 0.10); // Base + reduced HRA
    }
}
