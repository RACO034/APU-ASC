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
import java.io.*;
import java.util.*;

public class CS_AppointmentRepository {

    private static final String FILE_PATH = "data/Appointments.txt";

   static {
        FileHandler.ensureFileExists(FILE_PATH);
    };

    public List<String[]> getAllAppointments() {
        List<String[]> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = br.readLine()) != null) {
                list.add(line.split("\\|"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void saveAll(List<String[]> dataList) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String[] data : dataList) {
                bw.write(String.join("|", data));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}