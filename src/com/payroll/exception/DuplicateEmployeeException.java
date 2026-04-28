package com.payroll.exception;

public class DuplicateEmployeeException extends Exception {
    private final String empId;

    public DuplicateEmployeeException(String empId) {
        super("Employee already exists with ID: " + empId);
        this.empId = empId;
    }

    public String getEmpId() {
        return empId;
    }
}
