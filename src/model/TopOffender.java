package model;

public class TopOffender {
    private String name;
    private String licenseNumber;
    private int violationCount;
    private double totalFines;

    public TopOffender() {}

    public TopOffender(String name, String licenseNumber, int violationCount, double totalFines) {
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.violationCount = violationCount;
        this.totalFines = totalFines;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public int getViolationCount() { return violationCount; }
    public void setViolationCount(int violationCount) { this.violationCount = violationCount; }

    public double getTotalFines() { return totalFines; }
    public void setTotalFines(double totalFines) { this.totalFines = totalFines; }

    @Override
    public String toString() {
        return "TopOffender{name='" + name + "', licenseNumber='" + licenseNumber + "', violationCount=" + violationCount + ", totalFines=" + totalFines + "}";
    }
}
