package com.payroll.model;

/**
 * Represents a Full-Time Employee.
 * Demonstrates: Inheritance from Employee base class.
 */
public class FullTimeEmployee extends Employee {

    public FullTimeEmployee(String empId, String name, String department, double baseSalary) {
        super(empId, name, department, baseSalary);
    }

    @Override
    public String getEmployeeType() {
        return "FULL_TIME";
    }
}
