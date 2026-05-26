//exact same as Ian's
package asc_system.Technician;

/**
 *
 * @author Ian
 */
import java.io.*;
import java.util.*;

public class TechnicianRepository {

    
    private static final String FILE_PATH = "data/technicians.txt";

   static {
        FileHandler.ensureFileExists(FILE_PATH);
    }

    public List<String[]> getAllTechnicians() {
        List<String[]> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = br.readLine()) != null) {
                list.add(line.split("\\|"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }
    
    public void saveTechnician(Technician t) {
        List<String[]> all = getAllTechnicians();
        boolean found = false;

        for (int i = 0; i < all.size(); i++) {
            if (all.get(i)[0].trim().equals(t.getTechnicianId())) {
                String[] existing = all.get(i);
                
                // preserve pass and datejoined
                String password = existing.length > 2 ? existing[2] : "";
                String dateJoined = existing.length > 9 ? existing[9] : "";
                
                all.set(i, new String[]{
                    t.getTechnicianId(),
                    t.getUsername(),
                    password,
                    t.getEmail(),
                    t.getPhoneNumber(),
                    String.valueOf(t.getAge()),
                    t.getGender(),
                    t.getHomeAddress(),
                    t.getNationality(),
                    dateJoined,
                    t.getTrade() != null? t.getTrade().toString()  : "",
                    t.getStatus() != null? t.getStatus().toString() : ""
                });
                found = true;
                break;
            }
        }

        if (!found) return; // technician should already exist, don't create new

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String[] row : all) {
                bw.write(String.join("|", row));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
