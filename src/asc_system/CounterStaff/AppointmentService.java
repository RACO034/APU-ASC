package asc_system.CounterStaff;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Ian
 */
import java.util.*;

public class AppointmentService {

    private CS_AppointmentRepository repo = new CS_AppointmentRepository();

    public boolean isTechnicianAvailable(String techID, String date, String time, String duration) {

        int newStart = Integer.parseInt(time.replace(":", ""));
        int dur = Integer.parseInt(duration);
        int newEnd = newStart + (dur * 100);

        for (String[] data : repo.getAllAppointments()) {

            if (data[2].equals(techID) && data[4].equals(date)) {

                int existingStart = Integer.parseInt(data[5].replace(":", ""));
                int existingEnd = existingStart + (Integer.parseInt(data[6]) * 100);

                if (newStart < existingEnd && newEnd > existingStart) {
                    return false;
                }
            }
        }

        return true;
    }

    
    public String getAvailabilityStatus(String techID) {

        for (String[] data : repo.getAllAppointments()) {
            if (data[2].equals(techID) && data[7].equalsIgnoreCase("Assigned")) {
                return "Busy";
            }
        }

        return "Available";
    }
    
    public String getTechnicianStatus(String techID) {

        for (String[] data : repo.getAllAppointments()) {
            if (data[2].equals(techID) && data[7].equalsIgnoreCase("Assigned")) {
                return "Working";
            }
        }

        return "Idle";
    }
    
    public int calculateWorkload(String techID) {
    int totalHours = 0;

        for (String[] data : repo.getAllAppointments()) {
            // data[2] is techID, data[6] is duration
            if (data[2].equals(techID)) {
                totalHours += Integer.parseInt(data[6]);
            }
        }
        // 2. Return only after all checks pass
        return totalHours;
    }
    
    
    public int calculateWorkload(String techID, int newHours) {
    int currentTotal = 0;
    for (String[] data : repo.getAllAppointments()) {
        if (data[2].equals(techID)) {
            currentTotal += Integer.parseInt(data[6]);
        }
    }

    if (currentTotal + newHours > 8) {
        // This is what the 'catch' block in your UI is looking for
        throw new IllegalArgumentException("Adding " + newHours + "Hrs would exceed the 8-hour limit (Current: " + currentTotal + "Hrs).");
    }
    return currentTotal + newHours;
}
    

    public void assignTechnician(String appointmentID, String technicianID) {

        List<String[]> list = repo.getAllAppointments();
        String durationStr = "0";

    
        for (String[] data : list) {
            if (data[0].equals(appointmentID)) {
                durationStr = data[6]; // Get actual duration from index 6
                break;
            }
    }

        // 2. Validate using your overloaded method
        int duration = Integer.parseInt(durationStr);
        calculateWorkload(technicianID, duration);

    // If calculateWorkload didn't throw an exception, proceed to save
    for (String[] data : list) {
        if (data[0].equals(appointmentID)) {
            data[2] = technicianID;
            data[7] = "Assigned";
        }
    }

        repo.saveAll(list);
    }
}
