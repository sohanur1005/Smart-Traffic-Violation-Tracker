package dao;

import model.Record;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecordDAO {
    public boolean createRecord(Record record) {
        String sql = "INSERT INTO records (driver_id, vehicle_id, violation_id, officer_id, fine_amount, status, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, record.getDriverId());
            pstmt.setInt(2, record.getVehicleId());
            pstmt.setInt(3, record.getViolationId());
            pstmt.setInt(4, record.getOfficerId());
            pstmt.setDouble(5, record.getFineAmount());
            pstmt.setString(6, record.getStatus());
            pstmt.setString(7, record.getNotes());
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        record.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating violation record: " + e.getMessage());
        }
        return false;
    }

    public Record getRecordById(int id) {
        String sql = "SELECT * FROM records WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Record(
                        rs.getInt("id"),
                        rs.getInt("driver_id"),
                        rs.getInt("vehicle_id"),
                        rs.getInt("violation_id"),
                        rs.getInt("officer_id"),
                        rs.getDouble("fine_amount"),
                        rs.getTimestamp("violation_date"),
                        rs.getString("status"),
                        rs.getString("notes")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting record by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean updateRecordStatus(int id, String status) {
        String sql = "UPDATE records SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating record status: " + e.getMessage());
        }
        return false;
    }

    public List<Record> getRecordsByDriver(int driverId) {
        List<Record> list = new ArrayList<>();
        String sql = "SELECT * FROM records WHERE driver_id = ? ORDER BY violation_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, driverId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Record(
                        rs.getInt("id"),
                        rs.getInt("driver_id"),
                        rs.getInt("vehicle_id"),
                        rs.getInt("violation_id"),
                        rs.getInt("officer_id"),
                        rs.getDouble("fine_amount"),
                        rs.getTimestamp("violation_date"),
                        rs.getString("status"),
                        rs.getString("notes")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting records by driver: " + e.getMessage());
        }
        return list;
    }

    public List<Record> getRecordsByVehicle(int vehicleId) {
        List<Record> list = new ArrayList<>();
        String sql = "SELECT * FROM records WHERE vehicle_id = ? ORDER BY violation_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, vehicleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Record(
                        rs.getInt("id"),
                        rs.getInt("driver_id"),
                        rs.getInt("vehicle_id"),
                        rs.getInt("violation_id"),
                        rs.getInt("officer_id"),
                        rs.getDouble("fine_amount"),
                        rs.getTimestamp("violation_date"),
                        rs.getString("status"),
                        rs.getString("notes")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting records by vehicle: " + e.getMessage());
        }
        return list;
    }

    public List<Record> getAllRecords() {
        List<Record> list = new ArrayList<>();
        String sql = "SELECT * FROM records ORDER BY violation_date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Record(
                    rs.getInt("id"),
                    rs.getInt("driver_id"),
                    rs.getInt("vehicle_id"),
                    rs.getInt("violation_id"),
                    rs.getInt("officer_id"),
                    rs.getDouble("fine_amount"),
                    rs.getTimestamp("violation_date"),
                    rs.getString("status"),
                    rs.getString("notes")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error listing violation records: " + e.getMessage());
        }
        return list;
    }
}
