package service;

import dao.DriverDAO;
import model.Driver;

import java.util.List;

public class DriverService {
    private final DriverDAO driverDAO;

    public DriverService() {
        this.driverDAO = new DriverDAO();
    }

    public boolean registerDriver(String licenseNumber, String name, String email, String phone, String address) {
        if (driverDAO.getDriverByLicense(licenseNumber) != null) {
            System.out.println("Driver with license " + licenseNumber + " is already registered.");
            return false;
        }
        Driver driver = new Driver(0, licenseNumber, name, email, phone, address);
        return driverDAO.createDriver(driver);
    }

    public Driver getDriverByLicense(String licenseNumber) {
        return driverDAO.getDriverByLicense(licenseNumber);
    }

    public Driver getDriverByUserId(int userId) {
        return driverDAO.getDriverByUserId(userId);
    }

    public Driver getDriverById(int id) {
        return driverDAO.getDriverById(id);
    }

    public boolean updateDriver(Driver driver) {
        return driverDAO.updateDriver(driver);
    }

    public boolean deleteDriver(int id) {
        return driverDAO.deleteDriver(id);
    }

    public List<Driver> getAllDrivers() {
        return driverDAO.getAllDrivers();
    }
}
