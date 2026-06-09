package dao;

import model.Violation;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ViolationDAO {
    public boolean createViolation(Violation violation) {
        String sql = "INSERT INTO violations (code, description, fine_amount) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, violation.getCode());
            pstmt.setString(2, violation.getDescription());
            pstmt.setDouble(3, violation.getFineAmount());
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        violation.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating violation type: " + e.getMessage());
        }
        return false;
    }

    public Violation getViolationById(int id) {
        String sql = "SELECT * FROM violations WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Violation(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("description"),
                        rs.getDouble("fine_amount")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting violation by ID: " + e.getMessage());
        }
        return null;
    }

    public Violation getViolationByCode(String code) {
        String sql = "SELECT * FROM violations WHERE code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Violation(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("description"),
                        rs.getDouble("fine_amount")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting violation by code: " + e.getMessage());
        }
        return null;
    }

    public boolean updateViolation(Violation violation) {
        String sql = "UPDATE violations SET code = ?, description = ?, fine_amount = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, violation.getCode());
            pstmt.setString(2, violation.getDescription());
            pstmt.setDouble(3, violation.getFineAmount());
            pstmt.setInt(4, violation.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating violation type: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteViolation(int id) {
        String sql = "DELETE FROM violations WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting violation type: " + e.getMessage());
        }
        return false;
    }

    public List<Violation> getAllViolations() {
        List<Violation> list = new ArrayList<>();
        String sql = "SELECT * FROM violations";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Violation(
                    rs.getInt("id"),
                    rs.getString("code"),
                    rs.getString("description"),
                    rs.getDouble("fine_amount")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error listing violation types: " + e.getMessage());
        }
        return list;
    }
}
