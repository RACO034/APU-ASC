package asc_system.Technician;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author XL
 */
// Data Access Object (DAO)
/**
 * Index    Attribute       
 *  0       appointmentID
 *  1       customerID
 *  2       technicianID
 *  3       serviceType
 *  4       date 
 *  5       time
 *  6       duration
 *  7       status
 * // above only update, no append by technician
 * // below appended by technician 
 *  8       customerComments        
 *  9       technicianFeedback      
 */

public class AppointmentDAO {
    private static final String FILE_PATH  = "data/appointments.txt";
    private static final String DELIM_READ = "\\|";
    private static final String DELIM_WRITE = "|";
    
    // this is similar to AppointmentRepository
    public static List<Appointment> getAll() {
        List<Appointment> list = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return list;
 
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                Appointment a = parseLine(line);
                if (a != null) list.add(a);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
        
    // filter by technicianID
    public static List<Appointment> getByTechnicianID(String technicianID) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment a : getAll()) {
            if (technicianID.equals(a.getTechnicianID())) {
                result.add(a);
            }
        }
        return result;
    }
    
    // find by appointmentID
    public static Appointment getById(String appointmentID) {
        for (Appointment a : getAll()) {
            if (appointmentID.equals(a.getAppointmentID())) return a;
        }
        return null;
    }
    
    // save , only replace index 7,8,9
    public static void save(Appointment updated) {
        File file = new File(FILE_PATH);
        List<String> rawLines = new ArrayList<>();
 
        // 1. Read all raw lines
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) rawLines.add(line);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        
        // 2. Find and patch the matching line
        boolean found = false;
        for (int i = 0; i < rawLines.size(); i++) {
            String line = rawLines.get(i).trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            String[] parts = line.split(DELIM_READ, -1);
            if (parts.length >= 8 && parts[0].trim().equals(updated.getAppointmentID())) {
                String[] newParts = new String[10];
                for (int j = 0; j < 7; j++) {
                    newParts[j] = parts[j];   // [0]-[6]: preserve Ian's data untouched
                }
                // [7] status: enum -> file label string ("Completed", "Assigned", etc.)
                newParts[7] = updated.getStatus() != null
                    ? updated.getStatus().getFileLabel() : "Pending";
                newParts[8] = nvl(updated.getCustomerComments());
                newParts[9] = nvl(updated.getTechnicianFeedback());
                rawLines.set(i, String.join(DELIM_WRITE, newParts));
                found = true;
                break;
            }
        }
 
        // 3. If new record (shouldn't happen from Technician, but safe fallback)
        if (!found) {
            rawLines.add(toLine(updated));
        }
 
        // 4. Write all lines back
        new File("data").mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String line : rawLines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // parse one line into appointment
    private static Appointment parseLine(String line) {
        String[] p = line.split(DELIM_READ, -1);   // -1 keeps trailing empty fields
        if (p.length < 8) return null;
 
        Appointment a = new Appointment();
        a.setAppointmentID(p[0].trim());
        a.setCustomerID(p[1].trim());
        a.setTechnicianID(p[2].trim());
        a.setServiceType(p[3].trim());
        a.setScheduledDateFromString(p[4].trim());  // String -> LocalDate
        a.setScheduledTimeFromString(p[5].trim());  // String -> LocalTime
        try { a.setDuration(Integer.parseInt(p[6].trim())); }
        catch (NumberFormatException ignored) {}
        a.setStatus(AppointmentStatus.fromFileLabel(p[7].trim())); // String -> enum
 
        // Optional Technician-added columns
        if (p.length > 8) a.setCustomerComments(p[8].trim());
        if (p.length > 9) a.setTechnicianFeedback(p[9].trim());
 
        return a;
    }
    
    //serialise a full new appointment (fallback)
    private static String toLine(Appointment a) {
        return String.join(DELIM_WRITE,
            nvl(a.getAppointmentID()),
            nvl(a.getCustomerID()),
            nvl(a.getTechnicianID()),
            nvl(a.getServiceType()),
            a.getScheduledDateStr(),    // LocalDate -> "yyyy-MM-dd"
            a.getScheduledTimeStr(),    // LocalTime -> "HH:mm"
            String.valueOf(a.getDuration()),
            a.getStatus() != null ? a.getStatus().getFileLabel() : "Pending",
            nvl(a.getCustomerComments()),
            nvl(a.getTechnicianFeedback())
        );
    }
    
    private static String nvl(String s){ 
        return s == null ? "" : s; 
    }
    
}
