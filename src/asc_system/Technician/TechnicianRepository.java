package asc_system.Technician;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TechnicianRepository {

    private static final String FILE_PATH = "data/technicians.txt";
    private static final String USERS_PATH = "data/Users.txt";

    static {
        FileHandler.ensureFileExists(FILE_PATH);
        FileHandler.ensureFileExists(USERS_PATH);
    }

    public List<String[]> getAllTechnicians() {
        List<String[]> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
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

    public String generateTechnicianId() {
        int max = 0;
        for (String[] row : getAllTechnicians()) {
            if (row.length > 0 && row[0].matches("T\\d{3}")) {
                max = Math.max(max, Integer.parseInt(row[0].substring(1)));
            }
        }
        return String.format("T%03d", max + 1);
    }

    public Technician findByCredentials(String technicianId, String password) {
        if (technicianId == null || password == null) {
            return null;
        }
        for (String[] row : getAllTechnicians()) {
            if (row.length >= 12
                    && row[0].equalsIgnoreCase(technicianId.trim())
                    && row[2].equals(password)) {
                return rowToTechnician(row);
            }
        }
        return null;
    }

    public void addTechnician(Technician t, String password, String dateJoined) {
        String joined = (dateJoined == null || dateJoined.isBlank()) ? FileHandler.getTimestamp() : dateJoined;
        String[] row = new String[]{
            t.getTechnicianId(),
            t.getUsername(),
            password == null ? "" : password,
            t.getEmail(),
            t.getPhoneNumber(),
            String.valueOf(t.getAge()),
            t.getGender(),
            t.getHomeAddress(),
            t.getNationality(),
            joined,
            t.getTrade() != null ? t.getTrade().toString() : "",
            t.getStatus() != null ? t.getStatus().toString() : ""
        };
        List<String[]> all = getAllTechnicians();
        all.add(row);
        writeAllTechnicians(all);
        syncUsersLine(row);
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

                String password = (newPassword != null && !newPassword.isBlank())
                        ? newPassword
                        : (existing.length > 2 ? existing[2] : "");
                String dateJoined = existing.length > 9 ? existing[9] : FileHandler.getTimestamp();

                String[] updated = new String[]{
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
                };
                all.set(i, updated);
                syncUsersLine(updated);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Technician not found: " + t.getTechnicianId());
        }

        writeAllTechnicians(all);
    }

    public void deleteTechnician(String technicianId) {
        if (technicianId == null) {
            return;
        }
        List<String[]> all = getAllTechnicians();
        List<String[]> kept = new ArrayList<>();
        for (String[] row : all) {
            if (row.length > 0 && !row[0].equalsIgnoreCase(technicianId.trim())) {
                kept.add(row);
            }
        }
        writeAllTechnicians(kept);
        removeFromUsers(technicianId.trim());
    }

    private Technician rowToTechnician(String[] row) {
        TechnicianTrade trade = TechnicianTrade.valueOf(row[10]);
        TechnicianStatus status = TechnicianStatus.valueOf(row[11]);
        Technician t = new Technician(row[0], trade, status);
        t.setUsername(row[1]);
        t.password = row[2];
        t.setEmail(row[3]);
        t.setPhoneNumber(row[4]);
        try {
            t.setAge(Integer.parseInt(row[5].trim()));
        } catch (Exception ex) {
            t.setAge(18);
        }
        t.setGender(row[6]);
        t.setHomeAddress(row[7]);
        t.setNationality(row[8]);
        t.dateJoined = row[9];
        return t;
    }

    private void writeAllTechnicians(List<String[]> rows) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String[] row : rows) {
                bw.write(String.join("|", row));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void syncUsersLine(String[] techRow) {
        if (techRow.length < 9) {
            return;
        }
        String id = techRow[0];
        String usersLine = techRow[0] + "|" + techRow[1] + "|" + techRow[6] + "|" + techRow[2] + "|"
                + techRow[4] + "|" + techRow[3] + "|" + techRow[7] + "|" + techRow[8];

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
                    lines.add(usersLine);
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!found) {
            lines.add(usersLine);
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_PATH))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeFromUsers(String technicianId) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] data = line.split("\\|", -1);
                if (data.length > 0 && !data[0].equalsIgnoreCase(technicianId)) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_PATH))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
