package asc_system.CounterStaff;


import asc_system.CounterStaff.FileHandler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Ian
 */
public class CustomerManagement {

    
    private static final String FILE_PATH = "data/customers.txt";
    private static final String USERS_PATH = "data/Users.txt";

   static {
        FileHandler.ensureFileExists(FILE_PATH);
        FileHandler.ensureFileExists(USERS_PATH);
    }
    
    
    
    public static String generateCustomerID() {
        String lastID = null;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = br.readLine()) != null) {
                lastID = line.split("\\|")[0];
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (lastID == null) return "C00001";

        int num = Integer.parseInt(lastID.substring(1)) + 1;
        return String.format("C%05d", num);
    }
    
    public static void addCustomer(Customer customer) {


     String newRecord = customer.getCustomerID() + "|" +
                       customer.getUsername() + "|" +
                       customer.getGender() + "|" +
                       customer.getAge() + "|" +
                       customer.getPhoneNumber() + "|" +
                       customer.getEmail() + "|" +
                       customer.getHomeAddress() + "|" +
                       customer.getNationality() + "|" +
                       customer.getCarPlate() + "|" +
                       customer.getCarModel() + "|" +
                       customer.getDateAdded();

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
        bw.write(newRecord);
        bw.newLine();
    } catch (IOException e) {
        e.printStackTrace();
    }
    upsertCustomerInUsers(customer, "");
}

    public static void updateCustomer(Customer customer) {

        List<String> updatedLines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {

            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");

                // Match customer ID
                if (data[0].equals(customer.getCustomerID())) {

                String updatedRecord =
                       customer.getCustomerID() + "|" +
                       customer.getUsername() + "|" +
                       customer.getGender() + "|" +
                       customer.getAge() + "|" +
                       customer.getPhoneNumber() + "|" +
                       customer.getEmail() + "|" +
                       customer.getHomeAddress() + "|" +
                       customer.getNationality() + "|" +
                       customer.getCarPlate() + "|" +
                       customer.getCarModel() + "|" +
                       customer.getDateAdded();

                updatedLines.add(updatedRecord);
                found = true;

            } else {
                updatedLines.add(line);
            }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write back to file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {

            for (String l : updatedLines) {
                bw.write(l);
                bw.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!found) {
            System.out.println("Customer ID not found: " + customer.getCustomerID());
        }
        String password = findCustomerPassword(customer.getCustomerID());
        upsertCustomerInUsers(customer, password);
    }
    
    
    public static void deleteCustomer(String customerID) {

    List<String> updatedLines = new ArrayList<>();
    boolean found = false;

    try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {

        String line;

        while ((line = br.readLine()) != null) {
            String[] data = line.split("\\|");

            if (data[0].equals(customerID)) {
                found = true; // skip (delete)
            } else {
                updatedLines.add(line);
            }
        }

    } catch (IOException e) {
        e.printStackTrace();
    }

    // Rewrite file
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {

        for (String l : updatedLines) {
            bw.write(l);
            bw.newLine();
        }

    } catch (IOException e) {
        e.printStackTrace();
    }

    if (!found) {
        System.out.println("Customer ID not found: " + customerID);
    }
    removeCustomerFromUsers(customerID);
}
    
    public static String getCustomerNameByID(String customerID) {

    try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {

        String line;

        while ((line = br.readLine()) != null) {
            String[] data = line.split("\\|");

            if (data.length >= 2 && data[0].equals(customerID)) {
                return data[1]; // ✔ return name
            }
        }

    } catch (IOException e) {
        e.printStackTrace();
    }

    return "Unknown";
}
    
    
    
    public static void syncCustomerToUsers(String customerID, String password) {
        if (customerID == null || customerID.isBlank()) return;
        Customer c = loadCustomerById(customerID);
        if (c != null) {
            upsertCustomerInUsers(c, password == null ? "" : password);
        }
    }
    
    private static Customer loadCustomerById(String customerID) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split("\\|", -1);
                if (data.length >= 11 && data[0].equalsIgnoreCase(customerID)) {
                    Customer c = new Customer();
                    c.setCustomerID(data[0]);
                    c.setUsername(data[1]);
                    c.setGender(data[2]);
                    try {
                        c.setAge(Integer.parseInt(data[3].trim()));
                    } catch (Exception ex) {
                        c.setAge(18);
                    }
                    c.setPhoneNumber(data[4]);
                    c.setEmail(data[5]);
                    c.setHomeAddress(data[6]);
                    c.setNationality(data[7]);
                    c.setCarPlate(data[8]);
                    c.setCarModel(data[9]);
                    c.setDateAdded(data[10]);
                    return c;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static String findCustomerPassword(String customerID) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split("\\|", -1);
                if (data.length >= 12 && data[0].equalsIgnoreCase(customerID)) {
                    return data[11];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    private static void upsertCustomerInUsers(Customer customer, String password) {
        String usersLine = customer.getCustomerID() + "|" + customer.getUsername() + "|"
                + customer.getGender() + "|" + (password == null ? "" : password) + "|"
                + customer.getPhoneNumber() + "|" + customer.getEmail() + "|"
                + customer.getHomeAddress() + "|" + customer.getNationality();

        List<String> updated = new ArrayList<>();
        boolean found = false;
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split("\\|", -1);
                if (data.length > 0 && data[0].equalsIgnoreCase(customer.getCustomerID())) {
                    updated.add(usersLine);
                    found = true;
                } else {
                    updated.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!found) {
            updated.add(usersLine);
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_PATH))) {
            for (String line : updated) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void removeCustomerFromUsers(String customerID) {
        List<String> updated = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split("\\|", -1);
                if (data.length > 0 && !data[0].equalsIgnoreCase(customerID)) {
                    updated.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_PATH))) {
            for (String line : updated) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
