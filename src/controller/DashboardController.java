package controller;

import app.Main;
import model.*;
import model.Record;
import service.*;
import util.SceneManager;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

public class DashboardController {

    // ── Sidebar / Header ──────────────────────────────────────────────────────
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private Label headerTitle;
    @FXML private Label roleBadge;
    @FXML private StackPane contentArea;
    @FXML private VBox homeViewPane;

    // ── Stats ─────────────────────────────────────────────────────────────────
    @FXML private Label statDriversCount;
    @FXML private Label statVehiclesCount;
    @FXML private Label statTicketsCount;
    @FXML private Label statFinesUnpaid;
    @FXML private Label statCard1Title;
    @FXML private Label statCard2Title;
    @FXML private Label statCard3Title;
    @FXML private Label statCard4Title;

    // ── Notification Center (User Role) ───────────────────────────────────────
    @FXML private VBox notificationContainer;
    @FXML private Label notificationBadgeCount;
    @FXML private VBox notificationList;
    @FXML private Button btnMarkAllRead;

    // ── Admin Pending Payments Panel ──────────────────────────────────────────
    @FXML private VBox pendingPaymentsContainer;
    @FXML private Label pendingPaymentsBadge;
    @FXML private TableView<Record> pendingPaymentsTable;
    @FXML private TableColumn<Record, Integer> colPendingId;
    @FXML private TableColumn<Record, String>  colPendingDriver;
    @FXML private TableColumn<Record, String>  colPendingPlate;
    @FXML private TableColumn<Record, Double>  colPendingFine;
    @FXML private TableColumn<Record, String>  colPendingDate;

    // ── Recent Violations Table ───────────────────────────────────────────────
    @FXML private Label recentViolationsTitle;
    @FXML private HBox  userActionsRow;
    @FXML private Button btnPayFine;
    @FXML private VBox topOffendersContainer;

    @FXML private TableView<Record> recentViolationsTable;
    @FXML private TableColumn<Record, Integer> colId;
    @FXML private TableColumn<Record, String>  colDriver;
    @FXML private TableColumn<Record, String>  colPlate;
    @FXML private TableColumn<Record, String>  colCode;
    @FXML private TableColumn<Record, Double>  colFine;
    @FXML private TableColumn<Record, String>  colDate;
    @FXML private TableColumn<Record, String>  colStatus;

    // ── Top Offenders Table ───────────────────────────────────────────────────
    @FXML private TableView<TopOffender> topOffendersTable;
    @FXML private TableColumn<TopOffender, String>  colOffenderDriver;
    @FXML private TableColumn<TopOffender, String>  colOffenderLicense;
    @FXML private TableColumn<TopOffender, Integer> colOffenderTickets;
    @FXML private TableColumn<TopOffender, Double>  colOffenderFines;

    // ── Sidebar Buttons ───────────────────────────────────────────────────────
    @FXML private Button btnHome;
    @FXML private Button btnDrivers;
    @FXML private Button btnVehicles;
    @FXML private Button btnViolations;
    @FXML private Button btnRecords;
    @FXML private Button btnMyVehicles;
    @FXML private Button btnMyCitations;
    @FXML private Button btnMyProfile;

    // ── My Vehicles View ─────────────────────────────────────────────────────
    @FXML private VBox myVehiclesPane;
    @FXML private Label myVehiclesCountBadge;
    @FXML private TableView<Vehicle> myVehiclesTable;
    @FXML private TableColumn<Vehicle, Integer> colVehId;
    @FXML private TableColumn<Vehicle, String>  colVehPlate;
    @FXML private TableColumn<Vehicle, String>  colVehModel;
    @FXML private TableColumn<Vehicle, String>  colVehColor;
    @FXML private TableColumn<Vehicle, String>  colVehType;
    @FXML private TableColumn<Vehicle, Integer> colVehTickets;

    @FXML private VBox vehicleViolationsContainer;
    @FXML private Label vehicleViolationsTitle;
    @FXML private TableView<Record> vehicleViolationsTable;
    @FXML private TableColumn<Record, Integer> colVVTicketId;
    @FXML private TableColumn<Record, String>  colVVCode;
    @FXML private TableColumn<Record, Double>  colVVFine;
    @FXML private TableColumn<Record, String>  colVVDate;
    @FXML private TableColumn<Record, String>  colVVStatus;

    // ── My Citations View ─────────────────────────────────────────────────────
    @FXML private VBox myCitationsPane;
    @FXML private Label citTotalCount;
    @FXML private Label citUnpaidCount;
    @FXML private Label citPendingCount;
    @FXML private Label citPaidCount;
    @FXML private Label citTotalOwed;

    @FXML private TableView<Record> myCitationsTable;
    @FXML private TableColumn<Record, Integer> colCitId;
    @FXML private TableColumn<Record, String>  colCitPlate;
    @FXML private TableColumn<Record, String>  colCitCode;
    @FXML private TableColumn<Record, String>  colCitDesc;
    @FXML private TableColumn<Record, Double>  colCitFine;
    @FXML private TableColumn<Record, String>  colCitDate;
    @FXML private TableColumn<Record, String>  colCitStatus;

    // ── My Profile View ───────────────────────────────────────────────────────
    @FXML private VBox myProfilePane;
    @FXML private Label profileAvatarLabel;
    @FXML private Label profileNameLabel;
    @FXML private Label profileLicenseLabel;
    @FXML private Label profileStatusBadge;
    @FXML private Label profileFullName;
    @FXML private Label profileLicenseNo;
    @FXML private Label profileEmail;
    @FXML private Label profilePhone;
    @FXML private Label profileAddress;
    @FXML private Label profileUsername;
    @FXML private Label profileRole;
    @FXML private Label profileTotalOwed;
    @FXML private Label profileTotalPaid;
    @FXML private Label profileTotalPending;
    @FXML private Label profileTotalTickets;

    // ── Services ──────────────────────────────────────────────────────────────
    private final DriverService    driverService    = new DriverService();
    private final VehicleService   vehicleService   = new VehicleService();
    private final ViolationService violationService = new ViolationService();
    private final RecordService    recordService    = new RecordService();

    // ── Cached driver records for citations filter ────────────────────────────
    private List<Record> allDriverRecords = null;

    // ─────────────────────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        User user = Main.getCurrentUser();

        // ── Sidebar user info ─────────────────────────────────────────────────
        if (user != null) {
            String role = user.getRole() != null ? user.getRole().toUpperCase() : "OFFICER";

            if (role.equals("ADMIN")) {
                userNameLabel.setText("Admin: " + user.getFullName());
            } else if (role.equals("USER")) {
                userNameLabel.setText("Driver: " + user.getFullName());
            } else {
                userNameLabel.setText("Officer: " + user.getFullName());
            }
            userRoleLabel.setText("Role: " + role);

            // Role badge colour
            if (roleBadge != null) {
                if (role.equals("ADMIN")) {
                    roleBadge.setText("ADMINISTRATOR");
                    roleBadge.setStyle("-fx-background-color: #e8a87c; -fx-text-fill: #0b1a30; "
                            + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 10; -fx-background-radius: 12;");
                } else if (role.equals("USER")) {
                    roleBadge.setText("DRIVER");
                    roleBadge.setStyle("-fx-background-color: #38a169; -fx-text-fill: white; "
                            + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 10; -fx-background-radius: 12;");
                } else {
                    roleBadge.setText("OFFICER");
                    roleBadge.setStyle("-fx-background-color: #3182ce; -fx-text-fill: white; "
                            + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 10; -fx-background-radius: 12;");
                }
            }

            // ── Role-Based sidebar visibility ─────────────────────────────────
            if ("USER".equals(role)) {
                // Hide admin/officer sections
                hide(btnDrivers);
                hide(btnVehicles);
                hide(btnViolations);
                hide(btnRecords);
                // Show driver-only sections
                show(btnMyVehicles);
                show(btnMyCitations);
                show(btnMyProfile);
            } else if ("ADMIN".equals(role)) {
                // Admin: hide user sections
                hide(btnMyVehicles);
                hide(btnMyCitations);
                hide(btnMyProfile);
            } else {
                // OFFICER: hide admin management + user sections
                hide(btnDrivers);
                hide(btnVehicles);
                hide(btnViolations);
                hide(btnMyVehicles);
                hide(btnMyCitations);
                hide(btnMyProfile);
            }
        }

        setActiveButton(btnHome);

        // ── Column bindings: Recent Violations (Home View) ───────────────────
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDriver.setCellValueFactory(cd -> {
            Driver d = driverService.getDriverById(cd.getValue().getDriverId());
            return new SimpleStringProperty(d != null ? d.getName() : "Unknown");
        });
        colPlate.setCellValueFactory(cd -> {
            Vehicle v = vehicleService.getVehicleById(cd.getValue().getVehicleId());
            return new SimpleStringProperty(v != null ? v.getPlateNumber() : "Unknown");
        });
        colCode.setCellValueFactory(cd -> {
            Violation v = violationService.getViolationById(cd.getValue().getViolationId());
            return new SimpleStringProperty(v != null ? v.getCode() : "Unknown");
        });
        colFine.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().getFineAmount()).asObject());
        colDate.setCellValueFactory(cd -> {
            java.sql.Timestamp date = cd.getValue().getViolationDate();
            String dateStr = date != null ? date.toString() : "";
            return new SimpleStringProperty(dateStr.length() > 16 ? dateStr.substring(0, 16) : dateStr);
        });
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(col -> makeStatusCell());

        // ── Column bindings: Top Offenders ───────────────────────────────────
        colOffenderDriver.setCellValueFactory(new PropertyValueFactory<>("name"));
        colOffenderLicense.setCellValueFactory(new PropertyValueFactory<>("licenseNumber"));
        colOffenderTickets.setCellValueFactory(new PropertyValueFactory<>("violationCount"));
        colOffenderFines.setCellValueFactory(new PropertyValueFactory<>("totalFines"));

        // ── Column bindings: Pending Payments ────────────────────────────────
        if (colPendingId != null) {
            colPendingId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colPendingDriver.setCellValueFactory(cd -> {
                Driver d = driverService.getDriverById(cd.getValue().getDriverId());
                return new SimpleStringProperty(d != null ? d.getName() : "Unknown");
            });
            colPendingPlate.setCellValueFactory(cd -> {
                Vehicle v = vehicleService.getVehicleById(cd.getValue().getVehicleId());
                return new SimpleStringProperty(v != null ? v.getPlateNumber() : "Unknown");
            });
            colPendingFine.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().getFineAmount()).asObject());
            colPendingDate.setCellValueFactory(cd -> {
                java.sql.Timestamp date = cd.getValue().getViolationDate();
                String dateStr = date != null ? date.toString() : "";
                return new SimpleStringProperty(dateStr.length() > 16 ? dateStr.substring(0, 16) : dateStr);
            });
        }

        // ── Column bindings: My Vehicles ─────────────────────────────────────
        if (colVehId != null) {
            colVehId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colVehPlate.setCellValueFactory(new PropertyValueFactory<>("plateNumber"));
            colVehModel.setCellValueFactory(new PropertyValueFactory<>("model"));
            colVehColor.setCellValueFactory(new PropertyValueFactory<>("color"));
            colVehType.setCellValueFactory(new PropertyValueFactory<>("type"));
            colVehTickets.setCellValueFactory(cd ->
                    new SimpleIntegerProperty(recordService.getRecordsByVehicle(cd.getValue().getId()).size()).asObject());

            // Selection listener → show per-vehicle violations
            myVehiclesTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
                if (sel != null) loadVehicleViolations(sel);
            });
        }

        // ── Column bindings: Vehicle Violations sub-table ────────────────────
        if (colVVTicketId != null) {
            colVVTicketId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colVVCode.setCellValueFactory(cd -> {
                Violation v = violationService.getViolationById(cd.getValue().getViolationId());
                return new SimpleStringProperty(v != null ? v.getCode() : "Unknown");
            });
            colVVFine.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().getFineAmount()).asObject());
            colVVDate.setCellValueFactory(cd -> {
                java.sql.Timestamp date = cd.getValue().getViolationDate();
                String dateStr = date != null ? date.toString() : "";
                return new SimpleStringProperty(dateStr.length() > 16 ? dateStr.substring(0, 16) : dateStr);
            });
            colVVStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            colVVStatus.setCellFactory(col -> makeStatusCell());
        }

        // ── Column bindings: My Citations ────────────────────────────────────
        if (colCitId != null) {
            colCitId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colCitPlate.setCellValueFactory(cd -> {
                Vehicle v = vehicleService.getVehicleById(cd.getValue().getVehicleId());
                return new SimpleStringProperty(v != null ? v.getPlateNumber() : "Unknown");
            });
            colCitCode.setCellValueFactory(cd -> {
                Violation v = violationService.getViolationById(cd.getValue().getViolationId());
                return new SimpleStringProperty(v != null ? v.getCode() : "Unknown");
            });
            colCitDesc.setCellValueFactory(cd -> {
                Violation v = violationService.getViolationById(cd.getValue().getViolationId());
                return new SimpleStringProperty(v != null ? v.getDescription() : "—");
            });
            colCitFine.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().getFineAmount()).asObject());
            colCitDate.setCellValueFactory(cd -> {
                java.sql.Timestamp date = cd.getValue().getViolationDate();
                String dateStr = date != null ? date.toString() : "";
                return new SimpleStringProperty(dateStr.length() > 16 ? dateStr.substring(0, 16) : dateStr);
            });
            colCitStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            colCitStatus.setCellFactory(col -> makeStatusCell());
        }

        // ── Load dashboard for role ───────────────────────────────────────────
        boolean isDriver = user != null && "USER".equalsIgnoreCase(user.getRole());
        if (isDriver) {
            loadDriverDashboard(getDriverForUser(user));
        } else {
            loadDashboardStats();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  ADMIN DASHBOARD
    // ─────────────────────────────────────────────────────────────────────────
    private void loadDashboardStats() {
        int driversCount  = driverService.getAllDrivers().size();
        int vehiclesCount = vehicleService.getAllVehicles().size();

        List<Record> allRecords = recordService.getAllRecords();
        int ticketsCount = allRecords.size();

        double unpaidFinesTotal = 0;
        for (Record r : allRecords) {
            if ("UNPAID".equals(r.getStatus()) || "PENDING".equals(r.getStatus())) {
                unpaidFinesTotal += r.getFineAmount();
            }
        }

        statDriversCount.setText(String.valueOf(driversCount));
        statVehiclesCount.setText(String.valueOf(vehiclesCount));
        statTicketsCount.setText(String.valueOf(ticketsCount));
        statFinesUnpaid.setText(String.format("$%.2f", unpaidFinesTotal));

        int size = Math.min(allRecords.size(), 15);
        recentViolationsTable.setItems(FXCollections.observableArrayList(allRecords.subList(0, size)));
        topOffendersTable.setItems(FXCollections.observableArrayList(recordService.getTopOffenders()));

        loadPendingPaymentsPanel();
    }

    private void loadPendingPaymentsPanel() {
        if (pendingPaymentsContainer == null) return;

        List<Record> pending = recordService.getPendingPayments();
        if (pending.isEmpty()) {
            pendingPaymentsContainer.setVisible(false);
            pendingPaymentsContainer.setManaged(false);
            return;
        }
        pendingPaymentsContainer.setVisible(true);
        pendingPaymentsContainer.setManaged(true);
        if (pendingPaymentsBadge != null) {
            pendingPaymentsBadge.setText(pending.size() + " Awaiting");
        }
        if (pendingPaymentsTable != null) {
            pendingPaymentsTable.setItems(FXCollections.observableArrayList(pending));
        }
    }

    // ── Admin: Approve / Reject ───────────────────────────────────────────────
    @FXML
    public void handleApprovePending(ActionEvent event) {
        if (pendingPaymentsTable == null) return;
        Record selected = pendingPaymentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a pending payment to approve."); return; }
        if (recordService.approvePayment(selected.getId())) {
            showAlert(Alert.AlertType.INFORMATION, "Approved", "Payment for Ticket #" + selected.getId() + " APPROVED.");
            loadDashboardStats();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to approve payment.");
        }
    }

    @FXML
    public void handleRejectPending(ActionEvent event) {
        if (pendingPaymentsTable == null) return;
        Record selected = pendingPaymentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a pending payment to reject."); return; }
        if (recordService.rejectPayment(selected.getId())) {
            showAlert(Alert.AlertType.INFORMATION, "Rejected", "Payment for Ticket #" + selected.getId() + " REJECTED.");
            loadDashboardStats();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to reject payment.");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  DRIVER / USER DASHBOARD HOME
    // ─────────────────────────────────────────────────────────────────────────
    private void loadDriverDashboard(Driver driver) {
        if (driver == null) {
            statCard1Title.setText("MY VEHICLES");
            statCard2Title.setText("MY CITATIONS");
            statCard3Title.setText("LICENSE STATUS");
            statCard4Title.setText("MY UNPAID FINES");
            recentViolationsTitle.setText("My Traffic Citations");

            statDriversCount.setText("0");
            statVehiclesCount.setText("0");
            statTicketsCount.setText("ACTIVE");
            statTicketsCount.setStyle("-fx-text-fill: #38a169; -fx-font-size: 24px; -fx-font-weight: bold;");
            statFinesUnpaid.setText("$0.00");

            hide(topOffendersContainer);
            hide(pendingPaymentsContainer);
            hide(notificationContainer);
            userActionsRow.setVisible(true);
            userActionsRow.setManaged(true);

            recentViolationsTable.setItems(FXCollections.observableArrayList());
            recentViolationsTable.setPlaceholder(
                new Label("⚠️ Your account is not linked to a driver profile yet.\nPlease contact an administrator to link your account."));
            return;
        }

        statCard1Title.setText("MY VEHICLES");
        statCard2Title.setText("MY CITATIONS");
        statCard3Title.setText("LICENSE STATUS");
        statCard4Title.setText("MY UNPAID FINES");
        recentViolationsTitle.setText("My Traffic Citations");

        // Hide admin-only widgets
        hide(topOffendersContainer);
        hide(pendingPaymentsContainer);

        userActionsRow.setVisible(true);
        userActionsRow.setManaged(true);

        int vehiclesCount = vehicleService.getVehiclesByOwner(driver.getId()).size();
        allDriverRecords = recordService.getRecordsByDriver(driver.getId());
        int ticketsCount = allDriverRecords.size();

        // License status based on ticket count
        String licenseStatus;
        if (ticketsCount >= 5) {
            licenseStatus = "SUSPENDED";
            statTicketsCount.setStyle("-fx-text-fill: #e53e3e; -fx-font-size: 24px; -fx-font-weight: bold;");
        } else if (ticketsCount >= 3) {
            licenseStatus = "WARNING";
            statTicketsCount.setStyle("-fx-text-fill: #dd6b20; -fx-font-size: 24px; -fx-font-weight: bold;");
        } else {
            licenseStatus = "ACTIVE";
            statTicketsCount.setStyle("-fx-text-fill: #38a169; -fx-font-size: 24px; -fx-font-weight: bold;");
        }

        double unpaidTotal = 0;
        for (Record r : allDriverRecords) {
            if ("UNPAID".equals(r.getStatus()) || "REJECTED".equals(r.getStatus())) {
                unpaidTotal += r.getFineAmount();
            }
        }

        statDriversCount.setText(String.valueOf(vehiclesCount));
        statVehiclesCount.setText(String.valueOf(ticketsCount));
        statTicketsCount.setText(licenseStatus);
        statFinesUnpaid.setText(String.format("$%.2f", unpaidTotal));

        recentViolationsTable.setItems(FXCollections.observableArrayList(allDriverRecords));
        loadNotifications(driver.getId());
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  USER NOTIFICATIONS
    // ─────────────────────────────────────────────────────────────────────────
    private void loadNotifications(int driverId) {
        dao.NotificationDAO notificationDAO = new dao.NotificationDAO();
        List<Notification> notifications = notificationDAO.getNotificationsForDriver(driverId);
        List<Record> unpaidCitations = recordService.getUserNotifications(driverId);

        notificationList.getChildren().clear();
        int unreadCount = 0;

        if (notifications.isEmpty() && unpaidCitations.isEmpty()) {
            notificationContainer.setVisible(false);
            notificationContainer.setManaged(false);
            return;
        }

        notificationContainer.setVisible(true);
        notificationContainer.setManaged(true);

        // Prepend warning notifications for unpaid records
        for (Record r : unpaidCitations) {
            unreadCount++;
            HBox item = new HBox();
            item.setSpacing(10);
            item.setAlignment(Pos.CENTER_LEFT);
            item.setPadding(new Insets(8, 12, 8, 12));
            item.getStyleClass().add("notification-unread");

            Label iconLabel = new Label("⚠");
            iconLabel.setStyle("-fx-font-size: 16px;");

            Label msgLabel = new Label(String.format("Unpaid Fine for Citation #%d: $%.2f", r.getId(), r.getFineAmount()));
            msgLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2d3748; -fx-font-weight: bold;");
            msgLabel.setWrapText(true);
            HBox.setHgrow(msgLabel, Priority.ALWAYS);

            String dateStr = r.getViolationDate() != null ? r.getViolationDate().toString() : "";
            Label timeLabel = new Label(dateStr.length() > 16 ? dateStr.substring(0, 16) : dateStr);
            timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #a0aec0;");

            item.getChildren().addAll(iconLabel, msgLabel, timeLabel);

            Button payBtn = new Button("💳 Pay Now");
            payBtn.getStyleClass().add("notification-pay-btn");
            payBtn.setTooltip(new Tooltip("Select this citation and open the payment form"));
            payBtn.setOnAction(e -> {
                recentViolationsTable.getItems().stream()
                        .filter(rec -> rec.getId() == r.getId())
                        .findFirst()
                        .ifPresent(rec -> {
                            recentViolationsTable.getSelectionModel().select(rec);
                            recentViolationsTable.scrollTo(rec);
                            handlePaySelectedFine(new ActionEvent());
                        });
            });
            item.getChildren().add(payBtn);

            notificationList.getChildren().add(item);
        }

        // Add standard notifications
        for (Notification n : notifications) {
            boolean unread = !n.isRead();
            if (unread) unreadCount++;

            HBox item = new HBox();
            item.setSpacing(10);
            item.setAlignment(Pos.CENTER_LEFT);
            item.setPadding(new Insets(8, 12, 8, 12));
            item.getStyleClass().add(unread ? "notification-unread" : "notification-read");

            String icon = n.getMessage().startsWith("⚠") ? "⚠" :
                          n.getMessage().startsWith("💳") ? "💳" :
                          n.getMessage().startsWith("✅") ? "✅" :
                          n.getMessage().startsWith("❌") ? "❌" : "🔔";
            Label iconLabel = new Label(icon);
            iconLabel.setStyle("-fx-font-size: 16px;");

            Label msgLabel = new Label(n.getMessage());
            msgLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2d3748;"
                    + (unread ? " -fx-font-weight: bold;" : ""));
            msgLabel.setWrapText(true);
            HBox.setHgrow(msgLabel, Priority.ALWAYS);

            Label timeLabel = new Label(n.getCreatedAt().toString().substring(0, 16));
            timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #a0aec0;");

            item.getChildren().addAll(iconLabel, msgLabel, timeLabel);

            if (unread && n.getMessage().contains("ticket #") && n.getMessage().contains("Fine:")) {
                Button payBtn = new Button("💳 Pay Now");
                payBtn.getStyleClass().add("notification-pay-btn");
                payBtn.setTooltip(new Tooltip("Select this citation and open the payment form"));
                final int recordId = n.getRecordId();
                payBtn.setOnAction(e -> {
                    recentViolationsTable.getItems().stream()
                            .filter(rec -> rec.getId() == recordId)
                            .findFirst()
                            .ifPresent(rec -> {
                                recentViolationsTable.getSelectionModel().select(rec);
                                recentViolationsTable.scrollTo(rec);
                                handlePaySelectedFine(new ActionEvent());
                            });
                });
                item.getChildren().add(payBtn);
            }

            notificationList.getChildren().add(item);
        }

        notificationBadgeCount.setText(unreadCount + " New");
        if (unreadCount == 0) {
            notificationBadgeCount.setStyle("-fx-background-color: #cbd5e0; -fx-text-fill: #4a5568; "
                    + "-fx-font-size: 11px; -fx-padding: 2 6; -fx-background-radius: 10; -fx-font-weight: bold;");
        } else {
            notificationBadgeCount.setStyle("-fx-background-color: #e53e3e; -fx-text-fill: white; "
                    + "-fx-font-size: 11px; -fx-padding: 2 6; -fx-background-radius: 10; -fx-font-weight: bold;");
        }
    }

    @FXML
    public void handleMarkAllRead(ActionEvent event) {
        User user = Main.getCurrentUser();
        Driver driver = getDriverForUser(user);
        if (driver != null) {
            new dao.NotificationDAO().markAllAsRead(driver.getId());
            loadNotifications(driver.getId());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  USER: Pay Fine (from Home view)
    // ─────────────────────────────────────────────────────────────────────────
    @FXML
    public void handlePaySelectedFine(ActionEvent event) {
        Record selected = recentViolationsTable.getSelectionModel().getSelectedItem();
        processPayment(selected);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  USER: Pay Fine (from Citations view)
    // ─────────────────────────────────────────────────────────────────────────
    @FXML
    public void handlePayCitationFine(ActionEvent event) {
        Record selected = myCitationsTable.getSelectionModel().getSelectedItem();
        processPayment(selected);
    }

    private void processPayment(Record selected) {
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a citation record to make a payment.");
            return;
        }
        String status = selected.getStatus();
        if ("PAID".equals(status)) {
            showAlert(Alert.AlertType.INFORMATION, "Already Paid", "This citation is already paid in full.");
            return;
        }
        if ("PENDING".equals(status)) {
            showAlert(Alert.AlertType.INFORMATION, "Payment Pending", "Your payment is currently under administrator review.");
            return;
        }

        javafx.scene.control.Dialog<Boolean> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Secure Payment Gateway");
        dialog.setHeaderText("Pay Fine for Citation #" + selected.getId()
                + "\nAmount Due: $" + String.format("%.2f", selected.getFineAmount()));

        javafx.scene.control.ButtonType payBtnType = new javafx.scene.control.ButtonType(
                "Authorize Payment", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(payBtnType, javafx.scene.control.ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        javafx.scene.control.TextField cardholderName = new javafx.scene.control.TextField();
        cardholderName.setPromptText("John Doe");
        if (Main.getCurrentUser() != null) cardholderName.setText(Main.getCurrentUser().getFullName());
        javafx.scene.control.TextField cardNumber = new javafx.scene.control.TextField();
        cardNumber.setPromptText("1234-5678-9012-3456");
        javafx.scene.control.TextField expiry = new javafx.scene.control.TextField();
        expiry.setPromptText("MM/YY");
        javafx.scene.control.PasswordField cvv = new javafx.scene.control.PasswordField();
        cvv.setPromptText("123");

        grid.add(new Label("Cardholder Name:"), 0, 0); grid.add(cardholderName, 1, 0);
        grid.add(new Label("Card Number:"),     0, 1); grid.add(cardNumber,     1, 1);
        grid.add(new Label("Expiry Date:"),     0, 2); grid.add(expiry,         1, 2);
        grid.add(new Label("CVV:"),             0, 3); grid.add(cvv,            1, 3);

        dialog.getDialogPane().setContent(grid);

        javafx.scene.Node payButton = dialog.getDialogPane().lookupButton(payBtnType);
        payButton.setDisable(true);

        Runnable validate = () -> payButton.setDisable(
                cardholderName.getText().trim().isEmpty() || cardNumber.getText().trim().isEmpty()
                || expiry.getText().trim().isEmpty() || cvv.getText().trim().isEmpty());

        cardholderName.textProperty().addListener((o, ov, nv) -> validate.run());
        cardNumber.textProperty().addListener((o, ov, nv) -> validate.run());
        expiry.textProperty().addListener((o, ov, nv) -> validate.run());
        cvv.textProperty().addListener((o, ov, nv) -> validate.run());

        dialog.setResultConverter(btn -> btn == payBtnType ? Boolean.TRUE : null);

        java.util.Optional<Boolean> result = dialog.showAndWait();
        if (result.isPresent() && result.get()) {
            boolean success = recordService.submitPayment(selected.getId());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Payment Submitted",
                        "Your payment of $" + String.format("%.2f", selected.getFineAmount())
                                + " has been submitted. An admin will review and confirm shortly.");
                Driver driver = getDriverForUser(Main.getCurrentUser());
                loadDriverDashboard(driver);
                // Refresh citations view if it's currently shown
                allDriverRecords = null;
            } else {
                showAlert(Alert.AlertType.ERROR, "Payment Failed",
                        "An error occurred while submitting payment. The fine may already be paid or pending review.");
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  MY VEHICLES VIEW
    // ─────────────────────────────────────────────────────────────────────────
    @FXML
    public void showMyVehiclesView(ActionEvent event) {
        headerTitle.setText("My Registered Vehicles");
        setActiveButton(btnMyVehicles);
        swapView(myVehiclesPane);
        loadMyVehicles();
    }

    private void loadMyVehicles() {
        Driver driver = getDriverForUser(Main.getCurrentUser());
        if (driver == null) {
            myVehiclesCountBadge.setText("0 vehicles");
            myVehiclesTable.setItems(FXCollections.observableArrayList());
            myVehiclesTable.setPlaceholder(new Label("⚠️ No driver profile linked to your account."));
            hide(vehicleViolationsContainer);
            return;
        }
        List<Vehicle> vehicles = vehicleService.getVehiclesByOwner(driver.getId());
        myVehiclesCountBadge.setText(vehicles.size() + " vehicle" + (vehicles.size() == 1 ? "" : "s"));
        myVehiclesTable.setItems(FXCollections.observableArrayList(vehicles));
        myVehiclesTable.setPlaceholder(new Label("No registered vehicles found."));
        hide(vehicleViolationsContainer);
    }

    private void loadVehicleViolations(Vehicle vehicle) {
        List<Record> violations = recordService.getRecordsByVehicle(vehicle.getId());
        if (violations.isEmpty()) {
            hide(vehicleViolationsContainer);
            return;
        }
        vehicleViolationsTitle.setText("Violations for: " + vehicle.getPlateNumber() + " (" + vehicle.getModel() + ")");
        vehicleViolationsTable.setItems(FXCollections.observableArrayList(violations));
        vehicleViolationsContainer.setVisible(true);
        vehicleViolationsContainer.setManaged(true);
    }

    @FXML
    public void handleRefreshMyVehicles(ActionEvent event) {
        loadMyVehicles();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  MY CITATIONS VIEW
    // ─────────────────────────────────────────────────────────────────────────
    @FXML
    public void showMyCitationsView(ActionEvent event) {
        headerTitle.setText("My Traffic Citations");
        setActiveButton(btnMyCitations);
        swapView(myCitationsPane);
        loadMyCitations();
    }

    private void loadMyCitations() {
        Driver driver = getDriverForUser(Main.getCurrentUser());
        if (driver == null) {
            resetCitationCounters();
            myCitationsTable.setItems(FXCollections.observableArrayList());
            myCitationsTable.setPlaceholder(new Label("⚠️ No driver profile linked to your account."));
            return;
        }

        allDriverRecords = recordService.getRecordsByDriver(driver.getId());
        updateCitationCounters(allDriverRecords);
        myCitationsTable.setItems(FXCollections.observableArrayList(allDriverRecords));
        myCitationsTable.setPlaceholder(new Label("No citations found. Keep driving safely! 🚦"));
    }

    private void updateCitationCounters(List<Record> records) {
        int total   = records.size();
        int unpaid  = (int) records.stream().filter(r -> "UNPAID".equals(r.getStatus())).count();
        int pending = (int) records.stream().filter(r -> "PENDING".equals(r.getStatus())).count();
        int paid    = (int) records.stream().filter(r -> "PAID".equals(r.getStatus())).count();
        double owed = records.stream()
                .filter(r -> "UNPAID".equals(r.getStatus()) || "REJECTED".equals(r.getStatus()))
                .mapToDouble(Record::getFineAmount).sum();

        citTotalCount.setText(String.valueOf(total));
        citUnpaidCount.setText(String.valueOf(unpaid));
        citPendingCount.setText(String.valueOf(pending));
        citPaidCount.setText(String.valueOf(paid));
        citTotalOwed.setText(String.format("$%.2f", owed));
    }

    private void resetCitationCounters() {
        citTotalCount.setText("0");
        citUnpaidCount.setText("0");
        citPendingCount.setText("0");
        citPaidCount.setText("0");
        citTotalOwed.setText("$0.00");
    }

    // Filter buttons
    @FXML public void handleCitFilterAll(ActionEvent event) {
        if (allDriverRecords != null) myCitationsTable.setItems(FXCollections.observableArrayList(allDriverRecords));
    }
    @FXML public void handleCitFilterUnpaid(ActionEvent event) {
        if (allDriverRecords != null)
            myCitationsTable.setItems(FXCollections.observableArrayList(
                    allDriverRecords.stream().filter(r -> "UNPAID".equals(r.getStatus())).collect(Collectors.toList())));
    }
    @FXML public void handleCitFilterPending(ActionEvent event) {
        if (allDriverRecords != null)
            myCitationsTable.setItems(FXCollections.observableArrayList(
                    allDriverRecords.stream().filter(r -> "PENDING".equals(r.getStatus())).collect(Collectors.toList())));
    }
    @FXML public void handleCitFilterPaid(ActionEvent event) {
        if (allDriverRecords != null)
            myCitationsTable.setItems(FXCollections.observableArrayList(
                    allDriverRecords.stream().filter(r -> "PAID".equals(r.getStatus())).collect(Collectors.toList())));
    }

    @FXML
    public void handleRefreshMyCitations(ActionEvent event) {
        loadMyCitations();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  MY PROFILE VIEW
    // ─────────────────────────────────────────────────────────────────────────
    @FXML
    public void showMyProfileView(ActionEvent event) {
        headerTitle.setText("My Driver Profile");
        setActiveButton(btnMyProfile);
        swapView(myProfilePane);
        loadMyProfile();
    }

    private void loadMyProfile() {
        User user = Main.getCurrentUser();
        Driver driver = getDriverForUser(user);

        // Account info (always available)
        if (user != null) {
            profileUsername.setText(user.getUsername());
            profileRole.setText(user.getRole() != null ? user.getRole() : "USER");
        }

        if (driver == null) {
            profileNameLabel.setText("No driver profile linked");
            profileLicenseLabel.setText("License: —");
            profileAvatarLabel.setText("?");
            profileStatusBadge.setText("UNLINKED");
            profileStatusBadge.setStyle("-fx-background-color: #fed7d7; -fx-text-fill: #742a2a; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 3 12; -fx-background-radius: 12;");
            profileFullName.setText("—"); profileLicenseNo.setText("—");
            profileEmail.setText("—"); profilePhone.setText("—"); profileAddress.setText("—");
            profileTotalOwed.setText("$0.00"); profileTotalPaid.setText("$0.00");
            profileTotalPending.setText("$0.00"); profileTotalTickets.setText("0");
            return;
        }

        // Driver info
        profileNameLabel.setText(driver.getName());
        profileLicenseLabel.setText("License: " + driver.getLicenseNumber());

        // Avatar: first letter of name
        String initials = driver.getName().trim().isEmpty() ? "?" : String.valueOf(driver.getName().trim().charAt(0)).toUpperCase();
        profileAvatarLabel.setText(initials);

        profileFullName.setText(driver.getName());
        profileLicenseNo.setText(driver.getLicenseNumber());
        profileEmail.setText(driver.getEmail() != null && !driver.getEmail().isEmpty() ? driver.getEmail() : "—");
        profilePhone.setText(driver.getPhone() != null && !driver.getPhone().isEmpty() ? driver.getPhone() : "—");
        profileAddress.setText(driver.getAddress() != null && !driver.getAddress().isEmpty() ? driver.getAddress() : "—");

        // Fine summary
        List<Record> records = recordService.getRecordsByDriver(driver.getId());
        int totalTickets = records.size();
        double totalOwed    = records.stream().filter(r -> "UNPAID".equals(r.getStatus()) || "REJECTED".equals(r.getStatus())).mapToDouble(Record::getFineAmount).sum();
        double totalPaid    = records.stream().filter(r -> "PAID".equals(r.getStatus())).mapToDouble(Record::getFineAmount).sum();
        double totalPending = records.stream().filter(r -> "PENDING".equals(r.getStatus())).mapToDouble(Record::getFineAmount).sum();

        profileTotalOwed.setText(String.format("$%.2f", totalOwed));
        profileTotalPaid.setText(String.format("$%.2f", totalPaid));
        profileTotalPending.setText(String.format("$%.2f", totalPending));
        profileTotalTickets.setText(String.valueOf(totalTickets));

        // License status badge
        String statusText; String statusStyle;
        if (totalTickets >= 5) {
            statusText = "SUSPENDED";
            statusStyle = "-fx-background-color: #fed7d7; -fx-text-fill: #742a2a; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 3 12; -fx-background-radius: 12;";
        } else if (totalTickets >= 3) {
            statusText = "WARNING";
            statusStyle = "-fx-background-color: #feebc8; -fx-text-fill: #744210; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 3 12; -fx-background-radius: 12;";
        } else {
            statusText = "ACTIVE";
            statusStyle = "-fx-background-color: #c6f6d5; -fx-text-fill: #22543d; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 3 12; -fx-background-radius: 12;";
        }
        profileStatusBadge.setText(statusText);
        profileStatusBadge.setStyle(statusStyle);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  NAVIGATION
    // ─────────────────────────────────────────────────────────────────────────
    @FXML
    public void showHomeView(ActionEvent event) {
        headerTitle.setText("Dashboard Home");
        setActiveButton(btnHome);
        swapView(homeViewPane);

        User user = Main.getCurrentUser();
        boolean isDriver = user != null && "USER".equalsIgnoreCase(user.getRole());
        if (isDriver) {
            loadDriverDashboard(getDriverForUser(user));
        } else {
            loadDashboardStats();
        }
    }

    @FXML
    public void showDriversView(ActionEvent event) {
        if (!isAdmin()) return;
        headerTitle.setText("Driver Records Management");
        setActiveButton(btnDrivers);
        loadSubView("/view/driver.fxml");
    }

    @FXML
    public void showVehiclesView(ActionEvent event) {
        if (!isAdmin()) return;
        headerTitle.setText("Vehicle Registration Database");
        setActiveButton(btnVehicles);
        loadSubView("/view/vehicle.fxml");
    }

    @FXML
    public void showViolationsView(ActionEvent event) {
        if (!isAdmin()) return;
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

    @FXML
    public void handleLogout(ActionEvent event) {
        util.Session.currentUser = null;
        Main.setCurrentUser(null);
        SceneManager.switchToScene("/view/login.fxml");
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  HELPERS
    // ─────────────────────────────────────────────────────────────────────────
    private void loadSubView(String fxmlPath) {
        Parent view = SceneManager.loadFXML(fxmlPath);
        if (view != null) {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        }
    }

    /** Swap to one of the pre-built inline VBox panes (home / myVehicles / myCitations / myProfile). */
    private void swapView(VBox pane) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(pane);
        pane.setVisible(true);
        pane.setManaged(true);
    }

    private void setActiveButton(Button clicked) {
        for (Button b : new Button[]{btnHome, btnDrivers, btnVehicles, btnViolations,
                btnRecords, btnMyVehicles, btnMyCitations, btnMyProfile}) {
            if (b != null) b.getStyleClass().remove("nav-button-active");
        }
        clicked.getStyleClass().add("nav-button-active");
    }

    private boolean isAdmin() {
        User user = Main.getCurrentUser();
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }

    private Driver getDriverForUser(User user) {
        if (user == null || !"USER".equalsIgnoreCase(user.getRole())) return null;

        // 0. Primary: check driverId on User object directly
        if (user.getDriverId() != null && user.getDriverId() > 0) {
            Driver driver = driverService.getDriverById(user.getDriverId());
            if (driver != null) return driver;
        }

        // 1. Primary: FK driver_id on users row
        Driver driver = driverService.getDriverByUserId(user.getId());
        if (driver != null) return driver;

        // 2. Fallback: username == license_number
        driver = driverService.getDriverByLicense(user.getUsername());
        if (driver != null) return driver;

        // 3. Fallback: full name match
        for (Driver d : driverService.getAllDrivers()) {
            if (d.getName().equalsIgnoreCase(user.getFullName())) return d;
        }
        return null;
    }

    private void hide(Button btn) {
        if (btn == null) return;
        btn.setVisible(false);
        btn.setManaged(false);
    }

    private void hide(VBox box) {
        if (box == null) return;
        box.setVisible(false);
        box.setManaged(false);
    }

    private void show(Button btn) {
        if (btn == null) return;
        btn.setVisible(true);
        btn.setManaged(true);
    }

    private <T> TableCell<T, String> makeStatusCell() {
        return new TableCell<>() {
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
        };
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
