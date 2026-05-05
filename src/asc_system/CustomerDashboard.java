package asc_system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class CustomerDashboard extends JFrame {
    private Customer currentCustomer;
    // File paths as requested by groupmate
    private static final String APPOINTMENT_FILE = "data/appointments.txt";
    private static final String CUSTOMER_FILE = "data/customers.txt";

    static {
        FileHandler.ensureFileExists(APPOINTMENT_FILE);
        FileHandler.ensureFileExists(CUSTOMER_FILE);
    }

    // Theme Colors
    private final Color babyBlue = new Color(204, 229, 255);
    private final Color deepBlue = new Color(0, 102, 204);

    public CustomerDashboard(Customer customer) {
        this.currentCustomer = customer;
        setTitle("Customer Dashboard - " + customer.getUsername());
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setBackground(deepBlue);
        JLabel title = new JLabel("Welcome, " + customer.getUsername());
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Sidebar Menu
        JPanel sidebar = new JPanel(new GridLayout(4, 1, 5, 5));
        sidebar.setBackground(babyBlue);
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnProfile = createStyledButton("Edit Profile");
        JButton btnHistory = createStyledButton("Service History");
        JButton btnFeedback = createStyledButton("Feedbacks");
        JButton btnComment = createStyledButton("Leave Comment");

        sidebar.add(btnProfile);
        sidebar.add(btnHistory);
        sidebar.add(btnFeedback);
        sidebar.add(btnComment);
        add(sidebar, BorderLayout.WEST);

        // Main Content Area
        JPanel mainContent = new JPanel(new CardLayout());
        mainContent.setBackground(Color.WHITE);
        add(mainContent, BorderLayout.CENTER);

        // Action Listeners
        btnProfile.addActionListener(e -> showProfilePanel(mainContent));
        btnHistory.addActionListener(e -> showHistoryPanel(mainContent));
        
        setLocationRelativeTo(null);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(deepBlue));
        return btn;
    }

    // REQUIREMENT: Edit Personal Profile [cite: 30]
    private void showProfilePanel(JPanel container) {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Edit Profile"));

        JTextField txtEmail = new JTextField(currentCustomer.getEmail());
        JTextField txtPhone = new JTextField(currentCustomer.getPhoneNumber());
        JTextField txtAddress = new JTextField(currentCustomer.getHomeAddress());

        panel.add(new JLabel("Email:")); panel.add(txtEmail);
        panel.add(new JLabel("Phone:")); panel.add(txtPhone);
        panel.add(new JLabel("Address:")); panel.add(txtAddress);

        JButton saveBtn = new JButton("Update Profile");
        saveBtn.addActionListener(e -> {
            try {
                currentCustomer.setCustomerDetails(currentCustomer.getUsername(), txtEmail.getText(), txtPhone.getText(), txtAddress.getText(), currentCustomer.getNationality());
                JOptionPane.showMessageDialog(this, "Profile Updated Successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        panel.add(new JLabel("")); panel.add(saveBtn);
        
        container.removeAll();
        container.add(panel);
        container.revalidate();
        container.repaint();
    }

    // REQUIREMENT: Access Service & Payment History [cite: 30]
    private void showHistoryPanel(JPanel container) {
        String[] columns = {"Date", "Service Type", "Technician", "Status", "Price"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        
        // Reading from text file 
        try (BufferedReader br = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                // Filter by current customer's ID
                if (data[1].equals(currentCustomer.getCustomerID())) {
                    model.addRow(new Object[]{data[0], data[2], data[3], data[4], data[5]});
                }
            }
        } catch (IOException e) {
            System.out.println("No records found.");
        }

        container.removeAll();
        container.add(new JScrollPane(table));
        container.revalidate();
        container.repaint();
    }
}