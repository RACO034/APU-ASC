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
    private static final String COUNTER_STAFF_PATH = "data/counterstaff.txt";

    static {
        FileHandler.ensureFileExists(USERS_PATH);
        FileHandler.ensureFileExists(COUNTER_STAFF_PATH);
    }

    public List<String[]> getAllCounterStaff() {
        List<String[]> rows = readRows(COUNTER_STAFF_PATH);
        if (!rows.isEmpty()) {
            return rows;
        }

        // Backward compatibility: migrate existing CS rows from Users.txt to counterstaff.txt.
        List<String[]> migrated = new ArrayList<>();
        for (String[] row : UserDetailsEdit.getAllUsers()) {
            if (row.length >= 8 && row[0].startsWith("CS")) {
                migrated.add(new String[]{
                    row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7], FileHandler.getTimestamp()
                });
            }
        }
        if (!migrated.isEmpty()) {
            writeRowArray(COUNTER_STAFF_PATH, migrated);
        }
        return migrated;
    }

    public String generateCounterStaffId() {
        int max = 0;
        for (String[] row : getAllCounterStaff()) {
            if (row.length > 0 && row[0].matches("CS\\d{3}")) {
                max = Math.max(max, Integer.parseInt(row[0].substring(2)));
            }
        }
        return String.format("CS%03d", max + 1);
    }

    public void saveCounterStaff(String id, String name, String gender, String password,
            String phone, String email, String address, String nationality) {
        List<String[]> all = getAllCounterStaff();
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            String[] row = all.get(i);
            if (row.length > 0 && row[0].equalsIgnoreCase(id)) {
                String dateJoined = row.length > 8 ? row[8] : FileHandler.getTimestamp();
                all.set(i, new String[]{id, name, gender, password, phone, email, address, nationality, dateJoined});
                found = true;
                break;
            }
        }
        if (!found) {
            all.add(new String[]{id, name, gender, password, phone, email, address, nationality, FileHandler.getTimestamp()});
        }
        writeRowArray(COUNTER_STAFF_PATH, all);
        upsertUsersLine(id, buildUsersLine(id, name, gender, password, phone, email, address, nationality));
    }

    public void deleteCounterStaff(String id) {
        List<String[]> all = getAllCounterStaff();
        List<String[]> kept = new ArrayList<>();
        for (String[] row : all) {
            if (row.length > 0 && !row[0].equalsIgnoreCase(id)) {
                kept.add(row);
            }
        }
        writeRowArray(COUNTER_STAFF_PATH, kept);
        removeFromUsers(id);
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

    private void upsertUsersLine(String id, String newLine) {
        List<String> lines = new ArrayList<>();
        boolean found = false;
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] data = line.split("\\|", -1);
                if (data.length > 0 && data[0].equalsIgnoreCase(id)) {
                    lines.add(newLine);
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!found) {
            lines.add(newLine);
        }
        writeLines(USERS_PATH, lines);
    }

    private List<String[]> readRows(String path) {
        List<String[]> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    list.add(line.split("\\|", -1));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void writeRowArray(String path, List<String[]> rows) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (String[] row : rows) {
                bw.write(String.join("|", row));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String buildUsersLine(String id, String name, String gender, String password,
            String phone, String email, String address, String nationality) {
        return id + "|" + name + "|" + gender + "|" + password + "|"
                + phone + "|" + email + "|" + address + "|" + nationality;
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
