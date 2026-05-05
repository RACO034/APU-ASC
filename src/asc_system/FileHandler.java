package asc_system;
import java.io.File;
import java.io.IOException;

public class FileHandler {
    public static void ensureFileExists(String filePath) {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
        }
    }
}