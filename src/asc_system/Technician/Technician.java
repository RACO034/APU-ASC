/**
 *
 * @author XL
 */
package asc_system.Technician;

import asc_system.Technician.User;
import java.util.List;

// inheretance

public class Technician extends User{
    private String technicianID;
    private TechnicianTrade trade;
    private TechnicianStatus status; 
    
    // getters & setters (encapsulation)
 
    public String getTechnicianId() {
        return technicianID;
    }

    public void setTechnicianId(String technicianID) {
        if (technicianID == null || technicianID.isEmpty())
            throw new IllegalArgumentException("Technician ID cannot be empty");
        this.technicianID = technicianID;
    }

    public TechnicianTrade getTrade() {
        return trade;
    }

    public void setTrade(TechnicianTrade trade) {
        if (trade == null)
            throw new IllegalArgumentException("Trade cannot be null");
        this.trade = trade;
    }

    public TechnicianStatus getStatus() {
        return status;
    }

    public void setStatus(TechnicianStatus status) {
        if(status == null){
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = status;
    }
    
    // constructor

    public Technician(String technicianID, TechnicianTrade trade, TechnicianStatus status) {
        this.technicianID = technicianID;
        this.trade = trade;
        this.status = status;
    }
    
    // methods
    public List<Appointment> viewAssignedAppointments() {
        return AppointmentDAO.getByTechnicianID(this.technicianID);
    }
        
    public void updateAppointmentStatus(String appointmentID) {
        Appointment a = AppointmentDAO.getById(appointmentID);
        if (a == null) throw new IllegalArgumentException("Appointment not found: " + appointmentID);
        a.setStatus(AppointmentStatus.COMPLETED);
        AppointmentDAO.save(a);
    }
    
    public void submitFeedback(String appointmentID, String feedback) {
        Appointment a = AppointmentDAO.getById(appointmentID);
        if (a == null) throw new IllegalArgumentException("Appointment not found: " + appointmentID);
        a.setTechnicianFeedback(feedback);
        AppointmentDAO.save(a);
    }
}

