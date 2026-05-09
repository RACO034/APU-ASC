
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
public class UserDetailsEdit {
    
    private static final String FILE_PATH = "data/Users.txt";

   static {
        FileHandler.ensureFileExists(FILE_PATH);
    }
    
    public static void updateUser(String userID, String name, String gender, String password,
                                      String phone, String email, String address,
                                      String nationality) {

        List<String> updatedLines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {

            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");

                // Match customer ID
                if (data[0].equals(userID)) {

                    // Rebuild updated record
                    String updatedRecord = userID + "|" + name + "|" + gender + "|" + password + "|" +
                                           phone + "|" + email + "|" + address + "|" +
                                           nationality  ;

                    updatedLines.add(updatedRecord);
                    found = true;

                } else {
                    updatedLines.add(line); // keep original
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
            System.out.println("Customer ID not found: " + userID);
        }
    }
    
    
    public static List<String[]> getAllUsers() {

        List<String[]> users = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {

            String line;

            while ((line = br.readLine()) != null) {

                if (!line.trim().isEmpty()) {
                    String[] data = line.split("\\|");

                    if (data.length >= 8) {
                        users.add(data);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return users;
    }
    
}
