package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBConnection {
    private static String dbUrl;
    private static String dbName;
    private static String dbUser;
    private static String dbPassword;
    
    static {
        loadConfig();
        initializeDatabase();
    }
    
    private static void loadConfig() {
        Properties props = new Properties();
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                props.load(input);
            } else {
                // Fallback to direct file loading
                try (InputStream fileInput = new FileInputStream("src/config.properties")) {
                    props.load(fileInput);
                }
            }
            dbUrl = props.getProperty("db.url", "jdbc:mysql://localhost:3306/");
            dbName = props.getProperty("db.name", "traffic_db");
            dbUser = props.getProperty("db.user", "root");
            dbPassword = props.getProperty("db.password", "");
        } catch (IOException ex) {
            System.err.println("Warning: Could not load config.properties. Using default settings.");
            dbUrl = "jdbc:mysql://localhost:3306/";
            dbName = "traffic_db";
            dbUser = "root";
            dbPassword = "";
        }
    }
    
    private static void initializeDatabase() {
        // Load Driver class to verify mysql connector is present
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found in classpath! Make sure mysql-connector-j.jar is included.");
        }

        // First connect without DB name to create it if it doesn't exist
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
            
        } catch (SQLException e) {
            System.err.println("Error creating database: " + e.getMessage());
        }

        // Now connect to the database and create tables
        String fullUrl = dbUrl.endsWith("/") ? dbUrl + dbName : dbUrl + "/" + dbName;
        if (!fullUrl.contains("?")) {
            fullUrl += "?useSSL=false&allowPublicKeyRetrieval=true";
        }
        
        try (Connection conn = DriverManager.getConnection(fullUrl, dbUser, dbPassword);
             Statement stmt = conn.createStatement()) {
            
            // 1. Users table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) UNIQUE NOT NULL, " +
                "password VARCHAR(255) NOT NULL, " +
                "role VARCHAR(20) NOT NULL, " +
                "full_name VARCHAR(100) NOT NULL, " +
                "driver_id INT DEFAULT NULL" +
                ")"
            );
            // Add driver_id column to existing users tables (safe migration)
            try {
                stmt.executeUpdate(
                    "ALTER TABLE users ADD COLUMN driver_id INT DEFAULT NULL"
                );
            } catch (SQLException ignore) {
                // Column already exists — ignore
            }
            
            // 2. Drivers table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS drivers (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "license_number VARCHAR(50) UNIQUE NOT NULL, " +
                "name VARCHAR(100) NOT NULL, " +
                "email VARCHAR(100), " +
                "phone VARCHAR(20), " +
                "address TEXT" +
                ")"
            );
            
            // Add foreign key constraint to users.driver_id pointing to drivers.id
            try {
                stmt.executeUpdate(
                    "ALTER TABLE users ADD CONSTRAINT fk_user_driver FOREIGN KEY (driver_id) REFERENCES drivers(id) ON DELETE SET NULL"
                );
            } catch (SQLException ignore) {
                // Constraint already exists - ignore
            }
            
            // 3. Vehicles table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS vehicles (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "plate_number VARCHAR(20) UNIQUE NOT NULL, " +
                "owner_id INT NOT NULL, " +
                "model VARCHAR(50), " +
                "color VARCHAR(20), " +
                "type VARCHAR(30), " +
                "FOREIGN KEY (owner_id) REFERENCES drivers(id) ON DELETE CASCADE" +
                ")"
            );
            
            // 4. Violations table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS violations (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "code VARCHAR(20) UNIQUE NOT NULL, " +
                "description VARCHAR(255) NOT NULL, " +
                "fine_amount DECIMAL(10, 2) NOT NULL" +
                ")"
            );
            
            // 5. Records table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS records (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "driver_id INT NOT NULL, " +
                "vehicle_id INT NOT NULL, " +
                "violation_id INT NOT NULL, " +
                "officer_id INT NOT NULL, " +
                "fine_amount DECIMAL(10, 2) NOT NULL, " +
                "violation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "status VARCHAR(20) DEFAULT 'UNPAID', " +
                "notes TEXT, " +
                "fine DECIMAL(10, 2) DEFAULT 0.00, " +
                "date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "payment_status VARCHAR(20) DEFAULT 'UNPAID', " +
                "FOREIGN KEY (driver_id) REFERENCES drivers(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (violation_id) REFERENCES violations(id), " +
                "FOREIGN KEY (officer_id) REFERENCES users(id)" +
                ")"
            );
            // Safe migrations to add columns to existing records table
            try {
                stmt.executeUpdate("ALTER TABLE records ADD COLUMN fine DECIMAL(10, 2) DEFAULT 0.00");
            } catch (SQLException ignore) {}
            try {
                stmt.executeUpdate("ALTER TABLE records ADD COLUMN date TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
            } catch (SQLException ignore) {}
            try {
                stmt.executeUpdate("ALTER TABLE records ADD COLUMN payment_status VARCHAR(20) DEFAULT 'UNPAID'");
            } catch (SQLException ignore) {}
            
            // 6. Notifications table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS notifications (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "driver_id INT NOT NULL, " +
                "record_id INT NOT NULL, " +
                "message VARCHAR(255) NOT NULL, " +
                "is_read BOOLEAN DEFAULT FALSE, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (driver_id) REFERENCES drivers(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (record_id) REFERENCES records(id) ON DELETE CASCADE" +
                ")"
            );

            // 7. Data migration: align legacy status values to current spec
            //    PENDING_APPROVAL  → PENDING
            //    PAYMENT_REJECTED  → REJECTED
            stmt.executeUpdate(
                "UPDATE records SET status = 'PENDING' WHERE status = 'PENDING_APPROVAL'"
            );
            stmt.executeUpdate(
                "UPDATE records SET status = 'REJECTED' WHERE status = 'PAYMENT_REJECTED'"
            );
            // 8. Auto-link USER accounts to drivers by full_name match (best-effort)
            stmt.executeUpdate(
                "UPDATE users u " +
                "JOIN drivers d ON LOWER(TRIM(d.name)) = LOWER(TRIM(u.full_name)) " +
                "SET u.driver_id = d.id " +
                "WHERE u.role = 'USER' AND u.driver_id IS NULL"
            );
            
        } catch (SQLException e) {
            System.err.println("Error initializing database tables: " + e.getMessage());
        }
    }
    
    public static Connection getConnection() throws SQLException {
        String fullUrl = dbUrl.endsWith("/") ? dbUrl + dbName : dbUrl + "/" + dbName;
        if (!fullUrl.contains("?")) {
            fullUrl += "?useSSL=false&allowPublicKeyRetrieval=true";
        }
        return DriverManager.getConnection(fullUrl, dbUser, dbPassword);
    }
}
