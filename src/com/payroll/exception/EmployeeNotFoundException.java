package com.payroll.exception;

public class EmployeeNotFoundException extends Exception {
    private final String empId;

    public EmployeeNotFoundException(String empId) {
        super("Employee not found with ID: " + empId);
        this.empId = empId;
    }

    public String getEmpId() {
        return empId;
    }
}
