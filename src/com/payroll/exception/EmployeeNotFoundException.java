package com.payroll.exception;

/**
 * Custom Exception: Thrown when an employee ID is not found in the system.
 * Demonstrates: Custom Exception Handling in Java.
 */
public class EmployeeNotFoundException extends Exception {

    private final String empId;

    /**
     * @param empId The employee ID that was not found
     */
    public EmployeeNotFoundException(String empId) {
        super("Employee not found with ID: " + empId);
        this.empId = empId;
    }

    public String getEmpId() {
        return empId;
    }
}
