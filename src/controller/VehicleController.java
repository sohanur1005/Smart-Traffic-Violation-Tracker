package controller;

import model.Driver;
import model.Vehicle;
import service.DriverService;
import service.VehicleService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

public class VehicleController {
    @FXML
    private TableView<Vehicle> vehicleTable;
    @FXML
    private TableColumn<Vehicle, Integer> colId;
    @FXML
    private TableColumn<Vehicle, String> colPlate;
    @FXML
    private TableColumn<Vehicle, String> colOwner;
    @FXML
    private TableColumn<Vehicle, String> colModel;
    @FXML
    private TableColumn<Vehicle, String> colColor;
    @FXML
    private TableColumn<Vehicle, String> colType;

    @FXML
    private TextField searchField;
    @FXML
    private TextField plateField;
    @FXML
    private ComboBox<Driver> ownerComboBox;
    @FXML
    private TextField modelField;
    @FXML
    private TextField colorField;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private Label statusLabel;

    private final VehicleService vehicleService = new VehicleService();
    private final DriverService driverService = new DriverService();
    private final ObservableList<Vehicle> vehicleList = FXCollections.observableArrayList();
    private Vehicle selectedVehicle = null;

    @FXML
    public void initialize() {
        // Map columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPlate.setCellValueFactory(new PropertyValueFactory<>("plateNumber"));
        colOwner.setCellValueFactory(cellData -> {
            Driver d = driverService.getDriverById(cellData.getValue().getOwnerId());
            return new SimpleStringProperty(d != null ? d.getName() : "Unknown");
        });
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));

        // Setup StringConverter for ComboBox to display Driver profiles
        ownerComboBox.setConverter(new StringConverter<Driver>() {
            @Override
            public String toString(Driver driver) {
                return driver == null ? "" : driver.getName() + " (" + driver.getLicenseNumber() + ")";
            }

            @Override
            public Driver fromString(String string) {
                return null;
            }
        });

        // Load ComboBox options
        typeComboBox.setItems(FXCollections.observableArrayList("Sedan", "SUV", "Truck", "Motorcycle", "Van", "Coupe"));

        // Table Selection listener
        vehicleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedVehicle = newSelection;
                plateField.setText(selectedVehicle.getPlateNumber());
                modelField.setText(selectedVehicle.getModel());
                colorField.setText(selectedVehicle.getColor());
                typeComboBox.setValue(selectedVehicle.getType());

                Driver owner = driverService.getDriverById(selectedVehicle.getOwnerId());
                ownerComboBox.setValue(owner);

                statusLabel.setVisible(false);
            }
        });

        loadDriversList();
        loadVehicles();
    }

    private void loadDriversList() {
        List<Driver> drivers = driverService.getAllDrivers();
        ownerComboBox.setItems(FXCollections.observableArrayList(drivers));
    }

    private void loadVehicles() {
        vehicleList.clear();
        vehicleList.addAll(vehicleService.getAllVehicles());
        vehicleTable.setItems(vehicleList);
    }

    @FXML
    public void handleSearch(KeyEvent event) {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            vehicleTable.setItems(vehicleList);
            return;
        }

        List<Vehicle> filtered = new ArrayList<>();
        for (Vehicle v : vehicleList) {
            if (v.getPlateNumber().toLowerCase().contains(query)) {
                filtered.add(v);
            }
        }
        vehicleTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    public void handleAddVehicle(ActionEvent event) {
        String plate = plateField.getText().trim();
        Driver owner = ownerComboBox.getValue();
        String model = modelField.getText().trim();
        String color = colorField.getText().trim();
        String type = typeComboBox.getValue();

        if (plate.isEmpty() || owner == null) {
            showStatus("Plate Number and Owner Driver are required!", true);
            return;
        }

        boolean success = vehicleService.registerVehicle(plate, owner.getId(), model, color, type);
        if (success) {
            showStatus("Vehicle registered successfully.", false);
            clearForm(null);
            loadVehicles();
        } else {
            showStatus("Failed to register vehicle. Plate might already exist.", true);
        }
    }

    @FXML
    public void handleUpdateVehicle(ActionEvent event) {
        if (selectedVehicle == null) {
            showStatus("Please select a vehicle from the table first.", true);
            return;
        }

        String plate = plateField.getText().trim();
        Driver owner = ownerComboBox.getValue();
        String model = modelField.getText().trim();
        String color = colorField.getText().trim();
        String type = typeComboBox.getValue();

        if (plate.isEmpty() || owner == null) {
            showStatus("Plate Number and Owner Driver are required!", true);
            return;
        }

        selectedVehicle.setPlateNumber(plate);
        selectedVehicle.setOwnerId(owner.getId());
        selectedVehicle.setModel(model);
        selectedVehicle.setColor(color);
        selectedVehicle.setType(type);

        boolean success = vehicleService.updateVehicle(selectedVehicle);
        if (success) {
            showStatus("Vehicle registration updated.", false);
            clearForm(null);
            loadVehicles();
        } else {
            showStatus("Failed to update vehicle details.", true);
        }
    }

    @FXML
    public void handleDeleteVehicle(ActionEvent event) {
        if (selectedVehicle == null) {
            showStatus("Please select a vehicle from the table first.", true);
            return;
        }

        boolean success = vehicleService.deleteVehicle(selectedVehicle.getId());
        if (success) {
            showStatus("Vehicle details deleted.", false);
            clearForm(null);
            loadVehicles();
        } else {
            showStatus("Failed to delete vehicle.", true);
        }
    }

    @FXML
    public void clearForm(ActionEvent event) {
        plateField.clear();
        ownerComboBox.setValue(null);
        modelField.clear();
        colorField.clear();
        typeComboBox.setValue(null);
        selectedVehicle = null;
        vehicleTable.getSelectionModel().clearSelection();
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
