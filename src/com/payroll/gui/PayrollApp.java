package com.payroll.gui;

import javax.swing.*;

/**
 * Application entry point.
 * Sets dark Nimbus look-and-feel, then launches the LoginScreen.
 */
public class PayrollApp {

    public static void main(String[] args) {
        // Apply Nimbus LAF and override it with dark tokens
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        // Dark overrides for Nimbus
        UIManager.put("control",          new java.awt.Color(24, 24, 38));
        UIManager.put("info",             new java.awt.Color(24, 24, 38));
        UIManager.put("nimbusBase",       new java.awt.Color(18, 18, 28));
        UIManager.put("nimbusBlueGrey",   new java.awt.Color(55, 55, 80));
        UIManager.put("nimbusLightBackground", new java.awt.Color(32, 32, 52));
        UIManager.put("text",             new java.awt.Color(240, 240, 255));
        UIManager.put("TabbedPane.contentAreaColor", new java.awt.Color(24, 24, 38));

        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}
