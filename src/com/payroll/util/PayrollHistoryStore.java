package com.payroll.util;

import com.payroll.model.PayrollRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * In-memory store for all payroll records generated during the session.
 * Uses a {@link CopyOnWriteArrayList} so that the Swing table can iterate
 * over records while the payroll engine is still writing new ones.
 *
 * Singleton — one authoritative list per JVM run.
 */
public class PayrollHistoryStore {

    private static PayrollHistoryStore instance;
    private final CopyOnWriteArrayList<PayrollRecord> history = new CopyOnWriteArrayList<>();

    private PayrollHistoryStore() {}

    public static synchronized PayrollHistoryStore getInstance() {
        if (instance == null) instance = new PayrollHistoryStore();
        return instance;
    }

    public void add(PayrollRecord record) {
        history.add(record);
    }

    public void clear() {
        history.clear();
    }

    /** Returns an unmodifiable view for safe iteration. */
    public List<PayrollRecord> getAll() {
        return Collections.unmodifiableList(history);
    }

    public int size() {
        return history.size();
    }
}
