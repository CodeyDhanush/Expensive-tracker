package com.example.expensetracker.db;

import java.sql.*;
import java.nio.file.*;

public class Database {
    private static final String DB_FILE = "expenses.db";
    private static final String URL = "jdbc:sqlite:" + DB_FILE;

    static {
        // Create DB + tables on first load
        try (Connection conn = getConnection()) {
            if (conn != null) {
                try (Statement st = conn.createStatement()) {
                    st.executeUpdate("PRAGMA foreign_keys = ON");
                    st.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS transactions (" +
                        " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " date TEXT NOT NULL," +                // YYYY-MM-DD
                        " description TEXT," +
                        " category TEXT NOT NULL," +
                        " type TEXT NOT NULL CHECK(type IN ('INCOME','EXPENSE'))," +
                        " amount REAL NOT NULL CHECK(amount >= 0)" +
                        ")"
                    );
                    st.executeUpdate("CREATE INDEX IF NOT EXISTS idx_txn_date ON transactions(date)");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        // Ensure file exists (optional; JDBC will create it anyway)
        try { Files.createFile(Path.of(DB_FILE)); } catch (Exception ignored) {}
        return DriverManager.getConnection(URL);
    }
}
