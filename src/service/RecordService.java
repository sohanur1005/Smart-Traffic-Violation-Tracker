package service;

import dao.RecordDAO;
import dao.ViolationDAO;
import model.Record;
import model.Violation;

import java.util.List;

public class RecordService {
    private final RecordDAO recordDAO;
    private final ViolationDAO violationDAO;

    public RecordService() {
        this.recordDAO = new RecordDAO();
        this.violationDAO = new ViolationDAO();
    }

    public boolean issueViolation(int driverId, int vehicleId, int violationId, int officerId, String notes) {
        // Retrieve base fine
        Violation violation = violationDAO.getViolationById(violationId);
        if (violation == null) {
            System.err.println("Violation type ID " + violationId + " not found.");
            return false;
        }

        double fine = violation.getFineAmount();
        
        // Count previous violations for repeat offender logic
        int pastViolations = recordDAO.getRecordsByDriver(driverId).size();
        
        // Apply surcharge if violations >= 5
        if (pastViolations >= 5) {
            fine = fine * 1.20; // 20% surcharge
        }

        Record record = new Record(0, driverId, vehicleId, violationId, officerId, fine, null, "UNPAID", notes);
        return recordDAO.createRecord(record);
    }

    public Record getRecordById(int id) {
        return recordDAO.getRecordById(id);
    }

    public boolean payRecord(int id) {
        return recordDAO.updateRecordStatus(id, "PAID");
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
}
