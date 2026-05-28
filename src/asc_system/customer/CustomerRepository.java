package asc_system.customer;

import asc_system.CustomerAccountService;
import asc_system.FileConstants;
import asc_system.FileHandler;
import asc_system.models.Customer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * File-backed repository for the updated customer UI, adapted to this project’s existing files.
 *
 * customers.txt format (existing counter staff format, 11 fields):
 * customerID|username|gender|age|phone|email|address|nationality|carPlate|carModel|dateAdded
 *
 * Password is stored in customers.txt column 12 (index 11).
 */
public class CustomerRepository {
    private static final String CUSTOMERS_FILE = FileConstants.CUSTOMERS;

    public CustomerRepository() {
        FileHandler.ensureFileExists(CUSTOMERS_FILE);
    }

    private List<String> readCustomerLines() {
        FileHandler.ensureFileExists(CUSTOMERS_FILE);
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CUSTOMERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private void writeAllCustomerLines(List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CUSTOMERS_FILE))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Customer> getAllCustomers() {
        List<Customer> out = new ArrayList<>();
        for (String line : readCustomerLines()) {
            String[] p = line.split("\\|", -1);
            if (p.length < 11) {
                continue;
            }
            Customer c = new Customer();
            c.setId(p[0]);
            c.setName(p[1]);
            c.setPhone(p[4]);
            c.setEmail(p[5]);
            c.setTown(p[6]);                // store "address" into "town" field for this UI
            c.setRegistrationDate(p[10]);
            c.setPassword(CustomerAccountService.getPasswordUnsafe(p[0]));
            c.setRole("CUSTOMER");
            out.add(c);
        }
        return out;
    }

    public Customer findByCustomerID(String id) {
        if (id == null) return null;
        for (Customer c : getAllCustomers()) {
            if (id.equalsIgnoreCase(c.getId())) {
                return c;
            }
        }
        return null;
    }

    public Customer findByEmail(String email) {
        if (email == null) return null;
        for (Customer c : getAllCustomers()) {
            if (email.equalsIgnoreCase(c.getEmail())) {
                return c;
            }
        }
        return null;
    }

    /**
     * Updates the profile information in customers.txt.
     * If the model password is set, it also updates the password column in customers.txt.
     */
    public void updateCustomer(Customer c) {
        if (c == null || c.getId() == null) {
            return;
        }

        List<String> lines = readCustomerLines();
        boolean changed = false;
        for (int i = 0; i < lines.size(); i++) {
            String[] p = lines.get(i).split("\\|", -1);
            if (p.length < 11) {
                continue;
            }
            if (!c.getId().equalsIgnoreCase(p[0])) {
                continue;
            }

            // Keep all other fields unchanged to avoid affecting other modules.
            p[1] = safe(c.getName());
            p[4] = safe(c.getPhone());
            p[5] = safe(c.getEmail());
            p[6] = safe(c.getTown()); // write UI "town" back into address column

            lines.set(i, String.join("|", p));
            changed = true;
            break;
        }

        if (changed) {
            writeAllCustomerLines(lines);
        }

        if (c.getPassword() != null) {
            CustomerAccountService.setPassword(c.getId(), c.getPassword());
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}

