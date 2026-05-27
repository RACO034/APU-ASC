package asc_system;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
}
