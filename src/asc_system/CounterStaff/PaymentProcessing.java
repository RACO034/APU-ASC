package asc_system.CounterStaff;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Ian
 */
import asc_system.CounterStaff.FileHandler;
import asc_system.PublicClasses.ServiceType;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class PaymentProcessing {
    
    private static final String FILE_Pay = "data/Payments.txt";
    private static final String FILE_App = "data/Appointments.txt";
    private static final String FILE_Price = "data/prices.txt";

   static {
        FileHandler.ensureFileExists(FILE_Pay);
        FileHandler.ensureFileExists(FILE_App);
    }

    
   public static String processPayment(String appointmentID, String paymentMethod, boolean saveToFile) {
    // 1. Check if the appointment even exists
    String[] appointmentData = getAppointmentDetails(appointmentID);
    if (appointmentData == null) {
        return "ERROR: Appointment not found.";
    }

    // 2. Check if the appointment is completed
    String status = appointmentData[7]; 
    if (!status.equals("Completed")) {
        return "ERROR: Appointment not Completed.";
    }

    // 3. DUPLICATE CHECK: Search for existing payment record
    String[] existingPayment = getExistingPaymentDetails(appointmentID);

    String paymentID;
    String date;
    String customerName = appointmentData[1];
    String serviceType = appointmentData[3];
    double amount = getServicePrice(serviceType);

    if (saveToFile) {
        // If we are TRYING to save, but it already exists, block the action
        if (existingPayment != null) {
            return "ERROR: Payment already exists for Appointment " + appointmentID + ".";
        } 

        // Proceed with creating new payment
        paymentID = generatePaymentID();
        date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String record = paymentID + "|" + appointmentID + "|" + amount + "|" + paymentMethod + "|" + date;
        savePayment(record);
    } else {
        // Mode: View existing receipt
        if (existingPayment != null) {
            paymentID = existingPayment[0];
            date = existingPayment[4];
            // Update paymentMethod to what was actually saved in the file
            paymentMethod = existingPayment[3]; 
        } else {
            return "ERROR: No existing payment record found to display.";
        }
    }

    return generateReceipt(paymentID, customerName, appointmentID, serviceType, amount, paymentMethod, date);
}

    
    private static String[] getAppointmentDetails(String appointmentID) {

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_App))) {

            String line;
            while ((line = br.readLine()) != null) {

                String[] data = line.split("\\|");

                if (data[0].equals(appointmentID)) {
                    return data;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    

    public static double getServicePrice(String serviceType) {

    ServiceType service =
            ServiceType.fromDisplayName(serviceType);

    if (service != null) {
        return service.getPrice();
    }

    return 0.0;
    }
    
    
    public static int getServiceDuration(String serviceType) {

    ServiceType service =
            ServiceType.fromDisplayName(serviceType);

    if (service != null) {
        return service.getDuration();
    }

    return 0;
    }
    
    private static String generatePaymentID() {

        int count = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_Pay))) {
            while (br.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            // file might not exist yet
        }

        return String.format("P%03d", count + 1);
    }

    
    private static void savePayment(String record) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_Pay, true))) {
            bw.write(record);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ==============================
    // 6. RECEIPT GENERATION
    // ==============================
    public static String generateReceipt(String paymentID, String customerName,
                                         String appointmentID, String serviceType,
                                         double amount, String method, String date) {

        return "===== RECEIPT ========\n" +
               "Receipt ID: " + paymentID + "\n" +
               "Customer: " + customerName + "\n" +
               "Appointment ID: " + appointmentID + "\n" +
               "Service: " + serviceType + "\n" +
               "Amount Paid: RM" + String.format("%.2f", amount) + "\n" +
               "Payment Method: " + method + "\n" +
               "Date: " + date + "\n" +
               "===================";
    }
    
    
    
    public static String saveReceiptToFile(String receiptText) {

        try {
            if (receiptText == null || receiptText.isEmpty()) {
                return "ERROR: No receipt data.";
            }

            String customerName = "Unknown";
            String date = "UnknownDate";

            // Extract details
            for (String line : receiptText.split("\n")) {
                if (line.startsWith("Customer:")) {
                    customerName = line.replace("Customer:", "").trim();
                }
                if (line.startsWith("Date:")) {
                    date = line.replace("Date:", "").trim();
                }
            }

            
            customerName = customerName.replaceAll("[^a-zA-Z0-9]", "");

            String fileName = customerName + "_Receipt_" + date + ".txt";

            
            File folder = new File("Customers Receipts");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            File file = new File(folder, fileName);

            //  Write file
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write(receiptText);
            }

            return "SUCCESS: " + file.getPath();

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: Failed to save receipt.";
        }
    }
    
    public static String[] getExistingPaymentDetails(String appointmentID) {
    try (BufferedReader br = new BufferedReader(new FileReader(FILE_Pay))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] data = line.split("\\|");
            // data[1] is the appointmentID column in your record format
            if (data.length > 1 && data[1].equals(appointmentID)) {
                return data;
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return null;
    }
    
}