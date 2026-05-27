package asc_system.Manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * File-backed persistence for managers (pipe-delimited text file).
 *
 * Format: managerID|username|password|email|phone|age|gender|address|nationality|dateJoined
 */
public class ManagerRepository {

    private static final String FILE_PATH = "data/managers.txt";

    static {
        FileHandler.ensureFileExists(FILE_PATH);
    }

    public List<String[]> getAllManagers() {
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

    public Manager findByCredentials(String managerId, String password) {
        if (managerId == null || password == null) {
            return null;
        }
        String id = managerId.trim();
        for (String[] row : getAllManagers()) {
            if (row.length >= 3 && row[0].equalsIgnoreCase(id) && row[2].equals(password)) {
                return rowToManager(row);
            }
        }
        return null;
    }

    public boolean managerIdExists(String managerId) {
        for (String[] row : getAllManagers()) {
            if (row.length > 0 && row[0].equalsIgnoreCase(managerId.trim())) {
                return true;
            }
        }
        return false;
    }

    public String generateManagerID() {
        int max = 0;
        for (String[] row : getAllManagers()) {
            if (row.length > 0 && row[0].matches("M\\d{3}")) {
                int num = Integer.parseInt(row[0].substring(1));
                if (num > max) {
                    max = num;
                }
            }
        }
        return String.format("M%03d", max + 1);
    }

    public void saveManager(Manager manager) {
        List<String[]> all = getAllManagers();
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i)[0].equals(manager.getManagerID())) {
                all.set(i, managerToRow(manager));
                found = true;
                break;
            }
        }
        if (!found) {
            all.add(managerToRow(manager));
        }
        writeAll(all);
        syncUsersFile(manager);
    }

    private void syncUsersFile(Manager manager) {
        final String usersPath = "data/Users.txt";
        FileHandler.ensureFileExists(usersPath);
        List<String> lines = new ArrayList<>();
        boolean found = false;
        try (BufferedReader br = new BufferedReader(new FileReader(usersPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] data = line.split("\\|", -1);
                if (data[0].equals(manager.getManagerID())) {
                    lines.add(buildUsersLine(manager));
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!found) {
            lines.add(buildUsersLine(manager));
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(usersPath))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String buildUsersLine(Manager m) {
        return m.getManagerID() + "|"
                + m.getUsername() + "|"
                + m.getGender() + "|"
                + m.getPassword() + "|"
                + m.getPhoneNumber() + "|"
                + m.getEmail() + "|"
                + m.getHomeAddress() + "|"
                + m.getNationality();
    }

    private void writeAll(List<String[]> rows) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String[] row : rows) {
                bw.write(String.join("|", row));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Manager rowToManager(String[] row) {
        Manager m = new Manager();
        m.setManagerID(row[0]);
        m.setUsername(row[1]);
        m.password = row[2];
        m.setEmail(row[3]);
        m.setPhoneNumber(row[4]);
        try {
            m.setAge(Integer.parseInt(row[5].trim()));
        } catch (Exception ignored) {
            m.age = 18;
        }
        m.setGender(row[6]);
        m.setHomeAddress(row[7]);
        m.setNationality(row[8]);
        if (row.length > 9) {
            m.setDateJoined(row[9]);
        }
        return m;
    }

    private String[] managerToRow(Manager m) {
        return new String[]{
            m.getManagerID(),
            m.getUsername(),
            m.getPassword(),
            m.getEmail(),
            m.getPhoneNumber(),
            String.valueOf(m.getAge()),
            m.getGender(),
            m.getHomeAddress(),
            m.getNationality(),
            m.getDateJoined() != null ? m.getDateJoined() : FileHandler.getTimestamp()
        };
    }
}
