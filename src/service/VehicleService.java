package service;

import dao.VehicleDAO;
import model.Vehicle;

import java.util.List;

public class VehicleService {
    private final VehicleDAO vehicleDAO;

    public VehicleService() {
        this.vehicleDAO = new VehicleDAO();
    }

    public boolean registerVehicle(String plateNumber, int ownerId, String model, String color, String type) {
        if (vehicleDAO.getVehicleByPlate(plateNumber) != null) {
            System.out.println("Vehicle with plate number " + plateNumber + " is already registered.");
            return false;
        }
        Vehicle vehicle = new Vehicle(0, plateNumber, ownerId, model, color, type);
        return vehicleDAO.createVehicle(vehicle);
    }

    public Vehicle getVehicleByPlate(String plateNumber) {
        return vehicleDAO.getVehicleByPlate(plateNumber);
    }

    public Vehicle getVehicleById(int id) {
        return vehicleDAO.getVehicleById(id);
    }

    public boolean updateVehicle(Vehicle vehicle) {
        return vehicleDAO.updateVehicle(vehicle);
    }

    public boolean deleteVehicle(int id) {
        return vehicleDAO.deleteVehicle(id);
    }

    public List<Vehicle> getVehiclesByOwner(int ownerId) {
        return vehicleDAO.getVehiclesByOwner(ownerId);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleDAO.getAllVehicles();
    }
}
