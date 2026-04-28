package com.payroll.model;

/**
 * Represents a Part-Time Employee.
 * Demonstrates: Inheritance from Employee base class.
 */
public class PartTimeEmployee extends Employee {

    public PartTimeEmployee(String empId, String name, String department, double baseSalary) {
        super(empId, name, department, baseSalary);
    }

    @Override
    public String getEmployeeType() {
        return "PART_TIME";
    }
}
