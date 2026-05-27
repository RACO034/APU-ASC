package asc_system.Manager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simple analysed reports for the manager dashboard.
 */
public class ReportService {

    public String buildSummaryReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== APU-ASC ANALYSED REPORT ===\n\n");

        int totalAppt = 0, completed = 0, pending = 0, assigned = 0;
        Map<String, Integer> byService = new LinkedHashMap<>();
        Map<String, Integer> byTechnician = new LinkedHashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader("data/appointments.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] d = line.split("\\|", -1);
                if (d.length < 8) {
                    continue;
                }
                totalAppt++;
                String status = d[7].trim();
                if ("Completed".equalsIgnoreCase(status)) {
                    completed++;
                } else if ("Pending".equalsIgnoreCase(status)) {
                    pending++;
                } else if ("Assigned".equalsIgnoreCase(status)) {
                    assigned++;
                }
                byService.merge(d[3].trim(), 1, Integer::sum);
                byTechnician.merge(d[2].trim(), 1, Integer::sum);
            }
        } catch (IOException e) {
            sb.append("Could not read appointments.\n");
        }

        sb.append("--- Appointments ---\n");
        sb.append("Total: ").append(totalAppt).append("\n");
        sb.append("Completed: ").append(completed).append("\n");
        sb.append("Assigned: ").append(assigned).append("\n");
        sb.append("Pending: ").append(pending).append("\n\n");

        sb.append("--- By service type ---\n");
        for (Map.Entry<String, Integer> e : byService.entrySet()) {
            sb.append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        }
        sb.append("\n--- By technician ---\n");
        for (Map.Entry<String, Integer> e : byTechnician.entrySet()) {
            sb.append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        }

        double revenue = 0;
        int paymentCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("data/Payments.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] d = line.split("\\|", -1);
                if (d.length >= 3) {
                    try {
                        revenue += Double.parseDouble(d[2].trim());
                        paymentCount++;
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (IOException e) {
            sb.append("\nCould not read payments.\n");
        }

        sb.append("\n--- Payments ---\n");
        sb.append("Transactions: ").append(paymentCount).append("\n");
        sb.append("Total revenue (RM): ").append(String.format("%.2f", revenue)).append("\n");

        sb.append("\n--- Current prices ---\n");
        PriceService ps = new PriceService();
        for (Map.Entry<String, Double> e : ps.getAllPrices().entrySet()) {
            sb.append(e.getKey()).append(": RM").append(String.format("%.2f", e.getValue())).append("\n");
        }

        sb.append("\n=== END OF REPORT ===\n");
        return sb.toString();
    }
}
