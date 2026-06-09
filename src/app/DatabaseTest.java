package app;

import service.*;
import model.*;
import model.Record;
import java.util.List;

public class DatabaseTest {
    public static void main(String[] args) {
        System.out.println("Starting Database and Service Verification...");
        try {
            // Attempt to get connection
            java.sql.Connection conn = util.DBConnection.getConnection();
            System.out.println("Database connection established successfully!");
            conn.close();
            
            // Test Seeding/Registration
            AuthService auth = new AuthService();
            DriverService driverService = new DriverService();
            VehicleService vehicleService = new VehicleService();
            ViolationService violationService = new ViolationService();
            RecordService recordService = new RecordService();
            
            System.out.println("Registering test user, driver, vehicle, and violation type...");
            auth.register("test_admin", "adminpwd", "ADMIN", "Test Admin");
            User adminUser = auth.login("test_admin", "adminpwd");
            
            // Use a unique license number to prevent duplicate errors on subsequent runs
            String uniqueLicense = "DL_TEST_" + System.currentTimeMillis();
            driverService.registerDriver(uniqueLicense, "Testy McTest", "testy@test.com", "555-9999", "999 Test St");
            
            Driver testDriver = driverService.getDriverByLicense(uniqueLicense);
            if (testDriver != null) {
                System.out.println("Driver found: " + testDriver.getName() + " (ID: " + testDriver.getId() + ")");
                vehicleService.registerVehicle("TST-" + (System.currentTimeMillis() % 10000), testDriver.getId(), "Toyota Corolla", "Silver", "Sedan");
            }
            
            String uniqueCode = "SPEED_" + (System.currentTimeMillis() % 10000);
            violationService.addViolationType(uniqueCode, "Speed Limit 100 fine", 100.00);
            
            Violation viol = violationService.getViolationByCode(uniqueCode);
            List<Vehicle> driverVehicles = vehicleService.getVehiclesByOwner(testDriver.getId());
            Vehicle veh = driverVehicles.isEmpty() ? null : driverVehicles.get(0);
            
            if (testDriver != null && veh != null && viol != null && adminUser != null) {
                System.out.println("Issuing 4 tickets to verify standard pricing ($100)...");
                for (int i = 1; i <= 4; i++) {
                    recordService.issueViolation(testDriver.getId(), veh.getId(), viol.getId(), adminUser.getId(), "Standard ticket " + i);
                }
                
                // Get driver tickets so far
                List<Record> history1 = recordService.getRecordsByDriver(testDriver.getId());
                System.out.println("Past tickets count: " + history1.size());
                for (Record r : history1) {
                    System.out.println(" - Ticket ID: " + r.getId() + " | Fine: $" + r.getFineAmount() + " | Status: " + r.getStatus());
                }
                
                System.out.println("Issuing 5th ticket to verify 20% surcharge ($120.00 fine)...");
                recordService.issueViolation(testDriver.getId(), veh.getId(), viol.getId(), adminUser.getId(), "Surcharged ticket 5");
                
                List<Record> history2 = recordService.getRecordsByDriver(testDriver.getId());
                Record fifthTicket = null;
                for (Record r : history2) {
                    if (r.getNotes().equals("Surcharged ticket 5")) {
                        fifthTicket = r;
                        break;
                    }
                }
                
                if (fifthTicket != null) {
                    System.out.println("5th Ticket Levy Fine: $" + fifthTicket.getFineAmount());
                    if (fifthTicket.getFineAmount() == 120.00) {
                        System.out.println("SUCCESS: Surcharge applied correctly!");
                    } else {
                        System.err.println("FAILURE: Fine was expected to be $120.00 but got $" + fifthTicket.getFineAmount());
                    }
                } else {
                    System.err.println("FAILURE: 5th ticket not found in database history.");
                }
            }
            
            System.out.println("Verification test completed successfully!");
        } catch (Exception e) {
            System.err.println("Verification test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
