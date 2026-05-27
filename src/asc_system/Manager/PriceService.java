package asc_system.Manager;

import asc_system.FileHandler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Normal (1 hour) and major (3 hour) service prices.
 */
public class PriceService {

    public static final String MINOR_SERVICE = "Minor Service";
    public static final String MAJOR_SERVICE = "Major Service";

    private static final String FILE_PATH = "data/prices.txt";

    static {
        FileHandler.ensureFileExists(FILE_PATH);
    }

    public Map<String, Double> getAllPrices() {
        Map<String, Double> map = new LinkedHashMap<>();
        map.put(MINOR_SERVICE, 100.0);
        map.put(MAJOR_SERVICE, 300.0);
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(":", 2);
                if (p.length == 2) {
                    try {
                        map.put(p[0].trim(), Double.parseDouble(p[1].trim()));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public void savePrices(double minorPrice, double majorPrice) {
        if (minorPrice <= 0 || majorPrice <= 0) {
            throw new IllegalArgumentException("Prices must be greater than zero.");
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            bw.write(MINOR_SERVICE + ":" + minorPrice);
            bw.newLine();
            bw.write(MAJOR_SERVICE + ":" + majorPrice);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
