package asc_system.Manager;

import asc_system.CounterStaff.UserDetailsEdit;
import asc_system.Technician.TechnicianRepository;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager CRUD for managers, counter staff, and technicians.
 */
public class StaffManagementService {

    private final ManagerRepository managerRepo = new ManagerRepository();
    private final TechnicianRepository techRepo = new TechnicianRepository();
    private static final String USERS_PATH = "data/Users.txt";

    static {
        FileHandler.ensureFileExists(USERS_PATH);
    }

    public List<String[]> getAllCounterStaff() {
        List<String[]> list = new ArrayList<>();
        for (String[] row : UserDetailsEdit.getAllUsers()) {
            if (row[0].startsWith("CS")) {
                list.add(row);
            }
        }
        return list;
    }

    public String generateCounterStaffId() {
        int max = 0;
        for (String[] row : UserDetailsEdit.getAllUsers()) {
            if (row[0].matches("CS\\d{3}")) {
                max = Math.max(max, Integer.parseInt(row[0].substring(2)));
            }
        }
        return String.format("CS%03d", max + 1);
    }

    public void saveCounterStaff(String id, String name, String gender, String password,
            String phone, String email, String address, String nationality) {
        List<String> lines = new ArrayList<>();
        boolean found = false;
        String record = id + "|" + name + "|" + gender + "|" + password + "|"
                + phone + "|" + email + "|" + address + "|" + nationality;
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                if (line.split("\\|", -1)[0].equals(id)) {
                    lines.add(record);
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!found) {
            lines.add(record);
        }
        writeLines(USERS_PATH, lines);
    }

    public void deleteCounterStaff(String id) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty() && !line.split("\\|", -1)[0].equals(id)) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeLines(USERS_PATH, lines);
    }

    public void deleteManager(String id) {
        List<String[]> all = managerRepo.getAllManagers();
        List<String[]> kept = new ArrayList<>();
        for (String[] row : all) {
            if (!row[0].equals(id)) {
                kept.add(row);
            }
        }
        writeManagerRows(kept);
        removeFromUsers(id);
    }

    public void deleteTechnician(String id) {
        techRepo.deleteTechnician(id);
    }

    public ManagerRepository getManagerRepository() {
        return managerRepo;
    }

    public TechnicianRepository getTechnicianRepository() {
        return techRepo;
    }

    private void writeManagerRows(List<String[]> rows) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/managers.txt"))) {
            for (String[] row : rows) {
                bw.write(String.join("|", row));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeFromUsers(String id) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty() && !line.split("\\|", -1)[0].equals(id)) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeLines(USERS_PATH, lines);
    }

    private void writeLines(String path, List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
