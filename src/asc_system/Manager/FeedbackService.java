package asc_system.Manager;

import asc_system.FileHandler;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads customer comments and technician feedback from appointments.
 */
public class FeedbackService {

    private static final String FILE_PATH = "data/appointments.txt";

    static {
        FileHandler.ensureFileExists(FILE_PATH);
    }

    public List<String[]> getAllFeedbackRows() {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] d = line.split("\\|", -1);
                if (d.length >= 10) {
                    String comment = d[8] != null ? d[8].trim() : "";
                    String feedback = d[9] != null ? d[9].trim() : "";
                    if (!comment.isEmpty() || !feedback.isEmpty()) {
                        rows.add(new String[]{
                            d[0], d[1], d[2], d[3], d[4], d[7], comment, feedback
                        });
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rows;
    }
}
