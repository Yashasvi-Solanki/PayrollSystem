package com.payroll.model;

/**
 * Base class representing an Employee.
 * Demonstrates OOP Concepts: Encapsulation, Abstraction
 */
public abstract class Employee {

    // Private fields — Encapsulation
    private String empId;
    private String name;
    private String department;
    private double baseSalary;

    /**
     * Constructor to initialize Employee fields.
     *
     * @param empId      Unique Employee ID
     * @param name       Employee Name
     * @param department Department name
     * @param baseSalary Monthly base salary
     */
    public Employee(String empId, String name, String department, double baseSalary) {
        this.empId = empId;
        this.name = name;
        this.department = department;
        this.baseSalary = baseSalary;
    }

    // ─── Getters & Setters ─────────────────────────────────────────────────────

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public double getBaseSalary() { return baseSalary; }
    public void setBaseSalary(double baseSalary) { this.baseSalary = baseSalary; }

    /**
     * Abstract method — each subclass defines its own salary type label.
     * Demonstrates: Abstraction & Polymorphism
     *
     * @return Employee type string
     */
    public abstract String getEmployeeType();

    /**
     * Returns CSV representation for file storage.
     *
     * @return Comma-separated string of employee data
     */
    public String toCSV() {
        return empId + "," + name + "," + department + "," + getEmployeeType() + "," + baseSalary;
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-20s | %-15s | %-12s | %.2f",
                empId, name, department, getEmployeeType(), baseSalary);
    }
}
