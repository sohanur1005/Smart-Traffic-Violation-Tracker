package service;

import dao.NotificationDAO;
import dao.RecordDAO;
import dao.ViolationDAO;
import model.Record;
import model.Violation;

import java.util.List;

public class RecordService {
    private final RecordDAO recordDAO;
    private final ViolationDAO violationDAO;
    private final NotificationDAO notificationDAO;

    public RecordService() {
        this.recordDAO    = new RecordDAO();
        this.violationDAO = new ViolationDAO();
        this.notificationDAO = new NotificationDAO();
    }

    // ─── ISSUE VIOLATION ──────────────────────────────────────────────────────
    /**
     * Creates a new violation record with status = 'UNPAID' and sends
     * an automatic notification to the driver.
     */
    public boolean issueViolation(int driverId, int vehicleId, int violationId, int officerId, String notes) {
        Violation violation = violationDAO.getViolationById(violationId);
        if (violation == null) {
            System.err.println("Violation type ID " + violationId + " not found.");
            return false;
        }

        double fine = violation.getFineAmount();

        // Apply 20% surcharge for repeat offenders (5+ prior tickets)
        int pastViolations = recordDAO.getRecordsByDriver(driverId).size();
        if (pastViolations >= 5) {
            fine = fine * 1.20;
        }

        // Status is always 'UNPAID' when first issued
        Record record = new Record(0, driverId, vehicleId, violationId, officerId, fine, null, "UNPAID", notes);
        boolean success = recordDAO.createRecord(record);

        if (success) {
            // Auto-notification for the driver
            notificationDAO.createNotification(
                driverId,
                record.getId(),
                String.format(
                    "⚠ New traffic violation ticket #%d issued. Type: %s. Fine: $%.2f. Please pay immediately.",
                    record.getId(), violation.getCode(), fine
                )
            );
        }
        return success;
    }

    // ─── READ ─────────────────────────────────────────────────────────────────
    public Record getRecordById(int id) {
        return recordDAO.getRecordById(id);
    }

    // ─── PAYMENT WORKFLOW ─────────────────────────────────────────────────────

    /**
     * Admin manually collects/marks a fine as paid (bypass approval flow).
     */
    public boolean payRecord(int id) {
        return recordDAO.updateRecordStatus(id, "PAID");
    }

    /**
     * USER action: submit payment request → status becomes 'PENDING'.
     */
    public boolean submitPayment(int recordId) {
        boolean success = recordDAO.submitPayment(recordId);
        if (success) {
            Record record = recordDAO.getRecordById(recordId);
            if (record != null) {
                // Notify driver that payment is under review
                notificationDAO.createNotification(
                    record.getDriverId(),
                    recordId,
                    String.format(
                        "💳 Payment for ticket #%d submitted successfully. Awaiting admin approval.",
                        recordId
                    )
                );
            }
        }
        return success;
    }

    /**
     * ADMIN action: approve pending payment → status becomes 'PAID'.
     */
    public boolean approvePayment(int recordId) {
        boolean success = recordDAO.approvePayment(recordId);
        if (success) {
            Record record = recordDAO.getRecordById(recordId);
            if (record != null) {
                notificationDAO.createNotification(
                    record.getDriverId(),
                    recordId,
                    String.format(
                        "✅ Your payment for traffic ticket #%d has been APPROVED. Fine cleared.",
                        recordId
                    )
                );
            }
        }
        return success;
    }

    /**
     * ADMIN action: reject pending payment → status becomes 'REJECTED'.
     */
    public boolean rejectPayment(int recordId) {
        boolean success = recordDAO.rejectPayment(recordId);
        if (success) {
            Record record = recordDAO.getRecordById(recordId);
            if (record != null) {
                notificationDAO.createNotification(
                    record.getDriverId(),
                    recordId,
                    String.format(
                        "❌ Your payment for ticket #%d has been REJECTED. Please review and resubmit.",
                        recordId
                    )
                );
            }
        }
        return success;
    }

    // ─── QUERY METHODS ────────────────────────────────────────────────────────

    /**
     * Admin: get all records with status 'PENDING' for review.
     */
    public List<Record> getPendingPayments() {
        return recordDAO.getPendingPayments();
    }

    /**
     * User: get all UNPAID records for a driver (used as fine notifications).
     */
    public List<Record> getUserNotifications(int driverId) {
        return recordDAO.getUserNotifications(driverId);
    }

    public List<Record> getRecordsByDriver(int driverId) {
        return recordDAO.getRecordsByDriver(driverId);
    }

    public List<Record> getRecordsByVehicle(int vehicleId) {
        return recordDAO.getRecordsByVehicle(vehicleId);
    }

    public List<Record> getAllRecords() {
        return recordDAO.getAllRecords();
    }

    public List<model.TopOffender> getTopOffenders() {
        return recordDAO.getTopOffenders();
    }
}
