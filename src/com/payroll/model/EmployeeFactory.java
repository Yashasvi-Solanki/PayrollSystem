package com.payroll.model;

/**
 * Factory Pattern implementation.
 * Centralises employee instantiation — the rest of the system
 * never calls employee constructors directly.
 */
public class EmployeeFactory {

    private EmployeeFactory() { /* utility class — no instances */ }

    /**
     * Creates the correct Employee subclass based on a type string
     * (typically read from CSV or DB).
     *
     * @param type       One of "FULL_TIME", "PART_TIME", "CONTRACT"
     * @param empId      Employee ID
     * @param name       Full name
     * @param department Department name
     * @param baseSalary Monthly base salary (Rs.)
     * @return           Concrete Employee instance
     * @throws IllegalArgumentException for unknown types
     */
    public static Employee create(String type, String empId, String name,
                                  String department, double baseSalary) {
        return switch (type.toUpperCase().trim()) {
            case "FULL_TIME" -> new FullTimeEmployee(empId, name, department, baseSalary);
            case "PART_TIME" -> new PartTimeEmployee(empId, name, department, baseSalary);
            case "CONTRACT"  -> new ContractEmployee(empId, name, department, baseSalary);
            default          -> throw new IllegalArgumentException("Unknown employee type: " + type);
        };
    }
}
