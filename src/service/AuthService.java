package service;

import dao.UserDAO;
import model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthService {
    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public boolean register(String username, String password, String role, String fullName) {
        String license = null;
        if ("USER".equalsIgnoreCase(role)) {
            license = username; // default to username as license number for backward-compatibility / seed
        }
        return register(username, password, role, fullName, license);
    }

    public boolean register(String username, String password, String role, String fullName, Integer driverId) {
        if (userDAO.getUserByUsername(username) != null) {
            System.out.println("Username already exists.");
            return false;
        }
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) return false;

        User user = new User(0, username, hashedPassword, role.toUpperCase(), fullName, driverId);
        return userDAO.createUser(user);
    }

    public boolean register(String username, String password, String role, String fullName, String licenseNumber) {
        if (userDAO.getUserByUsername(username) != null) {
            System.out.println("Username already exists.");
            return false;
        }
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) return false;

        User user = new User(0, username, hashedPassword, role.toUpperCase(), fullName);

        if ("USER".equalsIgnoreCase(role)) {
            String licToUse = (licenseNumber != null && !licenseNumber.trim().isEmpty()) ? licenseNumber.trim() : username.trim();
            dao.DriverDAO driverDAO = new dao.DriverDAO();
            model.Driver existingDriver = driverDAO.getDriverByLicense(licToUse);
            if (existingDriver == null) {
                // Check if driver exists by full name match
                for (model.Driver d : driverDAO.getAllDrivers()) {
                    if (d.getName().equalsIgnoreCase(fullName)) {
                        existingDriver = d;
                        break;
                    }
                }
            }

            if (existingDriver != null) {
                // Check if this driver is already linked to another user account
                boolean alreadyLinked = false;
                String checkSql = "SELECT id FROM users WHERE driver_id = ?";
                try (java.sql.Connection conn = util.DBConnection.getConnection();
                     java.sql.PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                    pstmt.setInt(1, existingDriver.getId());
                    try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            alreadyLinked = true;
                        }
                    }
                } catch (java.sql.SQLException e) {
                    System.err.println("Error checking driver link: " + e.getMessage());
                }

                if (alreadyLinked) {
                    System.out.println("Driver is already linked to another user account.");
                    return false;
                }
                user.setDriverId(existingDriver.getId());
            } else {
                // Create new driver profile
                model.Driver newDriver = new model.Driver(0, licToUse, fullName, "", "", "");
                if (driverDAO.createDriver(newDriver)) {
                    user.setDriverId(newDriver.getId());
                } else {
                    System.out.println("Failed to create driver profile during registration.");
                    return false;
                }
            }
        }

        return userDAO.createUser(user);
    }

    public User login(String username, String password) {
        User user = userDAO.getUserByUsername(username);
        if (user == null) {
            return null;
        }
        String hashedPassword = hashPassword(password);
        if (hashedPassword != null && hashedPassword.equals(user.getPassword())) {
            return user;
        }
        return null;
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error hashing password: " + e.getMessage());
            return null;
        }
    }
}
