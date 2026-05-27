package asc_system;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Customer login passwords (separate file so counter-staff customer records stay unchanged).
 * Format: customerID|password
 */
public final class CustomerCredentials {

    private static final String FILE_PATH = "data/customer_credentials.txt";

    static {
        FileHandler.ensureFileExists(FILE_PATH);
    }

    private CustomerCredentials() {
    }

    public static boolean verify(String customerId, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|", -1);
                if (p.length >= 2 && p[0].equalsIgnoreCase(customerId.trim()) && p[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void save(String customerId, String password) {
        List<String[]> rows = new ArrayList<>();
        boolean found = false;
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] p = line.split("\\|", -1);
                if (p[0].equalsIgnoreCase(customerId)) {
                    rows.add(new String[]{customerId, password});
                    found = true;
                } else {
                    rows.add(p);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!found) {
            rows.add(new String[]{customerId, password});
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String[] r : rows) {
                bw.write(r[0] + "|" + r[1]);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean exists(String customerId) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|", -1);
                if (p.length >= 1 && p[0].equalsIgnoreCase(customerId.trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
