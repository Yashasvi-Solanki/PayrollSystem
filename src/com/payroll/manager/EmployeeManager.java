package com.payroll.manager;

import com.payroll.exception.DuplicateEmployeeException;
import com.payroll.exception.EmployeeNotFoundException;
import com.payroll.model.Employee;
import com.payroll.util.FileHandler;
import com.payroll.util.Logger;

import java.util.*;

/**
 * Manages all employee CRUD operations.
 *
 * Demonstrates:
 *  - ArrayList   → ordered storage of all employees
 *  - HashMap     → O(1) lookup by Employee ID
 *  - Collections.synchronizedMap() → thread-safe map
 *  - Custom Exception throwing
 */
public class EmployeeManager {

    // ArrayList for ordered iteration
    private final List<Employee> employeeList;

    // HashMap for fast ID-based lookup — wrapped in synchronizedMap for thread safety
    private final Map<String, Employee> employeeMap;

    /**
     * Constructor — loads existing data from file on startup.
     */
    public EmployeeManager() {
        this.employeeList = new ArrayList<>();
        this.employeeMap  = Collections.synchronizedMap(new HashMap<>());

        // Load persisted data
        List<Employee> loaded = FileHandler.loadEmployees();
        for (Employee emp : loaded) {
            employeeList.add(emp);
            employeeMap.put(emp.getEmpId(), emp);
        }
    }

    // ─── Add Employee ──────────────────────────────────────────────────────────

    /**
     * Adds a new employee to the system.
     *
     * @param employee Employee to add
     * @throws DuplicateEmployeeException if employee ID already exists
     */
    public void addEmployee(Employee employee) throws DuplicateEmployeeException {
        if (employeeMap.containsKey(employee.getEmpId())) {
            throw new DuplicateEmployeeException(employee.getEmpId());
        }
        employeeList.add(employee);
        employeeMap.put(employee.getEmpId(), employee);
        Logger.info("Added employee: " + employee.getEmpId() + " - " + employee.getName());
    }

    // ─── Get Employee by ID ────────────────────────────────────────────────────

    /**
     * Retrieves an employee by their ID using HashMap (O(1) lookup).
     *
     * @param empId Employee ID
     * @return Employee object
     * @throws EmployeeNotFoundException if not found
     */
    public Employee getEmployeeById(String empId) throws EmployeeNotFoundException {
        Employee emp = employeeMap.get(empId);
        if (emp == null) {
            throw new EmployeeNotFoundException(empId);
        }
        return emp;
    }

    // ─── Update Employee ───────────────────────────────────────────────────────

    /**
     * Updates the name, department, and base salary of an existing employee.
     *
     * @param empId       Employee ID to update
     * @param newName     Updated name
     * @param newDept     Updated department
     * @param newSalary   Updated base salary
     * @throws EmployeeNotFoundException if ID not found
     */
    public void updateEmployee(String empId, String newName, String newDept, double newSalary)
            throws EmployeeNotFoundException {
        Employee emp = getEmployeeById(empId);
        emp.setName(newName);
        emp.setDepartment(newDept);
        emp.setBaseSalary(newSalary);
        Logger.info("Updated employee: " + empId);
    }

    // ─── Delete Employee ───────────────────────────────────────────────────────

    /**
     * Removes an employee from both the list and map.
     *
     * @param empId Employee ID to delete
     * @throws EmployeeNotFoundException if ID not found
     */
    public void deleteEmployee(String empId) throws EmployeeNotFoundException {
        Employee emp = getEmployeeById(empId);
        employeeList.remove(emp);
        employeeMap.remove(empId);
        Logger.info("Deleted employee: " + empId);
    }

    // ─── Display All Employees ─────────────────────────────────────────────────

    /**
     * Prints all employees in a formatted table.
     */
    public void displayAllEmployees() {
        if (employeeList.isEmpty()) {
            System.out.println("\n  No employees found.");
            return;
        }
        System.out.println("\n" + "=".repeat(85));
        System.out.printf("%-10s | %-20s | %-15s | %-12s | %s%n",
                "ID", "Name", "Department", "Type", "Base Salary");
        System.out.println("=".repeat(85));
        for (Employee emp : employeeList) {
            System.out.println(emp);
        }
        System.out.println("=".repeat(85));
        System.out.println("  Total Employees: " + employeeList.size());
    }

    // ─── Search by Name ────────────────────────────────────────────────────────

    /**
     * Searches for employees whose name contains the query (case-insensitive).
     *
     * @param query Name or partial name to search
     */
    public void searchByName(String query) {
        System.out.println("\nSearch Results for: \"" + query + "\"");
        System.out.println("-".repeat(85));
        boolean found = false;
        for (Employee emp : employeeList) {
            if (emp.getName().toLowerCase().contains(query.toLowerCase())) {
                System.out.println(emp);
                found = true;
            }
        }
        if (!found) {
            System.out.println("  No employees matched \"" + query + "\"");
        }
    }

    // ─── Save & Getters ────────────────────────────────────────────────────────

    /**
     * Persists current employee list to CSV file.
     */
    public void saveToFile() {
        FileHandler.saveEmployees(employeeList);
    }

    /**
     * Returns the full employee list (used by PayrollManager).
     *
     * @return List of all employees
     */
    public List<Employee> getAllEmployees() {
        return Collections.unmodifiableList(employeeList);
    }

    /**
     * Returns total number of employees.
     *
     * @return count
     */
    public int getCount() {
        return employeeList.size();
    }
}
