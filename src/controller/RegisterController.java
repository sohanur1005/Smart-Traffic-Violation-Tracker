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

public class RegisterController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Label messageLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void handleRegister(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null || role.isEmpty()) {
            messageLabel.setText("Please fill in all fields.");
            messageLabel.setStyle("-fx-text-fill: #e53e3e; -fx-font-size: 12px;");
            messageLabel.setVisible(true);
            return;
        }

        if (!"user".equalsIgnoreCase(role) && !"admin".equalsIgnoreCase(role)) {
            messageLabel.setText("Invalid role selection. Only 'user' and 'admin' are allowed.");
            messageLabel.setStyle("-fx-text-fill: #e53e3e; -fx-font-size: 12px;");
            messageLabel.setVisible(true);
            return;
        }

        // We use username as full_name to satisfy the database non-null constraint
        boolean registered = authService.register(username, password, role, username);
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
