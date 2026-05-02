package com.payroll.model;

import java.time.LocalDate;

/**
 * Immutable DTO representing a single employee's completed payroll record.
 * Carried between layers without exposing DB internals.
 */
public final class PayrollRecord {

    private final String      empId;
    private final String      empName;
    private final String      department;
    private final String      empType;
    private final double      baseSalary;
    private final double      hra;
    private final double      da;
    private final double      grossSalary;
    private final double      pfDeduction;
    private final double      taxDeduction;
    private final double      totalDeductions;
    private final double      netSalary;
    private final LocalDate   payDate;

    public PayrollRecord(String empId, String empName, String department, String empType,
                         double baseSalary, double hra, double da, double grossSalary,
                         double pfDeduction, double taxDeduction, double totalDeductions,
                         double netSalary, LocalDate payDate) {
        this.empId           = empId;
        this.empName         = empName;
        this.department      = department;
        this.empType         = empType;
        this.baseSalary      = baseSalary;
        this.hra             = hra;
        this.da              = da;
        this.grossSalary     = grossSalary;
        this.pfDeduction     = pfDeduction;
        this.taxDeduction    = taxDeduction;
        this.totalDeductions = totalDeductions;
        this.netSalary       = netSalary;
        this.payDate         = payDate;
    }

    public String    getEmpId()           { return empId; }
    public String    getEmpName()         { return empName; }
    public String    getDepartment()      { return department; }
    public String    getEmpType()         { return empType; }
    public double    getBaseSalary()      { return baseSalary; }
    public double    getHra()             { return hra; }
    public double    getDa()              { return da; }
    public double    getGrossSalary()     { return grossSalary; }
    public double    getPfDeduction()     { return pfDeduction; }
    public double    getTaxDeduction()    { return taxDeduction; }
    public double    getTotalDeductions() { return totalDeductions; }
    public double    getNetSalary()       { return netSalary; }
    public LocalDate getPayDate()         { return payDate; }
}
