package asc_system;

import asc_system.Technician.Appointment;
import asc_system.Technician.AppointmentDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.List;

public class CustomerDashboard extends JFrame {

    private final Customer currentCustomer;
    private static final String APPOINTMENT_FILE = "data/appointments.txt";
    private static final String PAYMENT_FILE = "data/Payments.txt";

    static {
        FileHandler.ensureFileExists(APPOINTMENT_FILE);
        FileHandler.ensureFileExists(PAYMENT_FILE);
    }

    private final Color babyBlue = new Color(204, 229, 255);
    private final Color deepBlue = new Color(0, 102, 204);

    public CustomerDashboard(Customer customer) {
        this.currentCustomer = customer;
        setTitle("Customer Dashboard - " + customer.getUsername());
        setSize(900, 620);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(deepBlue);
        JLabel title = new JLabel("Welcome, " + customer.getUsername());
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(title, BorderLayout.WEST);
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> LoginFrame.returnToLogin(this));
        header.add(logout, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel sidebar = new JPanel(new GridLayout(5, 1, 5, 5));
        sidebar.setBackground(babyBlue);
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnProfile = createStyledButton("Edit Profile");
        JButton btnHistory = createStyledButton("Service & Payment History");
        JButton btnFeedback = createStyledButton("View Feedback");
        JButton btnComment = createStyledButton("Leave Comment");

        sidebar.add(btnProfile);
        sidebar.add(btnHistory);
        sidebar.add(btnFeedback);
        sidebar.add(btnComment);
        add(sidebar, BorderLayout.WEST);

        JPanel mainContent = new JPanel(new CardLayout());
        mainContent.setBackground(Color.WHITE);
        add(mainContent, BorderLayout.CENTER);

        btnProfile.addActionListener(e -> showProfilePanel(mainContent));
        btnHistory.addActionListener(e -> showHistoryPanel(mainContent));
        btnFeedback.addActionListener(e -> showFeedbackPanel(mainContent));
        btnComment.addActionListener(e -> showCommentPanel(mainContent));

        showProfilePanel(mainContent);
        setLocationRelativeTo(null);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(deepBlue));
        return btn;
    }

    private void showProfilePanel(JPanel container) {
        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Edit Profile"));

        JTextField txtEmail = new JTextField(currentCustomer.getEmail());
        JTextField txtPhone = new JTextField(currentCustomer.getPhoneNumber());
        JTextField txtAddress = new JTextField(currentCustomer.getHomeAddress());
        JTextField txtCarPlate = new JTextField(currentCustomer.getCarPlate());
        JTextField txtCarModel = new JTextField(currentCustomer.getCarModel());

        panel.add(new JLabel("Email:")); panel.add(txtEmail);
        panel.add(new JLabel("Phone:")); panel.add(txtPhone);
        panel.add(new JLabel("Address:")); panel.add(txtAddress);
        panel.add(new JLabel("Car plate:")); panel.add(txtCarPlate);
        panel.add(new JLabel("Car model:")); panel.add(txtCarModel);

        JButton saveBtn = new JButton("Update Profile");
        saveBtn.addActionListener(e -> {
            try {
                currentCustomer.setCustomerDetails(currentCustomer.getUsername(),
                        txtEmail.getText().trim(), txtPhone.getText().trim(),
                        txtAddress.getText().trim(), currentCustomer.getNationality());
                currentCustomer.setCarPlate(txtCarPlate.getText().trim());
                currentCustomer.setCarModel(txtCarModel.getText().trim());
                CustomerAccountService.saveCustomer(currentCustomer);
                JOptionPane.showMessageDialog(this, "Profile updated successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(new JLabel("")); panel.add(saveBtn);
        swapPanel(container, panel);
    }

    private void showHistoryPanel(JPanel container) {
        JTabbedPane tabs = new JTabbedPane();

        String[] serviceCols = {"Appt ID", "Service", "Technician", "Date", "Time", "Status"};
        DefaultTableModel serviceModel = new DefaultTableModel(serviceCols, 0);
        for (Appointment a : AppointmentDAO.getAll()) {
            if (currentCustomer.getCustomerID().equals(a.getCustomerID())) {
                serviceModel.addRow(new Object[]{
                    a.getAppointmentID(), a.getServiceType(), a.getTechnicianID(),
                    a.getScheduledDateStr(), a.getScheduledTimeStr(),
                    a.getStatus() != null ? a.getStatus().getFileLabel() : ""
                });
            }
        }
        tabs.addTab("Service history", new JScrollPane(new JTable(serviceModel)));

        String[] payCols = {"Payment ID", "Appointment", "Amount (RM)", "Method", "Date"};
        DefaultTableModel payModel = new DefaultTableModel(payCols, 0);
        try (BufferedReader br = new BufferedReader(new FileReader(PAYMENT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] d = line.split("\\|", -1);
                if (d.length >= 5) {
                    String[] appt = findAppointment(d[1]);
                    if (appt != null && appt[1].equals(currentCustomer.getCustomerID())) {
                        payModel.addRow(new Object[]{d[0], d[1], d[2], d[3], d[4]});
                    }
                }
            }
        } catch (IOException ignored) {
        }
        tabs.addTab("Payment history", new JScrollPane(new JTable(payModel)));

        swapPanel(container, tabs);
    }

    private void showFeedbackPanel(JPanel container) {
        String[] cols = {"Appt ID", "Service", "Technician feedback"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (Appointment a : AppointmentDAO.getAll()) {
            if (currentCustomer.getCustomerID().equals(a.getCustomerID())) {
                String fb = a.getTechnicianFeedback();
                if (fb != null && !fb.isEmpty()) {
                    model.addRow(new Object[]{a.getAppointmentID(), a.getServiceType(), fb});
                }
            }
        }
        swapPanel(container, new JScrollPane(new JTable(model)));
    }

    private void showCommentPanel(JPanel container) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Comment on an appointment"));

        JComboBox<String> cmbAppt = new JComboBox<>();
        for (Appointment a : AppointmentDAO.getAll()) {
            if (currentCustomer.getCustomerID().equals(a.getCustomerID())) {
                cmbAppt.addItem(a.getAppointmentID() + " - " + a.getServiceType());
            }
        }

        JTextArea txtComment = new JTextArea(5, 40);
        JButton save = new JButton("Submit comment");
        save.addActionListener(e -> {
            if (cmbAppt.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "No appointment selected.");
                return;
            }
            String apptId = cmbAppt.getSelectedItem().toString().split(" - ")[0];
            Appointment a = AppointmentDAO.getById(apptId);
            if (a == null) {
                return;
            }
            a.setCustomerComments(txtComment.getText().trim());
            AppointmentDAO.save(a);
            JOptionPane.showMessageDialog(this, "Comment saved.");
        });

        panel.add(new JLabel("Appointment:"), BorderLayout.NORTH);
        panel.add(cmbAppt, BorderLayout.NORTH);
        panel.add(new JScrollPane(txtComment), BorderLayout.CENTER);
        panel.add(save, BorderLayout.SOUTH);
        swapPanel(container, panel);
    }

    private String[] findAppointment(String apptId) {
        try (BufferedReader br = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] d = line.split("\\|", -1);
                if (d.length > 0 && d[0].equals(apptId)) {
                    return d;
                }
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    private void swapPanel(JPanel container, Component content) {
        container.removeAll();
        container.add(content);
        container.revalidate();
        container.repaint();
    }
}
