package asc_system.Technician;

/**
 *
 * @author XL
 */
public enum AppointmentStatus {
    PENDING   ("Pending"),
    ASSIGNED  ("Assigned"),
    COMPLETED ("Completed");
 
    //string stored on txt
    private final String fileLabel;
    
    //constructor
    AppointmentStatus(String fileLabel) {
        this.fileLabel = fileLabel;
    }
 
    //getter
    public String getFileLabel() {
        return fileLabel;
    }
 
    // get raw string from txt -> enum
    // case insensitive
    
    public static AppointmentStatus fromFileLabel(String label) {
        if (label == null) return PENDING;
        for (AppointmentStatus s : values()) {
            if (s.fileLabel.equalsIgnoreCase(label.trim())) return s;
        }
        return PENDING;   // safe default
    }
 
    // used by JTable cell renderer to colour the status chip. 
    @Override
    public String toString() {
        return fileLabel;
    }
}
