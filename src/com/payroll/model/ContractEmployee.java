package com.payroll.model;

/**
 * Represents a Contract Employee.
 * Demonstrates: Inheritance from Employee base class.
 */
public class ContractEmployee extends Employee {

    public ContractEmployee(String empId, String name, String department, double baseSalary) {
        super(empId, name, department, baseSalary);
    }

    @Override
    public String getEmployeeType() {
        return "CONTRACT";
    }
}
