package model;

public class Vehicle {
    private int id;
    private String plateNumber;
    private int ownerId;
    private String model;
    private String color;
    private String type;

    public Vehicle() {}

    public Vehicle(int id, String plateNumber, int ownerId, String model, String color, String type) {
        this.id = id;
        this.plateNumber = plateNumber;
        this.ownerId = ownerId;
        this.model = model;
        this.color = color;
        this.type = type;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @Override
    public String toString() {
        return "Vehicle{id=" + id + ", plateNumber='" + plateNumber + "', ownerId=" + ownerId + ", model='" + model + "', color='" + color + "', type='" + type + "'}";
    }
}
