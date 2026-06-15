package dao;

import model.Record;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecordDAO {

    // ─── Helper: map a ResultSet row to a Record object ───────────────────────
    private Record mapRow(ResultSet rs) throws SQLException {
        Record r = new Record();
        r.setId(rs.getInt("id"));
        r.setDriverId(rs.getInt("driver_id"));
        r.setVehicleId(rs.getInt("vehicle_id"));
        r.setViolationId(rs.getInt("violation_id"));
        r.setOfficerId(rs.getInt("officer_id"));
        r.setFineAmount(rs.getDouble("fine_amount"));
        r.setViolationDate(rs.getTimestamp("violation_date"));
        r.setStatus(rs.getString("status"));
        r.setNotes(rs.getString("notes"));
        return r;
    }

    // ─── CREATE ───────────────────────────────────────────────────────────────
    public boolean createRecord(Record record) {
        String sql = "INSERT INTO records (driver_id, vehicle_id, violation_id, officer_id, fine_amount, status, notes, fine, payment_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, record.getDriverId());
            pstmt.setInt(2, record.getVehicleId());
            pstmt.setInt(3, record.getViolationId());
            pstmt.setInt(4, record.getOfficerId());
            pstmt.setDouble(5, record.getFineAmount());
            pstmt.setString(6, record.getStatus());
            pstmt.setString(7, record.getNotes());
            pstmt.setDouble(8, record.getFineAmount());
            pstmt.setString(9, record.getStatus());
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

    // ─── READ BY ID ──────────────────────────────────────────────────────────
    public Record getRecordById(int id) {
        String sql = "SELECT * FROM records WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting record by ID: " + e.getMessage());
        }
        return null;
    }

    // ─── GENERIC STATUS UPDATE (internal use) ────────────────────────────────
    public boolean updateRecordStatus(int id, String status) {
        String sql = "UPDATE records SET status = ?, payment_status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setString(2, status);
            pstmt.setInt(3, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating record status: " + e.getMessage());
        }
        return false;
    }

    // ─── PAYMENT WORKFLOW METHODS ─────────────────────────────────────────────

    /**
     * User submits payment → status becomes 'PENDING' (awaiting admin approval).
     */
    public boolean submitPayment(int recordId) {
        String sql = "UPDATE records SET status = 'PENDING', payment_status = 'PENDING' WHERE id = ? AND status IN ('UNPAID', 'REJECTED')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recordId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error submitting payment: " + e.getMessage());
        }
        return false;
    }

    /**
     * Admin approves payment → status becomes 'PAID'.
     */
    public boolean approvePayment(int recordId) {
        String sql = "UPDATE records SET status = 'PAID', payment_status = 'PAID' WHERE id = ? AND status = 'PENDING'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recordId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error approving payment: " + e.getMessage());
        }
        return false;
    }

    /**
     * Admin rejects payment → status becomes 'REJECTED'.
     */
    public boolean rejectPayment(int recordId) {
        String sql = "UPDATE records SET status = 'REJECTED', payment_status = 'REJECTED' WHERE id = ? AND status = 'PENDING'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recordId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error rejecting payment: " + e.getMessage());
        }
        return false;
    }

    /**
     * Admin view: all records whose status is 'PENDING'.
     */
    public List<Record> getPendingPayments() {
        List<Record> list = new ArrayList<>();
        String sql = "SELECT * FROM records WHERE status = 'PENDING' ORDER BY violation_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching pending payments: " + e.getMessage());
        }
        return list;
    }

    /**
     * User view: all UNPAID records for a given driver — these act as notifications.
     */
    public List<Record> getUserNotifications(int driverId) {
        List<Record> list = new ArrayList<>();
        String sql = "SELECT * FROM records WHERE driver_id = ? AND payment_status = 'UNPAID' ORDER BY violation_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, driverId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user notifications: " + e.getMessage());
        }
        return list;
    }

    // ─── LIST METHODS ─────────────────────────────────────────────────────────

    public List<Record> getRecordsByDriver(int driverId) {
        List<Record> list = new ArrayList<>();
        String sql = "SELECT * FROM records WHERE driver_id = ? ORDER BY violation_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, driverId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
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
                    list.add(mapRow(rs));
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
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error listing violation records: " + e.getMessage());
        }
        return list;
    }

    public List<model.TopOffender> getTopOffenders() {
        List<model.TopOffender> list = new ArrayList<>();
        String sql = "SELECT d.name, d.license_number, COUNT(r.id) AS violation_count, SUM(r.fine_amount) AS total_fines " +
                     "FROM drivers d " +
                     "JOIN records r ON d.id = r.driver_id " +
                     "GROUP BY d.id, d.name, d.license_number " +
                     "ORDER BY violation_count DESC " +
                     "LIMIT 5";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new model.TopOffender(
                    rs.getString("name"),
                    rs.getString("license_number"),
                    rs.getInt("violation_count"),
                    rs.getDouble("total_fines")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting top offenders: " + e.getMessage());
        }
        return list;
    }
}
