package com.payroll.model;

/**
 * Permanent / Full-Time Employee.
 * Eligible for full HRA (20 %) and DA (10 %) on top of base salary.
 */
public class FullTimeEmployee extends Employee {

    public FullTimeEmployee(String empId, String name, String department, double baseSalary) {
        super(empId, name, department, baseSalary);
    }

    @Override
    public String getEmployeeType() {
        return "FULL_TIME";
    }

    /**
     * Gross = Base + HRA (20%) + DA (10%)
     */
    @Override
    public double calculateGrossPay() {
        double base = getBaseSalary();
        return base + (base * 0.20) + (base * 0.10); // Base + HRA + DA
    }
}
