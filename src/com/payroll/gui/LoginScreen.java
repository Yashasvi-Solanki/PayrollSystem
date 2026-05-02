package com.payroll.gui;

import com.payroll.service.AuthenticationService;
import com.payroll.service.AuthenticationService.Role;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Login Screen — entry point to the application.
 *
 * Validates credentials via {@link AuthenticationService}.
 * On success, opens the {@link MainDashboard} with the authenticated role.
 */
public class LoginScreen extends JFrame {

    // ── Colour palette ───────────────────────────────────────────────────────
    private static final Color BG_DARK      = new Color(18,  18,  28);
    private static final Color BG_CARD      = new Color(28,  28,  45);
    private static final Color ACCENT       = new Color(99, 102, 241);  // indigo
    private static final Color ACCENT_HOVER = new Color(79,  70, 229);
    private static final Color FG_PRIMARY   = new Color(240, 240, 255);
    private static final Color FG_MUTED     = new Color(148, 148, 180);
    private static final Color BORDER_CLR   = new Color(55,  55,  80);
    private static final Color ERROR_CLR    = new Color(239,  68,  68);
    private static final Color SUCCESS_CLR  = new Color(34,  197,  94);

    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JLabel         lblStatus;

    public LoginScreen() {
        setTitle("Payroll System — Login");
        setSize(480, 540);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BG_DARK);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG_DARK);
        root.setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(40, 40, 40, 40)));

        // ── Logo / Title ─────────────────────────────────────────────────
        JLabel icon = new JLabel("⚙", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        icon.setForeground(ACCENT);
        icon.setAlignmentX(CENTER_ALIGNMENT);
        card.add(icon);
        card.add(Box.createVerticalStrut(8));

        JLabel title = new JLabel("Payroll System", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(FG_PRIMARY);
        title.setAlignmentX(CENTER_ALIGNMENT);
        card.add(title);

        JLabel subtitle = new JLabel("Sign in to your account", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(FG_MUTED);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
        card.add(subtitle);
        card.add(Box.createVerticalStrut(30));

        // ── Username field ────────────────────────────────────────────────
        card.add(makeLabel("Username"));
        card.add(Box.createVerticalStrut(6));
        txtUsername = makeTextField("admin");
        card.add(txtUsername);
        card.add(Box.createVerticalStrut(16));

        // ── Password field ────────────────────────────────────────────────
        card.add(makeLabel("Password"));
        card.add(Box.createVerticalStrut(6));
        txtPassword = new JPasswordField();
        styleTextField(txtPassword);
        txtPassword.setText("admin123");
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(24));

        // ── Status label ──────────────────────────────────────────────────
        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(ERROR_CLR);
        lblStatus.setAlignmentX(CENTER_ALIGNMENT);
        card.add(lblStatus);
        card.add(Box.createVerticalStrut(8));

        // ── Login button ──────────────────────────────────────────────────
        JButton btnLogin = makeAccentButton("Sign In");
        btnLogin.addActionListener(e -> attemptLogin());
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(16));

        // ── Hint ──────────────────────────────────────────────────────────
        JLabel hint = new JLabel("<html><center>Demo: admin / admin123<br>HR: hr_user / hr@pass</center></html>",
                SwingConstants.CENTER);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hint.setForeground(FG_MUTED);
        hint.setAlignmentX(CENTER_ALIGNMENT);
        card.add(hint);

        root.add(card);

        // Enter key triggers login
        getRootPane().setDefaultButton(btnLogin);

        // Allow pressing Enter in password field
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) attemptLogin();
            }
        });

        add(root);
    }

    private void attemptLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        lblStatus.setText("Authenticating…");
        lblStatus.setForeground(FG_MUTED);

        Role role = AuthenticationService.getInstance().authenticate(username, password);

        if (role == null) {
            lblStatus.setText("⚠ Invalid username or password.");
            lblStatus.setForeground(ERROR_CLR);
            txtPassword.setText("");
        } else {
            lblStatus.setText("✓ Login successful!");
            lblStatus.setForeground(SUCCESS_CLR);
            Timer t = new Timer(500, ev -> {
                dispose();
                new MainDashboard(role).setVisible(true);
            });
            t.setRepeats(false);
            t.start();
        }
    }

    // ── UI helpers ───────────────────────────────────────────────────────────

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(FG_MUTED);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField makeTextField(String placeholder) {
        JTextField f = new JTextField(placeholder);
        styleTextField(f);
        return f;
    }

    private void styleTextField(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBackground(new Color(38, 38, 58));
        f.setForeground(FG_PRIMARY);
        f.setCaretColor(FG_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(10, 12, 10, 12)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    }

    private JButton makeAccentButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? ACCENT_HOVER :
                            getModel().isRollover() ? ACCENT_HOVER.brighter() : ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.setAlignmentX(CENTER_ALIGNMENT);
        return btn;
    }
}
