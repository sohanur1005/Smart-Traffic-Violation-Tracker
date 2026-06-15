package controller;

import dao.UserDAO;
import model.Driver;
import model.User;
import service.DriverService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import util.DBConnection;

public class DriverController {
    @FXML
    private TableView<Driver> driverTable;
    @FXML
    private TableColumn<Driver, Integer> colId;
    @FXML
    private TableColumn<Driver, String> colLicense;
    @FXML
    private TableColumn<Driver, String> colName;
    @FXML
    private TableColumn<Driver, String> colEmail;
    @FXML
    private TableColumn<Driver, String> colPhone;

    @FXML
    private TextField searchField;
    @FXML
    private TextField licenseField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextArea addressField;
    @FXML
    private Label statusLabel;

    private final DriverService driverService = new DriverService();
    private final ObservableList<Driver> driverList = FXCollections.observableArrayList();
    private Driver selectedDriver = null;

    @FXML
    public void initialize() {
        // Map table columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colLicense.setCellValueFactory(new PropertyValueFactory<>("licenseNumber"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // Selection listener to populate form
        driverTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedDriver = newSelection;
                licenseField.setText(selectedDriver.getLicenseNumber());
                nameField.setText(selectedDriver.getName());
                emailField.setText(selectedDriver.getEmail());
                phoneField.setText(selectedDriver.getPhone());
                addressField.setText(selectedDriver.getAddress());
                
                statusLabel.setVisible(false);
            }
        });

        loadDrivers();
    }

    private void loadDrivers() {
        driverList.clear();
        driverList.addAll(driverService.getAllDrivers());
        driverTable.setItems(driverList);
    }

    @FXML
    public void handleSearch(KeyEvent event) {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            driverTable.setItems(driverList);
            return;
        }

        List<Driver> filtered = new ArrayList<>();
        for (Driver d : driverList) {
            if (d.getName().toLowerCase().contains(query) || d.getLicenseNumber().toLowerCase().contains(query)) {
                filtered.add(d);
            }
        }
        driverTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    public void handleAddDriver(ActionEvent event) {
        String license = licenseField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        if (license.isEmpty() || name.isEmpty()) {
            showStatus("License and Name are required!", true);
            return;
        }

        boolean success = driverService.registerDriver(license, name, email, phone, address);
        if (success) {
            showStatus("Driver registered successfully.", false);
            clearForm(null);
            loadDrivers();
        } else {
            showStatus("Registration failed. License might already exist.", true);
        }
    }

    @FXML
    public void handleUpdateDriver(ActionEvent event) {
        if (selectedDriver == null) {
            showStatus("Please select a driver from the table first.", true);
            return;
        }

        String license = licenseField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        if (license.isEmpty() || name.isEmpty()) {
            showStatus("License and Name are required!", true);
            return;
        }

        selectedDriver.setLicenseNumber(license);
        selectedDriver.setName(name);
        selectedDriver.setEmail(email);
        selectedDriver.setPhone(phone);
        selectedDriver.setAddress(address);

        boolean success = driverService.updateDriver(selectedDriver);
        if (success) {
            showStatus("Driver details updated.", false);
            clearForm(null);
            loadDrivers();
        } else {
            showStatus("Failed to update driver details.", true);
        }
    }

    @FXML
    public void handleDeleteDriver(ActionEvent event) {
        if (selectedDriver == null) {
            showStatus("Please select a driver from the table first.", true);
            return;
        }

        boolean success = driverService.deleteDriver(selectedDriver.getId());
        if (success) {
            showStatus("Driver and associated vehicles deleted.", false);
            clearForm(null);
            loadDrivers();
        } else {
            showStatus("Failed to delete driver.", true);
        }
    }

    @FXML
    public void clearForm(ActionEvent event) {
        licenseField.clear();
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        addressField.clear();
        selectedDriver = null;
        driverTable.getSelectionModel().clearSelection();
        if (event != null) {
            statusLabel.setVisible(false);
        }
    }

    @FXML
    public void handleLinkUserAccount(ActionEvent event) {
        if (selectedDriver == null) {
            showStatus("Please select a driver from the table first.", true);
            return;
        }

        // Fetch all USER-role accounts from DB
        Map<String, Integer> userOptions = new LinkedHashMap<>();
        String sql = "SELECT id, username, full_name FROM users WHERE role = 'USER' ORDER BY full_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String label = rs.getString("full_name") + " (@" + rs.getString("username") + ")";
                userOptions.put(label, rs.getInt("id"));
            }
        } catch (SQLException e) {
            showStatus("Error loading user accounts: " + e.getMessage(), true);
            return;
        }

        if (userOptions.isEmpty()) {
            showStatus("No USER-role accounts found. Register users via the Register screen.", true);
            return;
        }

        List<String> choices = new ArrayList<>(userOptions.keySet());
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Link User Account");
        dialog.setHeaderText("Link driver: " + selectedDriver.getName()
                + " (" + selectedDriver.getLicenseNumber() + ")");
        dialog.setContentText("Select the user account to link:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(chosen -> {
            int userId = userOptions.get(chosen);
            UserDAO userDAO = new UserDAO();
            boolean success = userDAO.linkDriverToUser(userId, selectedDriver.getId());
            if (success) {
                showStatus("✅ Account '" + chosen + "' linked to driver '" + selectedDriver.getName() + "'.", false);
            } else {
                showStatus("Failed to link account. Please try again.", true);
            }
        });
    }

    private void showStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        if (isError) {
            statusLabel.setStyle("-fx-text-fill: #e53e3e;");
        } else {
            statusLabel.setStyle("-fx-text-fill: #38a169;");
        }
        statusLabel.setVisible(true);
    }
}
