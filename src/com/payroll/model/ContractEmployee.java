package com.payroll.model;

public class ContractEmployee extends Employee {
    public ContractEmployee(String empId, String name, String department, double baseSalary) {
        super(empId, name, department, baseSalary);
    }

    @Override
    public String getEmployeeType() {
        return "CONTRACT";
    }
}
