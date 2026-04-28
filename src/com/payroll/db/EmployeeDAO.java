package com.payroll.db;

import com.payroll.model.*;
import com.payroll.util.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Employee database operations.
 *
 * Demonstrates:
 * - JDBC PreparedStatement (prevents SQL injection)
 * - ResultSet processing
 * - try-with-resources for auto-closing
 * - Full CRUD on MySQL
 *
 * SQL Setup (run in MySQL before use):
 * ─────────────────────────────────────
 * CREATE TABLE IF NOT EXISTS employees (
 * emp_id VARCHAR(10) PRIMARY KEY,
 * name VARCHAR(100),
 * department VARCHAR(50),
 * emp_type VARCHAR(20),
 * base_salary DOUBLE
 * );
 *
 * CREATE TABLE IF NOT EXISTS payroll_history (
 * id INT AUTO_INCREMENT PRIMARY KEY,
 * emp_id VARCHAR(10),
 * run_date DATE,
 * gross_salary DOUBLE,
 * deductions DOUBLE,
 * net_salary DOUBLE,
 * FOREIGN KEY (emp_id) REFERENCES employees(emp_id)
 * );
 */
public class EmployeeDAO {

    // ─── SQL Queries ──────────────────────────────────────────────────────────
    private static final String INSERT_EMP = "INSERT INTO employees (emp_id, name, department, emp_type, base_salary) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_ALL = "SELECT * FROM employees";
    private static final String SELECT_BY_ID = "SELECT * FROM employees WHERE emp_id = ?";
    private static final String UPDATE_EMP = "UPDATE employees SET name=?, department=?, base_salary=? WHERE emp_id=?";
    private static final String DELETE_EMP = "DELETE FROM employees WHERE emp_id=?";
    private static final String INSERT_PAYROLL = "INSERT INTO payroll_history (emp_id, run_date, gross_salary, deductions, net_salary) VALUES (?, CURDATE(), ?, ?, ?)";

    // ─── Insert Employee ──────────────────────────────────────────────────────

    /**
     * Inserts a new employee into the database.
     *
     * @param emp Employee to insert
     */
    public void insertEmployee(Employee emp) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(INSERT_EMP)) {

            ps.setString(1, emp.getEmpId());
            ps.setString(2, emp.getName());
            ps.setString(3, emp.getDepartment());
            ps.setString(4, emp.getEmployeeType());
            ps.setDouble(5, emp.getBaseSalary());
            ps.executeUpdate();

            Logger.info("DB: Inserted employee " + emp.getEmpId());

        } catch (SQLException e) {
            Logger.error("DB insert failed for " + emp.getEmpId(), e);
        }
    }

    // ─── Get All Employees ────────────────────────────────────────────────────

    /**
     * Fetches all employees from the database.
     *
     * @return List of Employee objects
     */
    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Employee emp = buildEmployee(rs);
                if (emp != null)
                    list.add(emp);
            }

        } catch (SQLException e) {
            Logger.error("DB select all failed", e);
        }

        return list;
    }

    // ─── Get Employee by ID ───────────────────────────────────────────────────

    /**
     * Fetches a single employee by ID.
     *
     * @param empId Employee ID to search
     * @return Employee or null if not found
     */
    public Employee getEmployeeById(String empId) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {

            ps.setString(1, empId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return buildEmployee(rs);
                }
            }

        } catch (SQLException e) {
            Logger.error("DB select by ID failed for: " + empId, e);
        }
        return null;
    }

    // ─── Update Employee ──────────────────────────────────────────────────────

    /**
     * Updates employee name, department, and salary in the database.
     *
     * @param emp Updated employee object
     */
    public void updateEmployee(Employee emp) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(UPDATE_EMP)) {

            ps.setString(1, emp.getName());
            ps.setString(2, emp.getDepartment());
            ps.setDouble(3, emp.getBaseSalary());
            ps.setString(4, emp.getEmpId());
            ps.executeUpdate();

            Logger.info("DB: Updated employee " + emp.getEmpId());

        } catch (SQLException e) {
            Logger.error("DB update failed for " + emp.getEmpId(), e);
        }
    }

    // ─── Delete Employee ──────────────────────────────────────────────────────

    /**
     * Deletes an employee from the database by ID.
     *
     * @param empId Employee ID to delete
     */
    public void deleteEmployee(String empId) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(DELETE_EMP)) {

            ps.setString(1, empId);
            ps.executeUpdate();

            Logger.info("DB: Deleted employee " + empId);

        } catch (SQLException e) {
            Logger.error("DB delete failed for " + empId, e);
        }
    }

    // ─── Save Payroll History ─────────────────────────────────────────────────

    /**
     * Inserts a payroll record into payroll_history table.
     *
     * @param empId      Employee ID
     * @param gross      Gross salary
     * @param deductions Deductions
     * @param net        Net salary
     */
    public void savePayrollRecord(String empId, double gross, double deductions, double net) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(INSERT_PAYROLL)) {

            ps.setString(1, empId);
            ps.setDouble(2, gross);
            ps.setDouble(3, deductions);
            ps.setDouble(4, net);
            ps.executeUpdate();

        } catch (SQLException e) {
            Logger.error("DB payroll insert failed for " + empId, e);
        }
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    /**
     * Builds an Employee object from a ResultSet row.
     *
     * @param rs Current ResultSet row
     * @return Employee subclass instance
     * @throws SQLException on column access error
     */
    private Employee buildEmployee(ResultSet rs) throws SQLException {
        String empId = rs.getString("emp_id");
        String name = rs.getString("name");
        String department = rs.getString("department");
        String type = rs.getString("emp_type");
        double baseSalary = rs.getDouble("base_salary");

        switch (type) {
            case "FULL_TIME":
                return new FullTimeEmployee(empId, name, department, baseSalary);
            case "PART_TIME":
                return new PartTimeEmployee(empId, name, department, baseSalary);
            case "CONTRACT":
                return new ContractEmployee(empId, name, department, baseSalary);
            default:
                Logger.info("Unknown type from DB: " + type);
                return null;
        }
    }
}
