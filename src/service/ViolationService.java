package service;

import dao.ViolationDAO;
import model.Violation;

import java.util.List;

public class ViolationService {
    private final ViolationDAO violationDAO;

    public ViolationService() {
        this.violationDAO = new ViolationDAO();
    }

    public boolean addViolationType(String code, String description, double fineAmount) {
        if (violationDAO.getViolationByCode(code) != null) {
            System.out.println("Violation with code " + code + " already exists.");
            return false;
        }
        Violation violation = new Violation(0, code, description, fineAmount);
        return violationDAO.createViolation(violation);
    }

    public Violation getViolationByCode(String code) {
        return violationDAO.getViolationByCode(code);
    }

    public Violation getViolationById(int id) {
        return violationDAO.getViolationById(id);
    }

    public boolean updateViolation(Violation violation) {
        return violationDAO.updateViolation(violation);
    }

    public boolean deleteViolation(int id) {
        return violationDAO.deleteViolation(id);
    }

    public List<Violation> getAllViolations() {
        return violationDAO.getAllViolations();
    }
}
