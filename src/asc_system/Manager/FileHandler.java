package asc_system.Manager;

import java.io.File;
import java.io.IOException;

/**
 * Ensures data files exist before read/write operations.
 */
public class FileHandler {

    public static void ensureFileExists(String filePath) {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getTimestamp() {
        return java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
    }
}
