package dao;

import model.Driver;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverDAO {
    public boolean createDriver(Driver driver) {
        String sql = "INSERT INTO drivers (license_number, name, email, phone, address) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, driver.getLicenseNumber());
            pstmt.setString(2, driver.getName());
            pstmt.setString(3, driver.getEmail());
            pstmt.setString(4, driver.getPhone());
            pstmt.setString(5, driver.getAddress());
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        driver.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating driver: " + e.getMessage());
        }
        return false;
    }

    public Driver getDriverById(int id) {
        String sql = "SELECT * FROM drivers WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Driver(
                        rs.getInt("id"),
                        rs.getString("license_number"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting driver by ID: " + e.getMessage());
        }
        return null;
    }

    public Driver getDriverByLicense(String licenseNumber) {
        String sql = "SELECT * FROM drivers WHERE license_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, licenseNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Driver(
                        rs.getInt("id"),
                        rs.getString("license_number"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting driver by license number: " + e.getMessage());
        }
        return null;
    }

    public boolean updateDriver(Driver driver) {
        String sql = "UPDATE drivers SET license_number = ?, name = ?, email = ?, phone = ?, address = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, driver.getLicenseNumber());
            pstmt.setString(2, driver.getName());
            pstmt.setString(3, driver.getEmail());
            pstmt.setString(4, driver.getPhone());
            pstmt.setString(5, driver.getAddress());
            pstmt.setInt(6, driver.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating driver: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteDriver(int id) {
        String sql = "DELETE FROM drivers WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting driver: " + e.getMessage());
        }
        return false;
    }

    public List<Driver> getAllDrivers() {
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT * FROM drivers";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                drivers.add(new Driver(
                    rs.getInt("id"),
                    rs.getString("license_number"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error listing drivers: " + e.getMessage());
        }
        return drivers;
    }
}
