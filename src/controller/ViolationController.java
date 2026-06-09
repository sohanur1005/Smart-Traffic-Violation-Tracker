package controller;

import model.Violation;
import service.ViolationService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ViolationController {
    @FXML
    private TableView<Violation> violationTable;
    @FXML
    private TableColumn<Violation, Integer> colId;
    @FXML
    private TableColumn<Violation, String> colCode;
    @FXML
    private TableColumn<Violation, String> colDescription;
    @FXML
    private TableColumn<Violation, Double> colFine;

    @FXML
    private TextField codeField;
    @FXML
    private TextArea descField;
    @FXML
    private TextField fineField;
    @FXML
    private Label statusLabel;

    private final ViolationService violationService = new ViolationService();
    private final ObservableList<Violation> violationList = FXCollections.observableArrayList();
    private Violation selectedViolation = null;

    @FXML
    public void initialize() {
        // Map columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colFine.setCellValueFactory(new PropertyValueFactory<>("fineAmount"));

        // Table selection listener
        violationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedViolation = newSelection;
                codeField.setText(selectedViolation.getCode());
                descField.setText(selectedViolation.getDescription());
                fineField.setText(String.valueOf(selectedViolation.getFineAmount()));
                
                statusLabel.setVisible(false);
            }
        });

        loadViolations();
    }

    private void loadViolations() {
        violationList.clear();
        violationList.addAll(violationService.getAllViolations());
        violationTable.setItems(violationList);
    }

    @FXML
    public void handleAddViolation(ActionEvent event) {
        String code = codeField.getText().trim().toUpperCase();
        String desc = descField.getText().trim();
        String fineStr = fineField.getText().trim();

        if (code.isEmpty() || desc.isEmpty() || fineStr.isEmpty()) {
            showStatus("All fields are required!", true);
            return;
        }

        double fine;
        try {
            fine = Double.parseDouble(fineStr);
        } catch (NumberFormatException e) {
            showStatus("Fine amount must be a decimal value.", true);
            return;
        }

        boolean success = violationService.addViolationType(code, desc, fine);
        if (success) {
            showStatus("Violation type configured successfully.", false);
            clearForm(null);
            loadViolations();
        } else {
            showStatus("Failed to save. Code may already exist.", true);
        }
    }

    @FXML
    public void handleUpdateViolation(ActionEvent event) {
        if (selectedViolation == null) {
            showStatus("Please select a violation type from the table first.", true);
            return;
        }

        String code = codeField.getText().trim().toUpperCase();
        String desc = descField.getText().trim();
        String fineStr = fineField.getText().trim();

        if (code.isEmpty() || desc.isEmpty() || fineStr.isEmpty()) {
            showStatus("All fields are required!", true);
            return;
        }

        double fine;
        try {
            fine = Double.parseDouble(fineStr);
        } catch (NumberFormatException e) {
            showStatus("Fine amount must be a decimal value.", true);
            return;
        }

        selectedViolation.setCode(code);
        selectedViolation.setDescription(desc);
        selectedViolation.setFineAmount(fine);

        boolean success = violationService.updateViolation(selectedViolation);
        if (success) {
            showStatus("Violation type updated.", false);
            clearForm(null);
            loadViolations();
        } else {
            showStatus("Failed to update violation configuration.", true);
        }
    }

    @FXML
    public void handleDeleteViolation(ActionEvent event) {
        if (selectedViolation == null) {
            showStatus("Please select a violation type from the table first.", true);
            return;
        }

        boolean success = violationService.deleteViolation(selectedViolation.getId());
        if (success) {
            showStatus("Violation type deleted.", false);
            clearForm(null);
            loadViolations();
        } else {
            showStatus("Failed to delete violation configuration.", true);
        }
    }

    @FXML
    public void clearForm(ActionEvent event) {
        codeField.clear();
        descField.clear();
        fineField.clear();
        selectedViolation = null;
        violationTable.getSelectionModel().clearSelection();
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
