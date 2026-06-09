package model;

import java.sql.Timestamp;

public class Record {
    private int id;
    private int driverId;
    private int vehicleId;
    private int violationId;
    private int officerId;
    private double fineAmount;
    private Timestamp violationDate;
    private String status;
    private String notes;

    public Record() {}

    public Record(int id, int driverId, int vehicleId, int violationId, int officerId, double fineAmount, Timestamp violationDate, String status, String notes) {
        this.id = id;
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.violationId = violationId;
        this.officerId = officerId;
        this.fineAmount = fineAmount;
        this.violationDate = violationDate;
        this.status = status;
        this.notes = notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public int getViolationId() { return violationId; }
    public void setViolationId(int violationId) { this.violationId = violationId; }

    public int getOfficerId() { return officerId; }
    public void setOfficerId(int officerId) { this.officerId = officerId; }

    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }

    public Timestamp getViolationDate() { return violationDate; }
    public void setViolationDate(Timestamp violationDate) { this.violationDate = violationDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", driverId=" + driverId +
                ", vehicleId=" + vehicleId +
                ", violationId=" + violationId +
                ", officerId=" + officerId +
                ", fineAmount=" + fineAmount +
                ", violationDate=" + violationDate +
                ", status='" + status + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
