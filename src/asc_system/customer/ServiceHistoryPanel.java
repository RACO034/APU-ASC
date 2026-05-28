package asc_system.customer;

import asc_system.FileConstants;
import asc_system.FileHandler;
import asc_system.UIConstants;
import asc_system.models.Customer;
import asc_system.Session;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ServiceHistoryPanel — shows customer's appointments and payments in a table.
 *
 * Adapted to existing files:
 *
 * appointments.txt:
 * apptId|custId|techId|service|date|time|duration|status|customerComment|technicianFeedback
 *
 * Payments.txt:
 * paymentId|appointmentId|amount|method|date
 */
public class ServiceHistoryPanel extends JPanel {
    private final Customer customer;
    private DefaultTableModel model;
    private JTable table;
    private JTextArea detailArea;

    public ServiceHistoryPanel(Customer customer) {
        this.customer = customer;
        initUI();
        loadTable("All");
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_MAIN);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel lblFilter = new JLabel("Status:");
        lblFilter.setFont(UIConstants.BODY_FONT);
        JComboBox<String> cbFilter = new JComboBox<>(new String[]{"All", "Pending", "Assigned", "Completed"});
        JButton exportBtn = new JButton("Export to .txt");
        stylePrimary(exportBtn);

        top.add(lblFilter);
        top.add(cbFilter);
        top.add(exportBtn);
        add(top, BorderLayout.NORTH);

        String[] cols = {"Appointment ID", "Date", "Service Type", "Status", "Amount Paid"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(36);
        table.setFont(UIConstants.BODY_FONT);

        JTableHeader header = table.getTableHeader();
        header.setBackground(UIConstants.PRIMARY);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new Color(179, 219, 255));
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : UIConstants.BG_CARD);
                }
                return c;
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER));
        add(sp, BorderLayout.CENTER);

        detailArea = new JTextArea(6, 80);
        detailArea.setEditable(false);
        detailArea.setFont(UIConstants.BODY_FONT);
        detailArea.setBackground(UIConstants.BG_CARD);
        detailArea.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER));
        add(new JScrollPane(detailArea), BorderLayout.SOUTH);

        cbFilter.addActionListener(e -> loadTable((String) cbFilter.getSelectedItem()));
        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0) {
                showDetails(String.valueOf(model.getValueAt(r, 0)));
            }
        });
        exportBtn.addActionListener(e -> exportVisible());
    }

    private void loadTable(String status) {
        model.setRowCount(0);
        String custId = getCustomerId();
        if (custId.isEmpty()) {
            return;
        }

        Map<String, String> paymentsByAppt = loadPaymentsAmountByAppointment();

        FileHandler.ensureFileExists(FileConstants.APPOINTMENTS);
        try (BufferedReader br = new BufferedReader(new FileReader(FileConstants.APPOINTMENTS))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] a = line.split("\\|", -1);
                if (a.length < 8) continue;
                if (!custId.equals(a[1])) continue;

                String apptId = a[0];
                String date = a.length > 4 ? a[4] : "";
                String service = a.length > 3 ? a[3] : "";
                String stat = a.length > 7 ? a[7] : "";

                if (status != null && !"All".equalsIgnoreCase(status) && !stat.equalsIgnoreCase(status)) {
                    continue;
                }

                String amt = paymentsByAppt.getOrDefault(apptId, "0.00");
                model.addRow(new Object[]{apptId, date, service, stat, amt});
            }
        } catch (IOException ignored) {
        }
    }

    private Map<String, String> loadPaymentsAmountByAppointment() {
        Map<String, String> map = new HashMap<>();
        FileHandler.ensureFileExists(FileConstants.PAYMENTS);
        try (BufferedReader br = new BufferedReader(new FileReader(FileConstants.PAYMENTS))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length < 3) continue;
                String apptId = p[1];
                String amount = p[2];
                map.put(apptId, amount);
            }
        } catch (IOException ignored) {
        }
        return map;
    }

    private void showDetails(String apptId) {
        FileHandler.ensureFileExists(FileConstants.APPOINTMENTS);
        StringBuilder sb = new StringBuilder();

        String[] apptRow = null;
        try (BufferedReader br = new BufferedReader(new FileReader(FileConstants.APPOINTMENTS))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(apptId + "|")) {
                    apptRow = line.split("\\|", -1);
                    break;
                }
            }
        } catch (IOException ignored) {
        }

        if (apptRow != null && apptRow.length >= 8) {
            sb.append("Appointment ID: ").append(get(apptRow, 0)).append('\n');
            sb.append("Customer ID: ").append(get(apptRow, 1)).append('\n');
            sb.append("Technician ID: ").append(get(apptRow, 2)).append('\n');
            sb.append("Service Type: ").append(get(apptRow, 3)).append('\n');
            sb.append("Date: ").append(get(apptRow, 4)).append('\n');
            sb.append("Time: ").append(get(apptRow, 5)).append('\n');
            sb.append("Duration (hrs): ").append(get(apptRow, 6)).append('\n');
            sb.append("Status: ").append(get(apptRow, 7)).append('\n');
            sb.append("Customer Comment: ").append(get(apptRow, 8)).append('\n');
            sb.append("Technician Feedback: ").append(get(apptRow, 9)).append('\n');
        }

        // append payment info (if exists)
        FileHandler.ensureFileExists(FileConstants.PAYMENTS);
        try (BufferedReader br = new BufferedReader(new FileReader(FileConstants.PAYMENTS))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length < 5) continue;
                if (apptId.equals(p[1])) {
                    sb.append('\n');
                    sb.append("Payment ID: ").append(p[0]).append('\n');
                    sb.append("Amount: RM").append(p[2]).append('\n');
                    sb.append("Method: ").append(p[3]).append('\n');
                    sb.append("Date: ").append(p[4]).append('\n');
                    break;
                }
            }
        } catch (IOException ignored) {
        }

        detailArea.setText(sb.toString());
    }

    private void exportVisible() {
        String custId = getCustomerId();
        if (custId.isEmpty()) return;

        File dir = new File(FileConstants.EXPORTS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String outPath = FileConstants.EXPORTS_DIR + "history_" + custId + ".txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outPath))) {
            bw.write("Appointment ID | Date | Service Type | Status | Amount Paid");
            bw.newLine();
            for (int r = 0; r < model.getRowCount(); r++) {
                List<String> cols = new ArrayList<>();
                for (int c = 0; c < model.getColumnCount(); c++) {
                    cols.add(String.valueOf(model.getValueAt(r, c)));
                }
                bw.write(String.join(" | ", cols));
                bw.newLine();
            }
            JOptionPane.showMessageDialog(this, "Exported to " + outPath);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
        }
    }

    private String getCustomerId() {
        if (customer != null && customer.getId() != null) return customer.getId();
        if (Session.getCurrentCustomer() != null) return Session.getCurrentCustomer().getId();
        return "";
    }

    private String get(String[] arr, int idx) {
        return (arr != null && idx >= 0 && idx < arr.length) ? arr[idx] : "";
    }

    private void stylePrimary(JButton b) {
        b.setBackground(UIConstants.PRIMARY_DARK);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
    }
}

