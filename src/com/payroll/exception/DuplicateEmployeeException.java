package com.payroll.exception;

/**
 * Custom Exception: Thrown when adding an employee with a duplicate ID.
 * Demonstrates: Custom Exception Handling in Java.
 */
public class DuplicateEmployeeException extends Exception {

    private final String empId;

    /**
     * @param empId The duplicate employee ID
     */
    public DuplicateEmployeeException(String empId) {
        super("Employee already exists with ID: " + empId);
        this.empId = empId;
    }

    public String getEmpId() {
        return empId;
    }
}
