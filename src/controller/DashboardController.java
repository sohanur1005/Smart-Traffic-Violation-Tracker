package controller;

import app.Main;
import model.*;
import model.Record;
import service.*;
import util.SceneManager;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class DashboardController {
    @FXML
    private Label userNameLabel;
    @FXML
    private Label userRoleLabel;
    @FXML
    private Label headerTitle;
    @FXML
    private StackPane contentArea;
    @FXML
    private VBox homeViewPane;

    // Stats
    @FXML
    private Label statDriversCount;
    @FXML
    private Label statVehiclesCount;
    @FXML
    private Label statTicketsCount;
    @FXML
    private Label statFinesUnpaid;

    // Sidebar navigation buttons
    @FXML
    private Button btnHome;
    @FXML
    private Button btnDrivers;
    @FXML
    private Button btnVehicles;
    @FXML
    private Button btnViolations;
    @FXML
    private Button btnRecords;

    // Recent Violations Table
    @FXML
    private TableView<Record> recentViolationsTable;
    @FXML
    private TableColumn<Record, Integer> colId;
    @FXML
    private TableColumn<Record, String> colDriver;
    @FXML
    private TableColumn<Record, String> colPlate;
    @FXML
    private TableColumn<Record, String> colCode;
    @FXML
    private TableColumn<Record, Double> colFine;
    @FXML
    private TableColumn<Record, String> colDate;
    @FXML
    private TableColumn<Record, String> colStatus;

    private final DriverService driverService = new DriverService();
    private final VehicleService vehicleService = new VehicleService();
    private final ViolationService violationService = new ViolationService();
    private final RecordService recordService = new RecordService();

    @FXML
    public void initialize() {
        // Load logged in user details
        User user = Main.getCurrentUser();
        if (user != null) {
            userNameLabel.setText("Officer: " + user.getFullName());
            userRoleLabel.setText("Role: " + user.getRole());
        }

        // Set Home Active
        setActiveButton(btnHome);

        // Bind columns for recent violations table
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDriver.setCellValueFactory(cellData -> {
            Driver d = driverService.getDriverById(cellData.getValue().getDriverId());
            return new SimpleStringProperty(d != null ? d.getName() : "Unknown");
        });
        colPlate.setCellValueFactory(cellData -> {
            Vehicle v = vehicleService.getVehicleById(cellData.getValue().getVehicleId());
            return new SimpleStringProperty(v != null ? v.getPlateNumber() : "Unknown");
        });
        colCode.setCellValueFactory(cellData -> {
            Violation v = violationService.getViolationById(cellData.getValue().getViolationId());
            return new SimpleStringProperty(v != null ? v.getCode() : "Unknown");
        });
        colFine.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getFineAmount()).asObject());
        colDate.setCellValueFactory(cellData -> {
            String dateStr = cellData.getValue().getViolationDate().toString();
            return new SimpleStringProperty(dateStr.length() > 16 ? dateStr.substring(0, 16) : dateStr);
        });
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadDashboardStats();
    }

    private void loadDashboardStats() {
        // Fetch counters
        int driversCount = driverService.getAllDrivers().size();
        int vehiclesCount = vehicleService.getAllVehicles().size();
        
        List<Record> allRecords = recordService.getAllRecords();
        int ticketsCount = allRecords.size();

        double unpaidFinesTotal = 0;
        for (Record r : allRecords) {
            if ("UNPAID".equals(r.getStatus())) {
                unpaidFinesTotal += r.getFineAmount();
            }
        }

        statDriversCount.setText(String.valueOf(driversCount));
        statVehiclesCount.setText(String.valueOf(vehiclesCount));
        statTicketsCount.setText(String.valueOf(ticketsCount));
        statFinesUnpaid.setText(String.format("$%.2f", unpaidFinesTotal));

        // Populate table (up to 15 recent records)
        int size = Math.min(allRecords.size(), 15);
        List<Record> recentRecords = allRecords.subList(0, size);
        recentViolationsTable.setItems(FXCollections.observableArrayList(recentRecords));
    }

    @FXML
    public void showHomeView(ActionEvent event) {
        headerTitle.setText("Dashboard Home");
        setActiveButton(btnHome);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(homeViewPane);
        loadDashboardStats();
    }

    @FXML
    public void showDriversView(ActionEvent event) {
        headerTitle.setText("Driver Records Management");
        setActiveButton(btnDrivers);
        loadSubView("/view/driver.fxml");
    }

    @FXML
    public void showVehiclesView(ActionEvent event) {
        headerTitle.setText("Vehicle Registration Database");
        setActiveButton(btnVehicles);
        loadSubView("/view/vehicle.fxml");
    }

    @FXML
    public void showViolationsView(ActionEvent event) {
        headerTitle.setText("Traffic Violation Types Configuration");
        setActiveButton(btnViolations);
        loadSubView("/view/violation.fxml");
    }

    @FXML
    public void showRecordsView(ActionEvent event) {
        headerTitle.setText("Issue Traffic Ticket Citation");
        setActiveButton(btnRecords);
        loadSubView("/view/record.fxml");
    }

    private void loadSubView(String fxmlPath) {
        Parent view = SceneManager.loadFXML(fxmlPath);
        if (view != null) {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        }
    }

    private void setActiveButton(Button clickedButton) {
        // Clear active style from all sidebar buttons
        btnHome.getStyleClass().remove("nav-button-active");
        btnDrivers.getStyleClass().remove("nav-button-active");
        btnVehicles.getStyleClass().remove("nav-button-active");
        btnViolations.getStyleClass().remove("nav-button-active");
        btnRecords.getStyleClass().remove("nav-button-active");

        // Add active style to clicked button
        clickedButton.getStyleClass().add("nav-button-active");
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        Main.setCurrentUser(null);
        SceneManager.switchToScene("/view/login.fxml");
    }
}
