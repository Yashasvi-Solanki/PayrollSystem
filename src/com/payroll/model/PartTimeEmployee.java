package com.payroll.model;

public class PartTimeEmployee extends Employee {
    public PartTimeEmployee(String empId, String name, String department, double baseSalary) {
        super(empId, name, department, baseSalary);
    }

    @Override
    public String getEmployeeType() {
        return "PART_TIME";
    }
}
