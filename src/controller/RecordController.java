package controller;

import app.Main;
import model.Driver;
import model.Record;
import model.User;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
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
    private ComboBox<Driver> driverCombo;
    @FXML
    private Label offenderBadge;
    @FXML
    private ComboBox<Vehicle> vehicleCombo;
    @FXML
    private ComboBox<Violation> violationCombo;
    @FXML
    private Label fineLabel;
    @FXML
    private Label fineSurchargeNote;
    @FXML
    private TextArea notesField;
    @FXML
    private Label statusLabel;
    @FXML
    private Button issueFineButton;

    // Search & Filter Fields
    @FXML
    private TextField searchLicenseField;
    @FXML
    private ComboBox<Violation> filterViolationComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;

    // Review Payment Panel fields
    @FXML
    private VBox issueCitationFormContainer;
    @FXML
    private VBox reviewPaymentFormContainer;
    @FXML
    private Label reviewTicketId;
    @FXML
    private Label reviewDriverName;
    @FXML
    private Label reviewPlateNumber;
    @FXML
    private Label reviewFineAmount;
    @FXML
    private Label reviewIncidentNotes;
    @FXML
    private Button btnApprovePayment;
    @FXML
    private Button btnRejectPayment;

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
            java.sql.Timestamp date = cellData.getValue().getViolationDate();
            String dateStr = date != null ? date.toString() : "";
            return new SimpleStringProperty(dateStr.length() > 16 ? dateStr.substring(0, 16) : dateStr);
        });
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(col -> new TableCell<Record, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                getStyleClass().removeAll("status-unpaid", "status-pending", "status-paid", "status-rejected");
                if (empty || status == null) {
                    setText(null);
                } else {
                    setText(status);
                    switch (status.toUpperCase()) {
                        case "UNPAID"   -> getStyleClass().add("status-unpaid");
                        case "PENDING"  -> getStyleClass().add("status-pending");
                        case "PAID"     -> getStyleClass().add("status-paid");
                        case "REJECTED" -> getStyleClass().add("status-rejected");
                    }
                }
            }
        });

        // Setup converters
        driverCombo.setConverter(new StringConverter<Driver>() {
            @Override
            public String toString(Driver d) {
                return d == null ? "" : d.getName() + " (" + d.getLicenseNumber() + ")";
            }
            @Override
            public Driver fromString(String string) { return null; }
        });

        vehicleCombo.setConverter(new StringConverter<Vehicle>() {
            @Override
            public String toString(Vehicle v) {
                return v == null ? "" : v.getPlateNumber() + " (" + v.getModel() + ")";
            }
            @Override
            public Vehicle fromString(String string) { return null; }
        });

        violationCombo.setConverter(new StringConverter<Violation>() {
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
                checkAndShowReviewPanel(newSel);
            } else {
                selectedRecord = null;
                showIssueForm();
            }
        });

        // Filter ComboBox converter
        filterViolationComboBox.setConverter(new StringConverter<Violation>() {
            @Override
            public String toString(Violation v) {
                return v == null ? "All Types" : v.getCode();
            }
            @Override
            public Violation fromString(String string) { return null; }
        });

        // Search & Filter Listeners
        searchLicenseField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        filterViolationComboBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        loadComboBoxData();
        loadRecords();
    }

    private void loadComboBoxData() {
        driverCombo.setItems(FXCollections.observableArrayList(driverService.getAllDrivers()));
        violationCombo.setItems(FXCollections.observableArrayList(violationService.getAllViolations()));

        // Populate filter combo box with "All Types" option represented by null
        ObservableList<Violation> filterViolations = FXCollections.observableArrayList();
        filterViolations.add(null);
        filterViolations.addAll(violationService.getAllViolations());
        filterViolationComboBox.setItems(filterViolations);
        filterViolationComboBox.setValue(null);
    }

    private void loadRecords() {
        recordList.clear();
        recordList.addAll(recordService.getAllRecords());
        applyFilters();
    }

    private void applyFilters() {
        String searchLicense = searchLicenseField.getText() == null ? "" : searchLicenseField.getText().trim().toLowerCase();
        Violation filterViolation = filterViolationComboBox.getValue();
        java.time.LocalDate startDate = startDatePicker.getValue();
        java.time.LocalDate endDate = endDatePicker.getValue();

        ObservableList<Record> filteredList = FXCollections.observableArrayList();
        for (Record record : recordList) {
            // Check driver license or name
            Driver d = driverService.getDriverById(record.getDriverId());
            boolean licenseMatch = searchLicense.isEmpty() || 
                                   (d != null && (d.getLicenseNumber().toLowerCase().contains(searchLicense) || 
                                                  d.getName().toLowerCase().contains(searchLicense)));

            // Check violation type
            boolean violationMatch = filterViolation == null || record.getViolationId() == filterViolation.getId();

            // Check date
            boolean dateMatch = true;
            if (record.getViolationDate() != null) {
                java.time.LocalDate recordDate = record.getViolationDate().toLocalDateTime().toLocalDate();
                if (startDate != null && recordDate.isBefore(startDate)) {
                    dateMatch = false;
                }
                if (endDate != null && recordDate.isAfter(endDate)) {
                    dateMatch = false;
                }
            } else if (startDate != null || endDate != null) {
                dateMatch = false;
            }

            if (licenseMatch && violationMatch && dateMatch) {
                filteredList.add(record);
            }
        }
        recordTable.setItems(filteredList);
    }

    @FXML
    public void handleResetFilters(ActionEvent event) {
        searchLicenseField.clear();
        filterViolationComboBox.setValue(null);
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        recordTable.setItems(recordList);
    }

    @FXML
    public void handleDriverSelected(ActionEvent event) {
        Driver driver = driverCombo.getValue();
        if (driver == null) {
            offenderBadge.setText("[STATUS: SELECT DRIVER]");
            offenderBadge.getStyleClass().removeAll("status-good", "status-warning", "status-critical");
            offenderBadge.getStyleClass().add("status-good");
            vehicleCombo.getItems().clear();
            updateFineDisplay();
            return;
        }

        // Load vehicles for this driver
        List<Vehicle> driverVehicles = vehicleService.getVehiclesByOwner(driver.getId());
        vehicleCombo.setItems(FXCollections.observableArrayList(driverVehicles));
        if (!driverVehicles.isEmpty()) {
            vehicleCombo.setValue(driverVehicles.get(0));
        } else {
            vehicleCombo.setValue(null);
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
        Driver driver = driverCombo.getValue();
        Violation violation = violationCombo.getValue();

        if (driver == null || violation == null) {
            fineLabel.setText("$0.00");
            fineSurchargeNote.setText("Select driver & violation type");
            return;
        }

        int count = recordService.getRecordsByDriver(driver.getId()).size();
        double fine = violation.getFineAmount();

        if (count >= 5) {
            double calculatedFine = fine * 1.20;
            fineLabel.setText(String.format("$%.2f", calculatedFine));
            fineSurchargeNote.setText(String.format("Base: $%.2f + 20%% Repeat Offender Surcharge", fine));
        } else {
            fineLabel.setText(String.format("$%.2f", fine));
            fineSurchargeNote.setText("Base fine amount");
        }
    }

    @FXML
    public void handleSubmitTicket(ActionEvent event) {
        Driver driver = driverCombo.getValue();
        Vehicle vehicle = vehicleCombo.getValue();
        Violation violation = violationCombo.getValue();
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
        driverCombo.setValue(null);
        vehicleCombo.setValue(null);
        violationCombo.setValue(null);
        notesField.clear();
        offenderBadge.setText("[STATUS: SELECT DRIVER]");
        offenderBadge.getStyleClass().removeAll("status-good", "status-warning", "status-critical");
        offenderBadge.getStyleClass().add("status-good");
        fineLabel.setText("$0.00");
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

    @FXML
    public void handleApprovePayment(ActionEvent event) {
        if (selectedRecord == null) return;
        boolean success = recordService.approvePayment(selectedRecord.getId());
        if (success) {
            showStatus(String.format("Payment approved for Ticket #%d.", selectedRecord.getId()), false);
            loadRecords();
            clearForm(null);
            showIssueForm();
        } else {
            showStatus("Failed to approve payment.", true);
        }
    }

    @FXML
    public void handleRejectPayment(ActionEvent event) {
        if (selectedRecord == null) return;
        boolean success = recordService.rejectPayment(selectedRecord.getId());
        if (success) {
            showStatus(String.format("Payment rejected for Ticket #%d.", selectedRecord.getId()), false);
            loadRecords();
            clearForm(null);
            showIssueForm();
        } else {
            showStatus("Failed to reject payment.", true);
        }
    }

    @FXML
    public void handleCancelReview(ActionEvent event) {
        recordTable.getSelectionModel().clearSelection();
        selectedRecord = null;
        showIssueForm();
    }

    private void checkAndShowReviewPanel(Record record) {
        User user = Main.getCurrentUser();
        boolean isAdmin = user != null && "ADMIN".equalsIgnoreCase(user.getRole());

        // Show review panel when admin selects a PENDING payment record
        if (record != null && "PENDING".equalsIgnoreCase(record.getStatus()) && isAdmin) {
            issueCitationFormContainer.setVisible(false);
            issueCitationFormContainer.setManaged(false);

            reviewPaymentFormContainer.setVisible(true);
            reviewPaymentFormContainer.setManaged(true);

            reviewTicketId.setText("#" + record.getId());
            Driver d = driverService.getDriverById(record.getDriverId());
            reviewDriverName.setText(d != null ? d.getName() : "Unknown");
            Vehicle v = vehicleService.getVehicleById(record.getVehicleId());
            reviewPlateNumber.setText(v != null ? v.getPlateNumber() : "Unknown");
            reviewFineAmount.setText(String.format("$%.2f", record.getFineAmount()));
            reviewIncidentNotes.setText(record.getNotes() != null ? record.getNotes() : "");
        } else {
            showIssueForm();
        }
    }

    private void showIssueForm() {
        issueCitationFormContainer.setVisible(true);
        issueCitationFormContainer.setManaged(true);
        
        reviewPaymentFormContainer.setVisible(false);
        reviewPaymentFormContainer.setManaged(false);
    }
}
