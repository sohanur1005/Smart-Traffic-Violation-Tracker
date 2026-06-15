package controller;

import app.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.User;
import service.AuthService;
import util.SceneManager;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            errorLabel.setVisible(true);
            return;
        }

        User user = authService.login(username, password);
        if (user != null) {
            util.Session.currentUser = user;
            Main.setCurrentUser(user);
            // Switch to Dashboard
            SceneManager.switchToScene("/view/dashboard.fxml");
        } else {
            errorLabel.setText("Invalid username or password. Check database configs.");
            errorLabel.setVisible(true);
        }
    }

    @FXML
    public void goToRegister(ActionEvent event) {
        SceneManager.switchToScene("/view/register.fxml");
    }
}
