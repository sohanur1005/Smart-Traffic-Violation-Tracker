package controller;

import app.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import service.AuthService;
import util.SceneManager;

import javafx.collections.FXCollections;
import javafx.util.StringConverter;
import model.Driver;
import service.DriverService;

public class RegisterController {
    @FXML
    private TextField fullNameField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Label licenseLabel;

    @FXML
    private ComboBox<Driver> driverComboBox;

    @FXML
    private Label messageLabel;

    private final AuthService authService = new AuthService();
    private final DriverService driverService = new DriverService();

    @FXML
    public void initialize() {
        // Setup converter for driverComboBox
        driverComboBox.setConverter(new StringConverter<Driver>() {
            @Override
            public String toString(Driver d) {
                return d == null ? "" : d.getName() + " (" + d.getLicenseNumber() + ")";
            }
            @Override
            public Driver fromString(String string) { return null; }
        });

        // Load driver combo items
        driverComboBox.setItems(FXCollections.observableArrayList(driverService.getAllDrivers()));

        roleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isUser = "USER".equalsIgnoreCase(newVal);
            if (licenseLabel != null) {
                licenseLabel.setVisible(isUser);
                licenseLabel.setManaged(isUser);
            }
            if (driverComboBox != null) {
                driverComboBox.setVisible(isUser);
                driverComboBox.setManaged(isUser);
            }
        });
        roleComboBox.setValue("USER");
    }

    @FXML
    public void handleRegister(ActionEvent event) {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleComboBox.getValue();
        Driver selectedDriver = (driverComboBox != null && role != null && "USER".equalsIgnoreCase(role)) ? driverComboBox.getValue() : null;

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || role == null || role.isEmpty() || ("USER".equalsIgnoreCase(role) && selectedDriver == null)) {
            messageLabel.setText("Please fill in all fields.");
            messageLabel.setStyle("-fx-text-fill: #e53e3e; -fx-font-size: 12px;");
            messageLabel.setVisible(true);
            return;
        }

        if (!"OFFICER".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role) && !"USER".equalsIgnoreCase(role)) {
            messageLabel.setText("Invalid role selection. Only 'USER', 'OFFICER', and 'ADMIN' are allowed.");
            messageLabel.setStyle("-fx-text-fill: #e53e3e; -fx-font-size: 12px;");
            messageLabel.setVisible(true);
            return;
        }

        boolean registered;
        if ("USER".equalsIgnoreCase(role) && selectedDriver != null) {
            registered = authService.register(username, password, role, fullName, selectedDriver.getId());
        } else {
            registered = authService.register(username, password, role, fullName);
        }
        
        if (registered) {
            messageLabel.setText("Registration successful! Redirecting to login...");
            messageLabel.setStyle("-fx-text-fill: #38a169; -fx-font-size: 12px;");
            messageLabel.setVisible(true);
            
            // Redirect back to login scene after a 1.5 seconds delay
            javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));
            delay.setOnFinished(e -> SceneManager.switchToScene("/view/login.fxml"));
            delay.play();
        } else {
            messageLabel.setText("Registration failed. Username may already exist.");
            messageLabel.setStyle("-fx-text-fill: #e53e3e; -fx-font-size: 12px;");
            messageLabel.setVisible(true);
        }
    }

    @FXML
    public void goToLogin(ActionEvent event) {
        SceneManager.switchToScene("/view/login.fxml");
    }
}
