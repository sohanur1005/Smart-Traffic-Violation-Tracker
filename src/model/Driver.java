package model;

public class Driver {
    private int id;
    private String licenseNumber;
    private String name;
    private String email;
    private String phone;
    private String address;

    public Driver() {}

    public Driver(int id, String licenseNumber, String name, String email, String phone, String address) {
        this.id = id;
        this.licenseNumber = licenseNumber;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return "Driver{id=" + id + ", licenseNumber='" + licenseNumber + "', name='" + name + "', email='" + email + "', phone='" + phone + "', address='" + address + "'}";
    }
}
