package com.payroll.manager;

import com.payroll.exception.DuplicateEmployeeException;
import com.payroll.exception.EmployeeNotFoundException;
import com.payroll.model.Employee;
import com.payroll.util.AuditLogger;
import com.payroll.util.FileHandler;
import com.payroll.util.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the in-memory employee roster.
 *
 * Thread-safety:
 *   - {@link ConcurrentHashMap} for O(1) ID lookups under concurrent access.
 *   - All mutating methods are {@code synchronized} to keep the list and map
 *     consistent with each other.
 */
public class EmployeeManager {

    /** Ordered list for table display */
    private final List<Employee> employeeList;
    /** Fast lookup by ID */
    private final Map<String, Employee> employeeMap;

    public EmployeeManager() {
        this.employeeList = new ArrayList<>();
        this.employeeMap  = new ConcurrentHashMap<>();

        for (Employee emp : FileHandler.loadEmployees()) {
            employeeList.add(emp);
            employeeMap.put(emp.getEmpId(), emp);
        }
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    public synchronized void addEmployee(Employee employee) throws DuplicateEmployeeException {
        if (employeeMap.containsKey(employee.getEmpId())) {
            throw new DuplicateEmployeeException(employee.getEmpId());
        }
        employeeList.add(employee);
        employeeMap.put(employee.getEmpId(), employee);
        Logger.info("Added employee: " + employee.getEmpId() + " — " + employee.getName());
        AuditLogger.log("ADD_EMPLOYEE", "USER",
                "Added " + employee.getEmpId() + " (" + employee.getEmployeeType() + ")");
    }

    public Employee getEmployeeById(String empId) throws EmployeeNotFoundException {
        Employee emp = employeeMap.get(empId);
        if (emp == null) throw new EmployeeNotFoundException(empId);
        return emp;
    }

    public synchronized void updateEmployee(String empId, String newName,
                                            String newDept, double newSalary)
            throws EmployeeNotFoundException {
        Employee emp = getEmployeeById(empId);
        double oldSalary = emp.getBaseSalary();
        emp.setName(newName);
        emp.setDepartment(newDept);
        emp.setBaseSalary(newSalary);
        Logger.info("Updated employee: " + empId);
        AuditLogger.log("UPDATE_SALARY", "USER",
                "Employee " + empId + " salary changed from Rs." + oldSalary + " to Rs." + newSalary);
    }

    public synchronized void deleteEmployee(String empId) throws EmployeeNotFoundException {
        Employee emp = getEmployeeById(empId);
        employeeList.remove(emp);
        employeeMap.remove(empId);
        Logger.info("Deleted employee: " + empId);
        AuditLogger.log("DELETE_EMPLOYEE", "USER", "Removed " + empId + " — " + emp.getName());
    }

    public void saveToFile() {
        FileHandler.saveEmployees(employeeList);
    }

    // ── Queries ──────────────────────────────────────────────────────────────

    public List<Employee> getAllEmployees() {
        return Collections.unmodifiableList(employeeList);
    }

    public void searchByName(String query) {
        System.out.printf("%n  Search results for \"%s\":%n", query);
        employeeList.stream()
                .filter(e -> e.getName().toLowerCase().contains(query.toLowerCase()))
                .forEach(System.out::println);
    }

    public int getCount() { return employeeList.size(); }

    /** Total monthly payroll cost (net) across all employees. */
    public double getTotalPayrollCost() {
        return employeeList.stream()
                .mapToDouble(e -> {
                    double g = e.calculateGrossPay();
                    double pf  = switch (e.getEmployeeType()) {
                        case "FULL_TIME" -> e.getBaseSalary() * 0.12;
                        case "PART_TIME" -> e.getBaseSalary() * 0.06;
                        default          -> 0.0;
                    };
                    return g - pf - (g * 0.05); // rough net estimate for dashboard
                })
                .sum();
    }

    public void displayAllEmployees() {
        if (employeeList.isEmpty()) {
            System.out.println("  No employees found.");
            return;
        }
        System.out.println("\n" + "=".repeat(85));
        System.out.printf("%-10s | %-22s | %-16s | %-12s | %s%n",
                "ID", "Name", "Department", "Type", "Base Salary");
        System.out.println("=".repeat(85));
        employeeList.forEach(System.out::println);
        System.out.println("=".repeat(85));
        System.out.println("  Total: " + employeeList.size() + " employees");
    }
}
