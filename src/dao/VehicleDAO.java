package dao;

import model.Vehicle;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {
    public boolean createVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles (plate_number, owner_id, model, color, type) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, vehicle.getPlateNumber());
            pstmt.setInt(2, vehicle.getOwnerId());
            pstmt.setString(3, vehicle.getModel());
            pstmt.setString(4, vehicle.getColor());
            pstmt.setString(5, vehicle.getType());
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        vehicle.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating vehicle: " + e.getMessage());
        }
        return false;
    }

    public Vehicle getVehicleById(int id) {
        String sql = "SELECT * FROM vehicles WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Vehicle(
                        rs.getInt("id"),
                        rs.getString("plate_number"),
                        rs.getInt("owner_id"),
                        rs.getString("model"),
                        rs.getString("color"),
                        rs.getString("type")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting vehicle by ID: " + e.getMessage());
        }
        return null;
    }

    public Vehicle getVehicleByPlate(String plateNumber) {
        String sql = "SELECT * FROM vehicles WHERE plate_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plateNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Vehicle(
                        rs.getInt("id"),
                        rs.getString("plate_number"),
                        rs.getInt("owner_id"),
                        rs.getString("model"),
                        rs.getString("color"),
                        rs.getString("type")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting vehicle by plate: " + e.getMessage());
        }
        return null;
    }

    public boolean updateVehicle(Vehicle vehicle) {
        String sql = "UPDATE vehicles SET plate_number = ?, owner_id = ?, model = ?, color = ?, type = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vehicle.getPlateNumber());
            pstmt.setInt(2, vehicle.getOwnerId());
            pstmt.setString(3, vehicle.getModel());
            pstmt.setString(4, vehicle.getColor());
            pstmt.setString(5, vehicle.getType());
            pstmt.setInt(6, vehicle.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating vehicle: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteVehicle(int id) {
        String sql = "DELETE FROM vehicles WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting vehicle: " + e.getMessage());
        }
        return false;
    }

    public List<Vehicle> getVehiclesByOwner(int ownerId) {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE owner_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ownerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(new Vehicle(
                        rs.getInt("id"),
                        rs.getString("plate_number"),
                        rs.getInt("owner_id"),
                        rs.getString("model"),
                        rs.getString("color"),
                        rs.getString("type")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting vehicles by owner: " + e.getMessage());
        }
        return vehicles;
    }

    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                vehicles.add(new Vehicle(
                    rs.getInt("id"),
                    rs.getString("plate_number"),
                    rs.getInt("owner_id"),
                    rs.getString("model"),
                    rs.getString("color"),
                    rs.getString("type")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error listing all vehicles: " + e.getMessage());
        }
        return vehicles;
    }
}
