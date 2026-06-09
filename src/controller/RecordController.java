package controller;

import app.Main;
import model.Driver;
import model.Record;
import model.Vehicle;
import model.Violation;
import service.DriverService;
import service.RecordService;
import service.VehicleService;
import service.ViolationService;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.util.List;

public class RecordController {
    @FXML
    private TableView<Record> recordTable;
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

    @FXML
    private ComboBox<Driver> driverComboBox;
    @FXML
    private Label offenderBadge;
    @FXML
    private ComboBox<Vehicle> vehicleComboBox;
    @FXML
    private ComboBox<Violation> violationComboBox;
    @FXML
    private Label fineValueLabel;
    @FXML
    private Label fineSurchargeNote;
    @FXML
    private TextArea notesField;
    @FXML
    private Label statusLabel;

    private final RecordService recordService = new RecordService();
    private final DriverService driverService = new DriverService();
    private final VehicleService vehicleService = new VehicleService();
    private final ViolationService violationService = new ViolationService();

    private final ObservableList<Record> recordList = FXCollections.observableArrayList();
    private Record selectedRecord = null;

    @FXML
    public void initialize() {
        // Map columns
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

        // Setup converters
        driverComboBox.setConverter(new StringConverter<Driver>() {
            @Override
            public String toString(Driver d) {
                return d == null ? "" : d.getName() + " (" + d.getLicenseNumber() + ")";
            }
            @Override
            public Driver fromString(String string) { return null; }
        });

        vehicleComboBox.setConverter(new StringConverter<Vehicle>() {
            @Override
            public String toString(Vehicle v) {
                return v == null ? "" : v.getPlateNumber() + " (" + v.getModel() + ")";
            }
            @Override
            public Vehicle fromString(String string) { return null; }
        });

        violationComboBox.setConverter(new StringConverter<Violation>() {
            @Override
            public String toString(Violation v) {
                return v == null ? "" : v.getCode() + " - " + v.getDescription();
            }
            @Override
            public Violation fromString(String string) { return null; }
        });

        // Add selection listener to TableView
        recordTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                selectedRecord = newSel;
                statusLabel.setVisible(false);
            }
        });

        loadComboBoxData();
        loadRecords();
    }

    private void loadComboBoxData() {
        driverComboBox.setItems(FXCollections.observableArrayList(driverService.getAllDrivers()));
        violationComboBox.setItems(FXCollections.observableArrayList(violationService.getAllViolations()));
    }

    private void loadRecords() {
        recordList.clear();
        recordList.addAll(recordService.getAllRecords());
        recordTable.setItems(recordList);
    }

    @FXML
    public void handleDriverSelected(ActionEvent event) {
        Driver driver = driverComboBox.getValue();
        if (driver == null) {
            offenderBadge.setText("[STATUS: SELECT DRIVER]");
            offenderBadge.getStyleClass().removeAll("status-good", "status-warning", "status-critical");
            offenderBadge.getStyleClass().add("status-good");
            vehicleComboBox.getItems().clear();
            updateFineDisplay();
            return;
        }

        // Load vehicles for this driver
        List<Vehicle> driverVehicles = vehicleService.getVehiclesByOwner(driver.getId());
        vehicleComboBox.setItems(FXCollections.observableArrayList(driverVehicles));
        if (!driverVehicles.isEmpty()) {
            vehicleComboBox.setValue(driverVehicles.get(0));
        } else {
            vehicleComboBox.setValue(null);
        }

        // Fetch violation count
        int count = recordService.getRecordsByDriver(driver.getId()).size();
        offenderBadge.getStyleClass().removeAll("status-good", "status-warning", "status-critical");
        if (count < 3) {
            offenderBadge.setText(String.format("STATUS: ACTIVE DRIVER (%d TICKETS)", count));
            offenderBadge.getStyleClass().add("status-good");
        } else if (count < 5) {
            offenderBadge.setText(String.format("WARNING: REPEAT OFFENDER (%d TICKETS)", count));
            offenderBadge.getStyleClass().add("status-warning");
        } else {
            offenderBadge.setText(String.format("CRITICAL: HABITUAL OFFENDER (%d TICKETS) - 20%% SURCHARGE!", count));
            offenderBadge.getStyleClass().add("status-critical");
        }

        updateFineDisplay();
    }

    @FXML
    public void handleViolationSelected(ActionEvent event) {
        updateFineDisplay();
    }

    private void updateFineDisplay() {
        Driver driver = driverComboBox.getValue();
        Violation violation = violationComboBox.getValue();

        if (driver == null || violation == null) {
            fineValueLabel.setText("$0.00");
            fineSurchargeNote.setText("Select driver & violation type");
            return;
        }

        int count = recordService.getRecordsByDriver(driver.getId()).size();
        double fine = violation.getFineAmount();

        if (count >= 5) {
            double calculatedFine = fine * 1.20;
            fineValueLabel.setText(String.format("$%.2f", calculatedFine));
            fineSurchargeNote.setText(String.format("Base: $%.2f + 20%% Repeat Offender Surcharge", fine));
        } else {
            fineValueLabel.setText(String.format("$%.2f", fine));
            fineSurchargeNote.setText("Base fine amount");
        }
    }

    @FXML
    public void handleSubmitTicket(ActionEvent event) {
        Driver driver = driverComboBox.getValue();
        Vehicle vehicle = vehicleComboBox.getValue();
        Violation violation = violationComboBox.getValue();
        String notes = notesField.getText().trim();

        if (driver == null || vehicle == null || violation == null) {
            showStatus("Driver, Vehicle, and Violation Type are required!", true);
            return;
        }

        int officerId = Main.getCurrentUser() != null ? Main.getCurrentUser().getId() : 1;

        boolean success = recordService.issueViolation(driver.getId(), vehicle.getId(), violation.getId(), officerId, notes);
        if (success) {
            showStatus("Traffic ticket issued successfully.", false);
            clearForm(null);
            loadRecords();
        } else {
            showStatus("Failed to submit traffic ticket.", true);
        }
    }

    @FXML
    public void handleCollectFine(ActionEvent event) {
        if (selectedRecord == null) {
            showStatus("Select a ticket record from the table to resolve.", true);
            return;
        }

        if ("PAID".equals(selectedRecord.getStatus())) {
            showStatus("Selected ticket is already paid.", true);
            return;
        }

        boolean success = recordService.payRecord(selectedRecord.getId());
        if (success) {
            showStatus(String.format("Payment received. Ticket #%d resolved.", selectedRecord.getId()), false);
            loadRecords();
            selectedRecord = null;
            recordTable.getSelectionModel().clearSelection();
        } else {
            showStatus("Failed to record payment.", true);
        }
    }

    @FXML
    public void clearForm(ActionEvent event) {
        driverComboBox.setValue(null);
        vehicleComboBox.setValue(null);
        violationComboBox.setValue(null);
        notesField.clear();
        offenderBadge.setText("[STATUS: SELECT DRIVER]");
        offenderBadge.getStyleClass().removeAll("status-good", "status-warning", "status-critical");
        offenderBadge.getStyleClass().add("status-good");
        fineValueLabel.setText("$0.00");
        fineSurchargeNote.setText("Select driver & violation type");
        selectedRecord = null;
        recordTable.getSelectionModel().clearSelection();
        if (event != null) {
            statusLabel.setVisible(false);
        }
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
