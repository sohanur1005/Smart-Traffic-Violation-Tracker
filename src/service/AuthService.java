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
        if (userDAO.getUserByUsername(username) != null) {
            System.out.println("Username already exists.");
            return false;
        }
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) return false;

        User user = new User(0, username, hashedPassword, role.toUpperCase(), fullName);
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
