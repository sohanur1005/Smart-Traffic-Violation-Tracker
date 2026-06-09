package model;

public class Violation {
    private int id;
    private String code;
    private String description;
    private double fineAmount;

    public Violation() {}

    public Violation(int id, String code, String description, double fineAmount) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.fineAmount = fineAmount;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }

    @Override
    public String toString() {
        return "Violation{id=" + id + ", code='" + code + "', description='" + description + "', fineAmount=" + fineAmount + "}";
    }
}
