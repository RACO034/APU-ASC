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
}