package app;

import javafx.application.Application;
import javafx.stage.Stage;
import util.SceneManager;
import model.User;

public class Main extends Application {
    private static Stage primaryStage;
    private static User currentUser = null;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("Smart Traffic Violation Tracker");
        
        // Setup Stage reference
        SceneManager.setStage(primaryStage);
        
        // Switch to login scene
        SceneManager.switchToScene("/view/login.fxml");
        
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void main(String[] args) {
        // Run DB migrations
        try {
            util.DBConnection.getConnection().close();
        } catch (Exception e) {
            System.err.println("Could not establish a database connection on boot: " + e.getMessage());
        }
        
        // Seed default database table values
        seedData();

        launch(args);
    }

    private static void seedData() {
        service.AuthService authService = new service.AuthService();
        service.DriverService driverService = new service.DriverService();
        service.VehicleService vehicleService = new service.VehicleService();
        service.ViolationService violationService = new service.ViolationService();
        
        dao.UserDAO userDAO = new dao.UserDAO();
        if (userDAO.getUserByUsername("admin") == null) {
            System.out.println("Seeding database with sample admin, officer, drivers, vehicles, and violations...");
            
            authService.register("admin", "admin123", "ADMIN", "System Administrator");
            authService.register("officer1", "officer123", "OFFICER", "Officer Robert");
            
            driverService.registerDriver("DL10001", "John Doe", "john@example.com", "555-0101", "123 Elm St");
            driverService.registerDriver("DL10002", "Jane Smith", "jane@example.com", "555-0102", "456 Oak St");
            
            model.Driver john = driverService.getDriverByLicense("DL10001");
            model.Driver jane = driverService.getDriverByLicense("DL10002");
            
            if (john != null) {
                vehicleService.registerVehicle("ABC-1234", john.getId(), "Tesla Model 3", "Red", "Sedan");
            }
            if (jane != null) {
                vehicleService.registerVehicle("XYZ-9876", jane.getId(), "Ford F-150", "Blue", "Truck");
            }
            
            violationService.addViolationType("SPEED_LIMIT", "Exceeding Speed Limit", 150.00);
            violationService.addViolationType("RED_LIGHT", "Running Red Light", 200.00);
            violationService.addViolationType("DUI", "Driving Under Influence", 1000.00);
            violationService.addViolationType("NO_SEATBELT", "Not Wearing Seatbelt", 50.00);
            
            System.out.println("Seeding finished.");
        }
    }
}
