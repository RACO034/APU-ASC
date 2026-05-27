package asc_system.Technician;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
/**
 *
 * @author XL
 */

/**this is made to streamline technician workflow
 * txt files stores date and time as string
 * class holds them as LocalDate/ LocalTime -> code can use date / time object
 * AppointmentDAO handles String -> LocalDate/LocalTime conversion 
*/

public class Appointment {
    
    private static final String FILE_PATH = "data/appointments.txt";

    static {
         FileHandler.ensureFileExists(FILE_PATH);
     }
    // align with Ian 0-7, used enum for status tho
    private String appointmentID;
    private String customerID;
    private String technicianID;
    private String serviceType;
    private LocalDate scheduledDate; // yyyy-MM-dd
    private LocalTime scheduledTime; // HH:mm
    private int duration;
    private AppointmentStatus status;
    
    // technician fields 8,9
    private String customerComments;
    private String technicianFeedback;
    
    // formatters
    public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    
    // constructor

    public Appointment(){}
    
    // getter

    public String getAppointmentID() {
        return appointmentID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getTechnicianID() {
        return technicianID;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public LocalTime getScheduledTime() {
        return scheduledTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public int getDuration() {
        return duration;
    }

    public String getCustomerComments() {
        return customerComments;
    }

    public String getTechnicianFeedback() {
        return technicianFeedback;
    }
    
    // format date to write to file : yyyy-MM-dd
    public String getScheduledDateStr() {
        return scheduledDate != null ? scheduledDate.format(DATE_FMT) : "";
    }
    
    // format time to write to file : HH:mm
    public String getScheduledTimeStr() {
        return scheduledTime != null ? scheduledTime.format(TIME_FMT) : "";
    }
    
    // setters

    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void setTechnicianID(String technicianID) {
        this.technicianID = technicianID;
    }
    
    // accept LocalDate directly
    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
    
    // file string (yyyy-MM-dd) -> LocalDate
    // called by AppointmentDOA.parseLine()
    public void setScheduledDateFromString(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) { this.scheduledDate = null; return; }
        try {
            this.scheduledDate = LocalDate.parse(dateStr.trim(), DATE_FMT);
        } catch (DateTimeParseException e) {
            this.scheduledDate = null;
        }
    }
    
    // accept LocalTime directly
    public void setScheduledTime(LocalTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
    
    // file string (HH:mm) -> LocalTime
    // called by AppointmentDOA.parseLine()
    public void setScheduledTimeFromString(String timeStr) {
        if (timeStr == null || timeStr.isBlank()) { this.scheduledTime = null; return; }
        try {
            this.scheduledTime = LocalTime.parse(timeStr.trim(), TIME_FMT);
        } catch (DateTimeParseException e) {
            this.scheduledTime = null;
        }
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setCustomerComments(String customerComments) {
        this.customerComments = customerComments;
    }

    public void setTechnicianFeedback(String technicianFeedback) {
        this.technicianFeedback = technicianFeedback;
    }
    
    
}