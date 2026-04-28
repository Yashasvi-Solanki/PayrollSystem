package com.payroll.manager;

import com.payroll.exception.DuplicateEmployeeException;
import com.payroll.exception.EmployeeNotFoundException;
import com.payroll.model.Employee;
import com.payroll.util.FileHandler;
import com.payroll.util.Logger;

import java.util.*;

public class EmployeeManager {
    private final List<Employee> employeeList;
    private final Map<String, Employee> employeeMap;

    public EmployeeManager() {
        this.employeeList = new ArrayList<>();
        this.employeeMap  = Collections.synchronizedMap(new HashMap<>());

        List<Employee> loaded = FileHandler.loadEmployees();
        for (Employee emp : loaded) {
            employeeList.add(emp);
            employeeMap.put(emp.getEmpId(), emp);
        }
    }

    public void addEmployee(Employee employee) throws DuplicateEmployeeException {
        if (employeeMap.containsKey(employee.getEmpId())) {
            throw new DuplicateEmployeeException(employee.getEmpId());
        }
        employeeList.add(employee);
        employeeMap.put(employee.getEmpId(), employee);
        Logger.info("Added employee: " + employee.getEmpId() + " - " + employee.getName());
    }

    public Employee getEmployeeById(String empId) throws EmployeeNotFoundException {
        Employee emp = employeeMap.get(empId);
        if (emp == null) {
            throw new EmployeeNotFoundException(empId);
        }
        return emp;
    }

    public void updateEmployee(String empId, String newName, String newDept, double newSalary) throws EmployeeNotFoundException {
        Employee emp = getEmployeeById(empId);
        emp.setName(newName);
        emp.setDepartment(newDept);
        emp.setBaseSalary(newSalary);
        Logger.info("Updated employee: " + empId);
    }

    public void deleteEmployee(String empId) throws EmployeeNotFoundException {
        Employee emp = getEmployeeById(empId);
        employeeList.remove(emp);
        employeeMap.remove(empId);
        Logger.info("Deleted employee: " + empId);
    }

    public void displayAllEmployees() {
        if (employeeList.isEmpty()) {
            System.out.println("\n  No employees found.");
            return;
        }
        System.out.println("\n=====================================================================================");
        System.out.printf("%-10s | %-20s | %-15s | %-12s | %s%n", "ID", "Name", "Department", "Type", "Base Salary");
        System.out.println("=====================================================================================");
        for (Employee emp : employeeList) {
            System.out.println(emp);
        }
        System.out.println("=====================================================================================");
        System.out.println("  Total Employees: " + employeeList.size());
    }

    public void searchByName(String query) {
        System.out.println("\nSearch Results for: \"" + query + "\"");
        System.out.println("-------------------------------------------------------------------------------------");
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

    public void saveToFile() {
        FileHandler.saveEmployees(employeeList);
    }

    public List<Employee> getAllEmployees() {
        return Collections.unmodifiableList(employeeList);
    }

    public int getCount() {
        return employeeList.size();
    }
}
