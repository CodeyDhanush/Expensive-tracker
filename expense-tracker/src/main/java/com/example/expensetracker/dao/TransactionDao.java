package com.example.expensetracker.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.expensetracker.db.Database;
import com.example.expensetracker.model.Transaction;

public class TransactionDao {

    public List<Transaction> findAll() {
        String sql = "SELECT id, date, description, category, type, amount FROM transactions ORDER BY date DESC, id DESC";
        List<Transaction> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Transaction(
                    rs.getInt("id"),
                    LocalDate.parse(rs.getString("date")),
                    rs.getString("description"),
                    rs.getString("category"),
                    rs.getString("type"),
                    rs.getDouble("amount")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public void insert(Transaction t) {
        String sql = "INSERT INTO transactions(date, description, category, type, amount) VALUES(?,?,?,?,?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getDate().toString());
            ps.setString(2, t.getDescription());
            ps.setString(3, t.getCategory());
            ps.setString(4, t.getType());
            ps.setDouble(5, t.getAmount());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // --- fixed delete method ---
    public void deleteById(int id) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Wrapper so controller can call dao.delete(id)
    public void delete(int id) {
        deleteById(id);
    }

    /**
     * Returns a list of maps: [{ym:"2025-01", income:1234.0, expense:980.5}, ...]
     */
    public List<Map<String, Object>> monthlyIncomeVsExpense() {
        String sql = """
            SELECT strftime('%Y-%m', date) AS ym,
                   SUM(CASE WHEN type='INCOME' THEN amount ELSE 0 END) AS income,
                   SUM(CASE WHEN type='EXPENSE' THEN amount ELSE 0 END) AS expense
            FROM transactions
            GROUP BY ym
            ORDER BY ym
        """;
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("ym", rs.getString("ym"));
                m.put("income", rs.getDouble("income"));
                m.put("expense", rs.getDouble("expense"));
                rows.add(m);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rows;
    }
}
