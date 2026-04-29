package com.payroll.gui;

import com.payroll.exception.DuplicateEmployeeException;
import com.payroll.manager.EmployeeManager;
import com.payroll.manager.PayrollManager;
import com.payroll.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PayrollApp extends JFrame {
    private EmployeeManager employeeManager;
    private PayrollManager payrollManager;
    private DefaultTableModel tableModel;

    public PayrollApp() {
        employeeManager = new EmployeeManager();
        payrollManager = new PayrollManager(employeeManager);

        setTitle("Multithreaded Payroll Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        // Save data when user clicks the window close 'X'
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                employeeManager.saveToFile();
                System.exit(0);
            }
        });

        initUI();
        refreshTable();
    }

    private void initUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Payroll Management Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Employee ID", "Name", "Department", "Type", "Base Salary (Rs.)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 15, 15));
        buttonPanel.setPreferredSize(new Dimension(200, 0));

        JButton btnAdd = createStyledButton("Add Employee");
        JButton btnDelete = createStyledButton("Delete Employee");
        JButton btnRunPayroll = createStyledButton("Run Payroll");
        JButton btnDeptReport = createStyledButton("Department Report");
        JButton btnSaveExit = createStyledButton("Save & Exit");

        btnAdd.addActionListener(e -> showAddEmployeeDialog());
        
        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String empId = (String) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete employee " + empId + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        employeeManager.deleteEmployee(empId);
                        refreshTable();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an employee from the table to delete.", "Select Employee", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnRunPayroll.addActionListener(e -> {
            btnRunPayroll.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Payroll processing started in background threads.\nCheck the terminal for live thread logs.", "Processing", JOptionPane.INFORMATION_MESSAGE);
            
            // Run payroll in background so it doesn't freeze the GUI
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    payrollManager.runPayroll();
                    return null;
                }
                @Override
                protected void done() {
                    btnRunPayroll.setEnabled(true);
                    JOptionPane.showMessageDialog(PayrollApp.this, "Payroll run complete!\nCheck 'payslips' folder and 'payroll_log.txt'.", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            };
            worker.execute();
        });

        btnDeptReport.addActionListener(e -> {
            payrollManager.generateDepartmentReport();
            JOptionPane.showMessageDialog(this, "Department report generated successfully in the console.", "Report Generated", JOptionPane.INFORMATION_MESSAGE);
        });

        btnSaveExit.addActionListener(e -> {
            employeeManager.saveToFile();
            System.exit(0);
        });

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        buttonPanel.add(new JSeparator());
        buttonPanel.add(btnRunPayroll);
        buttonPanel.add(btnDeptReport);
        buttonPanel.add(btnSaveExit);

        rightPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel);
    }
    
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        return btn;
    }

    private void showAddEmployeeDialog() {
        JDialog dialog = new JDialog(this, "Add New Employee", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Employee ID:"));
        JTextField txtId = new JTextField();
        panel.add(txtId);

        panel.add(new JLabel("Name:"));
        JTextField txtName = new JTextField();
        panel.add(txtName);

        panel.add(new JLabel("Department:"));
        JTextField txtDept = new JTextField();
        panel.add(txtDept);

        panel.add(new JLabel("Type:"));
        String[] types = {"Full-Time", "Part-Time", "Contract"};
        JComboBox<String> comboType = new JComboBox<>(types);
        panel.add(comboType);

        panel.add(new JLabel("Base Salary (Rs.):"));
        JTextField txtSalary = new JTextField();
        panel.add(txtSalary);

        JButton btnSave = new JButton("Save Employee");
        JButton btnCancel = new JButton("Cancel");

        btnSave.addActionListener(e -> {
            try {
                String id = txtId.getText().trim().toUpperCase();
                String name = txtName.getText().trim();
                String dept = txtDept.getText().trim();
                String salaryStr = txtSalary.getText().trim();
                String type = (String) comboType.getSelectedItem();
                
                if (id.isEmpty() || name.isEmpty() || dept.isEmpty() || salaryStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double salary = Double.parseDouble(salaryStr);

                Employee emp;
                if ("Part-Time".equals(type)) {
                    emp = new PartTimeEmployee(id, name, dept, salary);
                } else if ("Contract".equals(type)) {
                    emp = new ContractEmployee(id, name, dept, salary);
                } else {
                    emp = new FullTimeEmployee(id, name, dept, salary);
                }

                employeeManager.addEmployee(emp);
                refreshTable();
                dialog.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid number for Base Salary.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (DuplicateEmployeeException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Duplicate Employee", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        panel.add(btnSave);
        panel.add(btnCancel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Employee> list = employeeManager.getAllEmployees();
        for (Employee emp : list) {
            tableModel.addRow(new Object[]{
                    emp.getEmpId(),
                    emp.getName(),
                    emp.getDepartment(),
                    emp.getEmployeeType(),
                    String.format("%.2f", emp.getBaseSalary())
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PayrollApp().setVisible(true);
        });
    }
}
