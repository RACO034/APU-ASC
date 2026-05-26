
package asc_system.Technician;


/**
 *
 * @author XL
 */

// similar to cs filehandler, duplicated for easier access
import java.io.File;
import java.io.IOException;

public class FileHandler {

    public static void ensureFileExists(String filePath) {
        try {
            File file = new File(filePath);

            // Ensure "data" folder exists
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs(); // creates /data folder
                System.out.println("Folder created: " + parentDir.getPath());
            }

            // Ensure file exists
            if (!file.exists()) {
                file.createNewFile();
                System.out.println("File created: " + file.getPath());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // to get timestamp for operations - date joined, appointment creations...
    // so timestamp same format everywhere
    public static String getTimestamp(){
        return java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
    }
}
