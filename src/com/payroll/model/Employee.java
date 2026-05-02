package com.payroll.model;

/**
 * Abstract base class for all employee types.
 * Demonstrates polymorphism — each subclass provides its own
 * calculatePay() implementation reflecting different compensation rules.
 */
public abstract class Employee {

    private String empId;
    private String name;
    private String department;
    private double baseSalary;

    public Employee(String empId, String name, String department, double baseSalary) {
        this.empId      = empId;
        this.name       = name;
        this.department = department;
        this.baseSalary = baseSalary;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public String getEmpId()               { return empId; }
    public void   setEmpId(String id)      { this.empId = id; }

    public String getName()                { return name; }
    public void   setName(String name)     { this.name = name; }

    public String getDepartment()          { return department; }
    public void   setDepartment(String d)  { this.department = d; }

    public double getBaseSalary()          { return baseSalary; }
    public void   setBaseSalary(double s)  { this.baseSalary = s; }

    // ── Abstract Contract ────────────────────────────────────────────────────

    /** Returns the discriminator string used in CSV / DB. */
    public abstract String getEmployeeType();

    /**
     * Returns the GROSS salary before any deductions.
     * Subclasses override this to apply type-specific allowances
     * (e.g. full HRA for permanent staff, none for contractors).
     */
    public abstract double calculateGrossPay();

    // ── Serialisation ────────────────────────────────────────────────────────

    public String toCSV() {
        return empId + "," + name + "," + department + "," + getEmployeeType() + "," + baseSalary;
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-22s | %-16s | %-12s | %,10.2f",
                empId, name, department, getEmployeeType(), baseSalary);
    }
}
