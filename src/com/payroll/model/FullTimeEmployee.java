package com.payroll.model;

public class FullTimeEmployee extends Employee {
    public FullTimeEmployee(String empId, String name, String department, double baseSalary) {
        super(empId, name, department, baseSalary);
    }

    @Override
    public String getEmployeeType() {
        return "FULL_TIME";
    }
}
