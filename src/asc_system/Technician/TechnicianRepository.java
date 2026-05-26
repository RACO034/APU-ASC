//exact same as Ian's
package asc_system.Technician;

/**
 *
 * @author Ian
 */
import java.io.*;
import java.util.*;

public class TechnicianRepository {

    
    private static final String FILE_PATH = "data/technicians.txt";

   static {
        FileHandler.ensureFileExists(FILE_PATH);
    }

    public List<String[]> getAllTechnicians() {
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
}
