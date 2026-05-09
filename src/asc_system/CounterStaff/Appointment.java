
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Ian
 */
public class Appointment {
        private static final String FILE_PATH = "data/appointments.txt";

           static {
                FileHandler.ensureFileExists(FILE_PATH);
            }
    private String appointmentID;
    private String customerID;
    private String technicianID;
    private String serviceType;
    private LocalDateTime dateTime;
    private int duration;
    private String status;
    
    public void validateAndSave(LocalDateTime dateTime, String customerID, String serviceType) {
   
    setDateTime(dateTime, serviceType);
    
    saveAppointment(customerID, serviceType);
    }

    public void setDateTime(LocalDateTime dateTime, String serviceType) {

    LocalDateTime now = LocalDateTime.now();

    
    if (dateTime.isBefore(now)) {
        throw new IllegalArgumentException("Appointment cannot be in the past.");
    }

   
    DayOfWeek day = dateTime.getDayOfWeek();
    if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
        throw new IllegalArgumentException("Only Monday to Friday allowed.");
    }

    
    int hour = dateTime.getHour();
    if (hour < 9 || hour >= 17) {
        throw new IllegalArgumentException("Time must be between 09:00 and 17:00.");
    }

   //Duration check
    int duration = serviceType.equals("Major Service") ? 3 : 1;

    if (hour + duration > 17) {
        throw new IllegalArgumentException("Service exceeds working hours.");
    }

    //(today → end of next week)
    LocalDate today = LocalDate.now();
    LocalDate endNextWeek = today.plusWeeks(1).with(DayOfWeek.SUNDAY);

    if (dateTime.toLocalDate().isAfter(endNextWeek)) {
        throw new IllegalArgumentException("Appointment must be within next week.");
    }

    this.dateTime = dateTime;
}
    
    public void saveAppointment(String customerID, String serviceType) {

    this.appointmentID = generateAppointmentID();
    this.customerID = customerID;
    this.technicianID = "NULL"; // not assigned yet
    this.serviceType = serviceType;

    // Duration logic
    this.duration = serviceType.equals("Major Service") ? 3 : 1;

    // Default status
    this.status = "Pending";

    // Extract date & time
    String date = dateTime.toLocalDate().toString();     // yyyy-MM-dd
    String time = dateTime.toLocalTime().toString();     // HH:mm

    // Build record
    String record = appointmentID + "|" + customerID + "|" + technicianID + "|" +
                    serviceType + "|" + date + "|" + time + "|" +
                    duration + "|" + status;

    // Write to file
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
        bw.write(record);
        bw.newLine();
    } catch (IOException e) {
        e.printStackTrace();
    }
}


    public static String generateAppointmentID() {
        String lastID = null;

    try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
        String line;

        while ((line = br.readLine()) != null) {
            lastID = line.split("\\|")[0];
        }

    } catch (IOException e) {
        e.printStackTrace();
    }

    if (lastID == null) return "APP001";

    int num = Integer.parseInt(lastID.substring(3)) + 1; // skips "APP"
    return String.format("APP%03d", num);
    

}
    
    
    
}