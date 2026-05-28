package asc_system.customer;

import asc_system.FileConstants;
import asc_system.FileHandler;
import asc_system.UIConstants;
import asc_system.Session;
import asc_system.models.Customer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class HomePanel extends JPanel {
    private final Customer customer;

    public HomePanel(Customer customer) {
        this.customer = customer;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_MAIN);

        String name = (customer != null ? customer.getName()
                : (Session.getCurrentCustomer() != null ? Session.getCurrentCustomer().getName() : "Customer"));
        JLabel welcome = new JLabel("Hello, " + name + "!");
        welcome.setFont(UIConstants.TITLE_FONT);
        welcome.setForeground(UIConstants.TEXT_PRIMARY);
        welcome.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(welcome, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 4, 12, 12));
        cards.setOpaque(false);
        cards.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        cards.add(createCard("Total Appointments", String.valueOf(countAppointments())));
        cards.add(createCard("Pending", String.valueOf(countByStatus("Pending"))));
        cards.add(createCard("Completed", String.valueOf(countByStatus("Completed"))));
        cards.add(createCard("Total Paid (RM)", String.format("%.2f", sumPaymentsForCustomerAppointments())));

        add(cards, BorderLayout.CENTER);

        JPanel south = new JPanel();
        south.setOpaque(false);
        JButton book = new JButton("Book Appointment");
        stylePrimary(book);
        south.add(book);
        JLabel info = new JLabel(" ");
        info.setForeground(UIConstants.TEXT_SECONDARY);
        south.add(info);
        add(south, BorderLayout.SOUTH);

        book.addActionListener(e -> info.setText("To book an appointment, please visit the service centre or contact counter staff."));
    }

    private JPanel createCard(String title, String value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UIConstants.BG_CARD);
        p.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER));
        JLabel t = new JLabel(title);
        t.setFont(UIConstants.SUBHEADING_FONT);
        t.setForeground(UIConstants.TEXT_SECONDARY);
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIConstants.PRIMARY);
        header.add(t, BorderLayout.CENTER);
        JLabel v = new JLabel(value, SwingConstants.CENTER);
        v.setFont(UIConstants.TITLE_FONT);
        v.setForeground(UIConstants.TEXT_PRIMARY);
        p.add(header, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    private int countAppointments() {
        int c = 0;
        FileHandler.ensureFileExists(FileConstants.APPOINTMENTS);
        try (BufferedReader br = new BufferedReader(new FileReader(FileConstants.APPOINTMENTS))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length > 1 && p[1].equals(getCustomerId())) c++;
            }
        } catch (IOException ignored) {
        }
        return c;
    }

    private int countByStatus(String status) {
        int c = 0;
        FileHandler.ensureFileExists(FileConstants.APPOINTMENTS);
        try (BufferedReader br = new BufferedReader(new FileReader(FileConstants.APPOINTMENTS))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length > 7 && p[1].equals(getCustomerId()) && p[7].equalsIgnoreCase(status)) c++;
            }
        } catch (IOException ignored) {
        }
        return c;
    }

    /**
     * Adapted to current Payments.txt format:
     * paymentId|appointmentId|amount|method|date
     *
     * We sum only payments whose appointment belongs to the customer.
     */
    private double sumPaymentsForCustomerAppointments() {
        String custId = getCustomerId();
        if (custId.isEmpty()) return 0.0;

        Set<String> customerApptIds = new HashSet<>();
        FileHandler.ensureFileExists(FileConstants.APPOINTMENTS);
        try (BufferedReader br = new BufferedReader(new FileReader(FileConstants.APPOINTMENTS))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] a = line.split("\\|", -1);
                if (a.length > 1 && custId.equals(a[1])) {
                    customerApptIds.add(a[0]);
                }
            }
        } catch (IOException ignored) {
        }

        double sum = 0.0;
        FileHandler.ensureFileExists(FileConstants.PAYMENTS);
        try (BufferedReader br = new BufferedReader(new FileReader(FileConstants.PAYMENTS))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length < 3) continue;
                String apptId = p[1];
                if (!customerApptIds.contains(apptId)) continue;
                try {
                    sum += Double.parseDouble(p[2]);
                } catch (Exception ignored) {
                }
            }
        } catch (IOException ignored) {
        }

        return sum;
    }

    private String getCustomerId() {
        if (customer != null && customer.getId() != null) return customer.getId();
        if (Session.getCurrentCustomer() != null) return Session.getCurrentCustomer().getId();
        return "";
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

