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
    
    public Technician findByCredentials(String technicianId, String password) {
        for (String[] row : getAllTechnicians()) {
            if (row.length >= 3 && row[0].equalsIgnoreCase(technicianId.trim())
                    && row[2].equals(password)) {
                return rowToTechnician(row);
            }
        }
        return null;
    }

    public String generateTechnicianId() {
        int max = 0;
        for (String[] row : getAllTechnicians()) {
            if (row[0].matches("T\\d{3}")) {
                max = Math.max(max, Integer.parseInt(row[0].substring(1)));
            }
        }
        return String.format("T%03d", max + 1);
    }

    public void addTechnician(Technician t, String password, String dateJoined) {
        List<String[]> all = getAllTechnicians();
        all.add(new String[]{
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
            t.getTrade() != null ? t.getTrade().toString() : TechnicianTrade.GENERAL_MAINTENANCE.toString(),
            t.getStatus() != null ? t.getStatus().toString() : TechnicianStatus.AVAILABLE.toString()
        });
        writeAll(all);
    }

    public void saveTechnician(Technician t) {
        saveTechnician(t, null);
    }

    public void saveTechnician(Technician t, String newPassword) {
        List<String[]> all = getAllTechnicians();
        boolean found = false;

        for (int i = 0; i < all.size(); i++) {
            if (all.get(i)[0].trim().equals(t.getTechnicianId())) {
                String[] existing = all.get(i);
                String password = newPassword != null && !newPassword.isEmpty()
                        ? newPassword
                        : (existing.length > 2 ? existing[2] : "");
                String dateJoined = existing.length > 9 ? existing[9] : FileHandler.getTimestamp();

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
                    t.getTrade() != null ? t.getTrade().toString() : "",
                    t.getStatus() != null ? t.getStatus().toString() : ""
                });
                found = true;
                break;
            }
        }

        if (!found) {
            addTechnician(t, newPassword != null ? newPassword : "TempPass1234!", FileHandler.getTimestamp());
            return;
        }

        writeAll(all);
    }

    public void deleteTechnician(String technicianId) {
        List<String[]> all = getAllTechnicians();
        List<String[]> kept = new ArrayList<>();
        for (String[] row : all) {
            if (!row[0].equals(technicianId)) {
                kept.add(row);
            }
        }
        writeAll(kept);
    }

    private Technician rowToTechnician(String[] row) {
        Technician t = new Technician(
                row[0],
                TechnicianTrade.valueOf(row[10]),
                TechnicianStatus.valueOf(row[11]));
        t.setUsername(row[1]);
        t.password = row[2];
        t.setEmail(row[3]);
        t.setPhoneNumber(row[4]);
        try {
            t.setAge(Integer.parseInt(row[5]));
        } catch (Exception ignored) {
        }
        t.setGender(row[6]);
        t.setHomeAddress(row[7]);
        t.setNationality(row[8]);
        return t;
    }

    private void writeAll(List<String[]> all) {
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
