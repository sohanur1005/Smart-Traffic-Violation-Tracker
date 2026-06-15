package model;

public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private String fullName;
    private Integer driverId;

    public User() {}

    public User(int id, String username, String password, String role, String fullName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
    }

    public User(int id, String username, String password, String role, String fullName, Integer driverId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.driverId = driverId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Integer getDriverId() { return driverId; }
    public void setDriverId(Integer driverId) { this.driverId = driverId; }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', role='" + role + "', fullName='" + fullName + "', driverId=" + driverId + "}";
    }
}
