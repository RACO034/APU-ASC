package asc_system.CounterStaff;


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

   static {
        FileHandler.ensureFileExists(FILE_PATH);
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
    
    
    
}
