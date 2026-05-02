package com.payroll.gui;

import com.payroll.exception.DuplicateEmployeeException;
import com.payroll.exception.EmployeeNotFoundException;
import com.payroll.manager.EmployeeManager;
import com.payroll.manager.PayrollManager;
import com.payroll.model.*;
import com.payroll.model.PayrollRecord;
import com.payroll.service.AuthenticationService.Role;
import com.payroll.util.PayrollHistoryStore;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * Main application window — MVC View layer.
 * Contains three tabs: Dashboard, Employee Management, Payroll Dispatcher, Reports.
 */
public class MainDashboard extends JFrame {

    // ── Palette ──────────────────────────────────────────────────────────────
    static final Color BG_DARK    = new Color(18,  18,  28);
    static final Color BG_PANEL   = new Color(24,  24,  38);
    static final Color BG_CARD    = new Color(32,  32,  52);
    static final Color ACCENT     = new Color(99, 102, 241);
    static final Color ACCENT2    = new Color(16, 185, 129);  // emerald
    static final Color FG_PRIMARY = new Color(240, 240, 255);
    static final Color FG_MUTED   = new Color(148, 148, 180);
    static final Color BORDER_CLR = new Color(55,  55,  80);
    static final Color ROW_ALT    = new Color(28,  28,  46);

    private final EmployeeManager employeeManager;
    private final PayrollManager  payrollManager;
    private final Role            userRole;

    // Shared table models
    private DefaultTableModel empTableModel;
    private DefaultTableModel histTableModel;

    // Dashboard stat labels
    private JLabel lblEmpCount;
    private JLabel lblPayrollCost;
    private JLabel lblPayslipsGenerated;

    // Progress bar for payroll run
    private JProgressBar progressBar;
    private JLabel       lblProgress;

    public MainDashboard(Role role) {
        this.userRole        = role;
        this.employeeManager = new EmployeeManager();
        this.payrollManager  = new PayrollManager(employeeManager);

        setTitle("Payroll Management System  |  " + role.name());
        setSize(1150, 720);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                employeeManager.saveToFile();
                dispose();
                System.exit(0);
            }
        });

        buildUI();
        refreshEmployeeTable();
        refreshDashboardStats();
    }

    // ── UI Construction ──────────────────────────────────────────────────────

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);

        root.add(buildTopBar(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(BG_PANEL);
        tabs.setForeground(FG_PRIMARY);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabs.addTab("📊  Dashboard",   buildDashboardTab());
        tabs.addTab("👥  Employees",   buildEmployeesTab());
        tabs.addTab("⚡  Run Payroll", buildPayrollTab());
        tabs.addTab("📋  Reports",     buildReportsTab());

        root.add(tabs, BorderLayout.CENTER);
        add(root);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_CARD);
        bar.setBorder(new EmptyBorder(12, 24, 12, 24));

        JLabel title = new JLabel("⚙  Payroll Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(FG_PRIMARY);
        bar.add(title, BorderLayout.WEST);

        JLabel roleTag = new JLabel("Role: " + userRole.name() + "  ");
        roleTag.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleTag.setForeground(ACCENT2);
        bar.add(roleTag, BorderLayout.EAST);
        return bar;
    }

    // ── Dashboard Tab ────────────────────────────────────────────────────────

    private JPanel buildDashboardTab() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setBackground(BG_DARK);
        p.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel heading = new JLabel("System Overview");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setForeground(FG_PRIMARY);
        p.add(heading, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 3, 20, 0));
        cards.setBackground(BG_DARK);

        lblEmpCount         = new JLabel("0", SwingConstants.CENTER);
        lblPayrollCost      = new JLabel("Rs. 0", SwingConstants.CENTER);
        lblPayslipsGenerated = new JLabel("0", SwingConstants.CENTER);

        cards.add(statCard("👤  Active Employees", lblEmpCount,    ACCENT));
        cards.add(statCard("💰  Est. Monthly Cost", lblPayrollCost, new Color(245,158,11)));
        cards.add(statCard("📄  Payslips Generated", lblPayslipsGenerated, ACCENT2));

        p.add(cards, BorderLayout.CENTER);
        return p;
    }

    private JPanel statCard(String label, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(30, 20, 30, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.BOTH;

        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(FG_MUTED);
        card.add(lbl, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 0, 0);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(accent);
        card.add(valueLabel, gbc);

        return card;
    }

    // ── Employees Tab ────────────────────────────────────────────────────────

    private JPanel buildEmployeesTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(BG_DARK);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Table
        String[] cols = {"Employee ID", "Name", "Department", "Type", "Base Salary (Rs.)", "Est. Gross (Rs.)"};
        empTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable empTable = styledTable(empTableModel);

        p.add(new JScrollPane(empTable), BorderLayout.CENTER);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setBackground(BG_DARK);

        JButton btnAdd  = accentBtn("＋ Add Employee", ACCENT);
        JButton btnEdit = accentBtn("✏ Edit", new Color(245,158,11));
        JButton btnDel  = accentBtn("🗑 Delete", new Color(239,68,68));
        JButton btnRef  = accentBtn("↻ Refresh", FG_MUTED);

        boolean canEdit = (userRole == Role.ADMIN);
        btnAdd.setEnabled(canEdit);
        btnEdit.setEnabled(canEdit);
        btnDel.setEnabled(canEdit);

        btnAdd.addActionListener(e -> showAddDialog(empTable));
        btnEdit.addActionListener(e -> showEditDialog(empTable));
        btnDel.addActionListener(e -> deleteSelected(empTable));
        btnRef.addActionListener(e -> { refreshEmployeeTable(); refreshDashboardStats(); });

        toolbar.add(btnAdd); toolbar.add(btnEdit); toolbar.add(btnDel); toolbar.add(btnRef);
        p.add(toolbar, BorderLayout.SOUTH);
        return p;
    }

    // ── Payroll Tab ──────────────────────────────────────────────────────────

    private JPanel buildPayrollTab() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setBackground(BG_DARK);
        p.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(BG_DARK);

        JLabel h = new JLabel("Payroll Dispatcher");
        h.setFont(new Font("Segoe UI", Font.BOLD, 22));
        h.setForeground(FG_PRIMARY);
        h.setAlignmentX(LEFT_ALIGNMENT);
        top.add(h);

        JLabel sub = new JLabel("Runs concurrent worker threads via ExecutorService (thread pool = 5)");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(FG_MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        top.add(Box.createVerticalStrut(6));
        top.add(sub);
        p.add(top, BorderLayout.NORTH);

        // Center card
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR),
                new EmptyBorder(30, 30, 30, 30)));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        progressBar.setForeground(ACCENT);
        progressBar.setBackground(new Color(40, 40, 60));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        progressBar.setAlignmentX(LEFT_ALIGNMENT);
        card.add(progressBar);
        card.add(Box.createVerticalStrut(12));

        lblProgress = new JLabel("Ready — click 'Run Payroll' to begin.");
        lblProgress.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblProgress.setForeground(FG_MUTED);
        lblProgress.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lblProgress);
        card.add(Box.createVerticalStrut(24));

        JButton btnRun = accentBtn("▶  Run Payroll Now", ACCENT);
        btnRun.setAlignmentX(LEFT_ALIGNMENT);
        btnRun.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnRun.setMaximumSize(new Dimension(220, 44));
        card.add(btnRun);

        btnRun.addActionListener(e -> {
            if (employeeManager.getCount() == 0) {
                showError("No employees to process. Add employees first.");
                return;
            }
            btnRun.setEnabled(false);
            progressBar.setValue(0);
            lblProgress.setText("Processing…");
            lblProgress.setForeground(FG_MUTED);

            SwingWorker<List<PayrollRecord>, Integer> worker = new SwingWorker<>() {
                @Override protected List<PayrollRecord> doInBackground() throws Exception {
                    return payrollManager.runPayroll(pct -> publish(pct));
                }
                @Override protected void process(java.util.List<Integer> chunks) {
                    int v = chunks.get(chunks.size() - 1);
                    progressBar.setValue(v);
                    lblProgress.setText("Processing… " + v + "% complete");
                }
                @Override protected void done() {
                    try {
                        List<PayrollRecord> records = get();
                        progressBar.setValue(100);
                        lblProgress.setForeground(ACCENT2);
                        lblProgress.setText("✓ Done! " + records.size()
                                + " payslips generated. Check payslips/ folder.");
                        refreshHistoryTable();
                        refreshDashboardStats();
                    } catch (Exception ex) {
                        lblProgress.setText("Error: " + ex.getMessage());
                        lblProgress.setForeground(new Color(239, 68, 68));
                    }
                    btnRun.setEnabled(true);
                }
            };
            worker.execute();
        });

        p.add(card, BorderLayout.CENTER);
        return p;
    }

    // ── Reports Tab ──────────────────────────────────────────────────────────

    private JPanel buildReportsTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(BG_DARK);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel h = new JLabel("Payroll History");
        h.setFont(new Font("Segoe UI", Font.BOLD, 18));
        h.setForeground(FG_PRIMARY);
        p.add(h, BorderLayout.NORTH);

        String[] cols = {"Date", "Emp ID", "Name", "Department", "Type",
                         "Gross (Rs.)", "Deductions (Rs.)", "Net (Rs.)"};
        histTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable histTable = styledTable(histTableModel);
        p.add(new JScrollPane(histTable), BorderLayout.CENTER);

        JButton btnRefresh = accentBtn("↻ Refresh", FG_MUTED);
        btnRefresh.addActionListener(e -> refreshHistoryTable());
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bar.setBackground(BG_DARK);
        bar.add(btnRefresh);
        p.add(bar, BorderLayout.SOUTH);
        return p;
    }

    // ── Data Refresh ─────────────────────────────────────────────────────────

    private void refreshEmployeeTable() {
        empTableModel.setRowCount(0);
        for (Employee e : employeeManager.getAllEmployees()) {
            empTableModel.addRow(new Object[]{
                e.getEmpId(), e.getName(), e.getDepartment(),
                e.getEmployeeType(),
                String.format("%,.2f", e.getBaseSalary()),
                String.format("%,.2f", e.calculateGrossPay())
            });
        }
    }

    private void refreshHistoryTable() {
        histTableModel.setRowCount(0);
        for (PayrollRecord r : PayrollHistoryStore.getInstance().getAll()) {
            histTableModel.addRow(new Object[]{
                r.getPayDate(), r.getEmpId(), r.getEmpName(),
                r.getDepartment(), r.getEmpType(),
                String.format("%,.2f", r.getGrossSalary()),
                String.format("%,.2f", r.getTotalDeductions()),
                String.format("%,.2f", r.getNetSalary())
            });
        }
    }

    private void refreshDashboardStats() {
        lblEmpCount.setText(String.valueOf(employeeManager.getCount()));
        lblPayrollCost.setText(String.format("Rs.%,.0f", employeeManager.getTotalPayrollCost()));
        lblPayslipsGenerated.setText(String.valueOf(PayrollHistoryStore.getInstance().size()));
    }

    // ── Dialogs ───────────────────────────────────────────────────────────────

    private void showAddDialog(JTable table) {
        JDialog dlg = new JDialog(this, "Add New Employee", true);
        dlg.setSize(420, 340);
        dlg.setLocationRelativeTo(this);
        dlg.getContentPane().setBackground(BG_CARD);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 12));
        panel.setBackground(BG_CARD);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField fId   = darkField(); JTextField fName = darkField();
        JTextField fDept = darkField(); JTextField fSal  = darkField();
        String[] types = {"Full-Time","Part-Time","Contract"};
        JComboBox<String> cbType = new JComboBox<>(types);
        cbType.setBackground(BG_PANEL); cbType.setForeground(FG_PRIMARY);

        panel.add(darkLabel("Employee ID:")); panel.add(fId);
        panel.add(darkLabel("Name:"));        panel.add(fName);
        panel.add(darkLabel("Department:"));  panel.add(fDept);
        panel.add(darkLabel("Type:"));        panel.add(cbType);
        panel.add(darkLabel("Base Salary:")); panel.add(fSal);

        JButton save   = accentBtn("Save", ACCENT);
        JButton cancel = accentBtn("Cancel", FG_MUTED);
        panel.add(save); panel.add(cancel);

        save.addActionListener(e -> {
            try {
                String id   = fId.getText().trim().toUpperCase();
                String name = fName.getText().trim();
                String dept = fDept.getText().trim();
                double sal  = Double.parseDouble(fSal.getText().trim());
                String type = switch ((String)cbType.getSelectedItem()) {
                    case "Part-Time" -> "PART_TIME";
                    case "Contract"  -> "CONTRACT";
                    default          -> "FULL_TIME";
                };
                Employee emp = EmployeeFactory.create(type, id, name, dept, sal);
                employeeManager.addEmployee(emp);
                refreshEmployeeTable(); refreshDashboardStats();
                dlg.dispose();
            } catch (NumberFormatException ex) {
                showError("Enter a valid numeric salary.");
            } catch (DuplicateEmployeeException ex) {
                showError(ex.getMessage());
            }
        });
        cancel.addActionListener(e -> dlg.dispose());

        dlg.add(panel);
        dlg.setVisible(true);
    }

    private void showEditDialog(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Select an employee to edit."); return; }
        String empId = (String) empTableModel.getValueAt(row, 0);
        try {
            Employee emp = employeeManager.getEmployeeById(empId);
            JDialog dlg = new JDialog(this, "Edit Employee — " + empId, true);
            dlg.setSize(400, 280);
            dlg.setLocationRelativeTo(this);
            dlg.getContentPane().setBackground(BG_CARD);

            JPanel panel = new JPanel(new GridLayout(4, 2, 10, 12));
            panel.setBackground(BG_CARD);
            panel.setBorder(new EmptyBorder(20, 20, 20, 20));

            JTextField fName = darkField(emp.getName());
            JTextField fDept = darkField(emp.getDepartment());
            JTextField fSal  = darkField(String.valueOf(emp.getBaseSalary()));

            panel.add(darkLabel("Name:"));        panel.add(fName);
            panel.add(darkLabel("Department:"));  panel.add(fDept);
            panel.add(darkLabel("Base Salary:")); panel.add(fSal);

            JButton save   = accentBtn("Save", ACCENT);
            JButton cancel = accentBtn("Cancel", FG_MUTED);
            panel.add(save); panel.add(cancel);

            save.addActionListener(e -> {
                try {
                    String n = fName.getText().trim();
                    String d = fDept.getText().trim();
                    double s = Double.parseDouble(fSal.getText().trim());
                    employeeManager.updateEmployee(empId, n, d, s);
                    refreshEmployeeTable(); refreshDashboardStats();
                    dlg.dispose();
                } catch (NumberFormatException ex) {
                    showError("Enter a valid numeric salary.");
                } catch (EmployeeNotFoundException ex) {
                    showError(ex.getMessage());
                }
            });
            cancel.addActionListener(e -> dlg.dispose());
            dlg.add(panel);
            dlg.setVisible(true);
        } catch (EmployeeNotFoundException ex) {
            showError(ex.getMessage());
        }
    }

    private void deleteSelected(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Select an employee to delete."); return; }
        String empId = (String) empTableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete employee " + empId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                employeeManager.deleteEmployee(empId);
                refreshEmployeeTable(); refreshDashboardStats();
            } catch (EmployeeNotFoundException ex) { showError(ex.getMessage()); }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setBackground(BG_PANEL);
        t.setForeground(FG_PRIMARY);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setRowHeight(30);
        t.setGridColor(BORDER_CLR);
        t.getTableHeader().setBackground(BG_CARD);
        t.getTableHeader().setForeground(FG_MUTED);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.setSelectionBackground(new Color(99, 102, 241, 80));
        t.setSelectionForeground(FG_PRIMARY);
        // Alternating row renderer
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, r, c);
                setBackground(sel ? new Color(99, 102, 241, 80) : (r % 2 == 0 ? BG_PANEL : ROW_ALT));
                setForeground(FG_PRIMARY);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
        return t;
    }

    static JButton accentBtn(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(color);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(8, 16, 8, 16));
        return b;
    }

    private JLabel darkLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(FG_MUTED);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return l;
    }

    private JTextField darkField() { return darkField(""); }
    private JTextField darkField(String val) {
        JTextField f = new JTextField(val);
        f.setBackground(new Color(40, 40, 60));
        f.setForeground(FG_PRIMARY);
        f.setCaretColor(FG_PRIMARY);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR),
                new EmptyBorder(6, 8, 6, 8)));
        return f;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
