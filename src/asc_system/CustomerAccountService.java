package asc_system;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads customer profile rows from customers.txt into asc_system.Customer.
 */
public final class CustomerAccountService {

    private static final String FILE_PATH = "data/customers.txt";

    static {
        FileHandler.ensureFileExists(FILE_PATH);
    }

    private CustomerAccountService() {
    }

    public static Customer loadCustomer(String customerId) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] d = line.split("\\|", -1);
                if (d.length >= 11 && d[0].equalsIgnoreCase(customerId.trim())) {
                    Customer c = new Customer();
                    c.setCustomerID(d[0]);
                    c.setUsername(d[1]);
                    c.setGender(d[2]);
                    try {
                        c.setAge(Integer.parseInt(d[3].trim()));
                    } catch (Exception ignored) {
                        c.setAge(18);
                    }
                    c.setPhoneNumber(d[4]);
                    c.setEmail(d[5]);
                    c.setHomeAddress(d[6]);
                    c.setNationality(d[7]);
                    c.setCarPlate(d[8]);
                    c.setCarModel(d[9]);
                    c.setDateAdded(d[10]);
                    return c;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Verifies customer password stored in customers.txt column 12 (index 11).
     * Backwards compatible: if the column doesn't exist yet, we migrate from legacy file.
     */
    public static boolean verifyPassword(String customerId, String password) {
        if (customerId == null || password == null) {
            return false;
        }
        String id = customerId.trim();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] d = line.split("\\|", -1);
                if (d.length >= 12 && d[0].equalsIgnoreCase(id)) {
                    return d[11].equals(password);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Returns password from customers.txt, or null if missing. */
    public static String getPasswordUnsafe(String customerId) {
        if (customerId == null) return null;
        String id = customerId.trim();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] d = line.split("\\|", -1);
                if (d.length >= 12 && d[0].equalsIgnoreCase(id)) {
                    return d[11];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates/sets the password inside customers.txt (column 12).
     * If a row is still 11 columns, it will be expanded to 12.
     */
    public static void setPassword(String customerId, String newPassword) {
        if (customerId == null) return;

        List<String> lines = new ArrayList<>();
        boolean updated = false;
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] d = line.split("\\|", -1);
                if (d.length >= 11 && d[0].equalsIgnoreCase(customerId.trim())) {
                    String[] out = ensureLength(d, 12);
                    out[11] = newPassword == null ? "" : newPassword;
                    lines.add(String.join("|", out));
                    updated = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (updated) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
                for (String l : lines) {
                    bw.write(l);
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            asc_system.CounterStaff.CustomerManagement.syncCustomerToUsers(customerId.trim(),
                    newPassword == null ? "" : newPassword);
        }
    }

    public static void saveCustomer(Customer c) {
        asc_system.CounterStaff.Customer cs = new asc_system.CounterStaff.Customer();
        cs.setCustomerID(c.getCustomerID());
        cs.setUsername(c.getUsername());
        cs.setGender(c.getGender() != null ? c.getGender() : "Male");
        cs.setAge(c.getAge() > 0 ? c.getAge() : 18);
        cs.setPhoneNumber(c.getPhoneNumber());
        cs.setEmail(c.getEmail());
        cs.setHomeAddress(c.getHomeAddress());
        cs.setNationality(c.getNationality());
        cs.setCarPlate(c.getCarPlate());
        cs.setCarModel(c.getCarModel());
        cs.setDateAdded(c.getDateAdded());
        asc_system.CounterStaff.CustomerManagement.updateCustomer(cs);
    }

    private static String[] ensureLength(String[] in, int len) {
        if (in.length >= len) return in;
        String[] out = new String[len];
        System.arraycopy(in, 0, out, 0, in.length);
        for (int i = in.length; i < len; i++) out[i] = "";
        return out;
    }
}
