package asc_system.CounterStaff;


import java.io.BufferedReader;
import java.io.FileReader;
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
public class CS_CustomerRepository {

    private static final String FILE_PATH = "data/customers.txt";

   static {
        FileHandler.ensureFileExists(FILE_PATH);
    }
   
    public List<String[]> getAllCustomers() {
        List<String[]> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");

                if (data.length == 11) { // safety check
                    list.add(data);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }
}