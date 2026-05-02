package com.payroll.service;

import com.payroll.util.AuditLogger;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Authentication Service — Role-Based Access Control.
 *
 * In a production system this would delegate to a real credential store
 * with BCrypt-hashed passwords.  For this self-contained demo the users
 * are held in a ConcurrentHashMap with SHA-256 digests of the passwords.
 *
 * Roles:
 *   ADMIN  — full access (add/edit/delete employees, run payroll)
 *   HR     — view-only + run payroll, cannot edit employee records
 */
public class AuthenticationService {

    public enum Role { ADMIN, HR }

    /** Holds username → [hashedPassword, role] */
    private final ConcurrentHashMap<String, String[]> userStore = new ConcurrentHashMap<>();

    private static AuthenticationService instance;

    private AuthenticationService() {
        // Seed default accounts (password stored as SHA-256 hex)
        register("admin",   hash("admin123"),  Role.ADMIN);
        register("hr_user", hash("hr@pass"),   Role.HR);
    }

    public static synchronized AuthenticationService getInstance() {
        if (instance == null) instance = new AuthenticationService();
        return instance;
    }

    // ── Public API ───────────────────────────────────────────────────────────

    /**
     * Validates credentials and returns the user's role on success,
     * or {@code null} on failure.
     */
    public Role authenticate(String username, String plainPassword) {
        String[] entry = userStore.get(username.toLowerCase());
        if (entry == null) return null;

        String storedHash = entry[0];
        if (!storedHash.equals(hash(plainPassword))) return null;

        AuditLogger.log("LOGIN", username, "Successful authentication as " + entry[1]);
        return Role.valueOf(entry[1]);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void register(String username, String hashedPw, Role role) {
        userStore.put(username, new String[]{ hashedPw, role.name() });
    }

    /** Simple SHA-256 hex digest. Replace with BCrypt in production. */
    private static String hash(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
