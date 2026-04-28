package com.payroll.model;

public abstract class Employee {
    private String empId;
    private String name;
    private String department;
    private double baseSalary;

    public Employee(String empId, String name, String department, double baseSalary) {
        this.empId = empId;
        this.name = name;
        this.department = department;
        this.baseSalary = baseSalary;
    }

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public double getBaseSalary() { return baseSalary; }
    public void setBaseSalary(double baseSalary) { this.baseSalary = baseSalary; }

    public abstract String getEmployeeType();

    public String toCSV() {
        return empId + "," + name + "," + department + "," + getEmployeeType() + "," + baseSalary;
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-20s | %-15s | %-12s | %.2f",
                empId, name, department, getEmployeeType(), baseSalary);
    }
}
