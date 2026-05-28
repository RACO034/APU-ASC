package asc_system.Technician;
 
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;


/**
 *
 * @author XL
 */
public class TechnicianDashboard extends JFrame{
    // data
    private final Technician current;
    private List<Appointment> appointments;
    
    // File paths 
    private static final String APPOINTMENT_FILE = "data/appointments.txt";
    private static final String CUSTOMER_FILE = "data/customers.txt";
    
    static {
        FileHandler.ensureFileExists(APPOINTMENT_FILE);
        FileHandler.ensureFileExists(CUSTOMER_FILE);
    }
    
    // layout
    private JPanel cardPanel;
    private CardLayout cardLayout;
   
    
    // appointment table references
    private DefaultTableModel dashTableModel;
    private DefaultTableModel apptTableModel;
    
    // feedback panel refs
    private Appointment selectedForFeedback;
    private JLabel fbApptLabel;
    private JLabel fbCommentsValue;
    private JLabel fbServiceField;
    private JTextArea fbRemarksArea;
    
    // column definition
     private static final String[] APPT_COLS =
        {"ID", "Customer ID", "Service", "Date", "Time", "Dur.", "Status", "Action"};
    // Action column index
    private static final int COL_ACTION = 7;
    
    //constructors
    public TechnicianDashboard(Technician technician) {
        this.current = technician;
        this.appointments = current.viewAssignedAppointments();
 
        setTitle("APU-ASC  |  Technician Dashboard");
        setSize(860, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
 
        // Main split: sidebar | card panel
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildSidebar(), buildCardPanel());
        split.setDividerLocation(180);
        split.setDividerSize(1);
        split.setEnabled(false);          // fixed width sidebar
        add(split);
        setVisible(true);
    }
    
    // SIDEBAR
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(10, 8, 10, 8));
 
        // Header – name + role (no custom avatar painting)
        JLabel nameLabel = new JLabel(nvl(current.getUsername()));
        nameLabel.setFont(new Font("Dialog", Font.BOLD, 13));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        String idStr = current.getTechnicianId() != null ? current.getTechnicianId().toString() : "—";
        JLabel idLabel = new JLabel("ID · " + idStr);
        idLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
        idLabel.setForeground(Color.GRAY);
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String tradeStr = current.getTrade() != null ? current.getTrade().toString() : "—";
        JLabel roleLabel = new JLabel("Technician · " + tradeStr);
        roleLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
        roleLabel.setForeground(Color.GRAY);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        String statusStr = current.getStatus() != null ? current.getStatus().toString() : "—";
        JLabel statusLabel = new JLabel("Status: " + statusStr);
        statusLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        sidebar.add(nameLabel);
        sidebar.add(Box.createVerticalStrut(3));
        sidebar.add(idLabel);
        sidebar.add(Box.createVerticalStrut(2));
        sidebar.add(roleLabel);
        sidebar.add(Box.createVerticalStrut(2));
        sidebar.add(statusLabel);
        sidebar.add(new JSeparator());
        sidebar.add(Box.createVerticalStrut(8));
 
        // Nav buttons
        for (String[] nav : new String[][]{
            {"Dashboard",    "DASHBOARD"},
            {"Appointments", "APPOINTMENTS"},
            {"Feedback",     "FEEDBACK"},
            {"My Profile",   "PROFILE"}
        }) {
            JButton btn = new JButton(nav[0]);
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
            btn.addActionListener(e -> showCard(nav[1]));
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(4));
        }
 
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(new JSeparator());
 
        JButton logout = new JButton("Log out");
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        logout.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out?", "Log out",
                JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) dispose();
        });
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(logout);
 
        return sidebar;
    }
 
    
    // CARD PANEL
    
     private JPanel buildCardPanel() {
        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.add(buildDashboardView(), "DASHBOARD");
        cardPanel.add(buildAppointmentsView(), "APPOINTMENTS");
        cardPanel.add(buildFeedbackView(), "FEEDBACK");
        cardPanel.add(buildProfileView(), "PROFILE");
        return cardPanel;
    }
 
    private void showCard(String name) {
        cardLayout.show(cardPanel, name);
    }
    
    
    // DASHBOARD
    
     private JPanel buildDashboardView() {
        JPanel root = new JPanel(new BorderLayout(0, 8));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
 
        root.add(new JLabel("Dashboard – Today's overview"), BorderLayout.NORTH);
 
        // Stat summary row
        long pending   = appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.PENDING).count();
        long inProg    = appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.ASSIGNED).count();
        long completed = appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.COMPLETED).count();
        long needFB    = appointments.stream()
            .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED
                      && (a.getTechnicianFeedback() == null || a.getTechnicianFeedback().isEmpty()))
            .count();
 
        JPanel statRow = new JPanel(new GridLayout(1, 4, 6, 0));
        statRow.add(statPanel("Total Assigned",String.valueOf(appointments.size())));
        statRow.add(statPanel("In Progress",String.valueOf(inProg)));
        statRow.add(statPanel("Completed",String.valueOf(completed)));
        statRow.add(statPanel("Pending Feedback",String.valueOf(needFB)));
        root.add(statRow, BorderLayout.NORTH);   // replaced the label above
 
        // BUG FIX 1: dashboard table uses APPT_COLS (8 cols) and isCellEditable
        // corrected to col == COL_ACTION (index 7)
        dashTableModel = new DefaultTableModel(APPT_COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == COL_ACTION; }
        };
        populateDashTable();
 
        JTable table = new JTable(dashTableModel);
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        installButtonColumn(table, dashTableModel, COL_ACTION, "Details",
            row -> showAppointmentDetails(appointments.get(row)));
 
        JButton viewAll = new JButton("View all appointments →");
        viewAll.addActionListener(e -> showCard("APPOINTMENTS"));
 
        JPanel center = new JPanel(new BorderLayout(0, 4));
        center.add(viewAll,                BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
 
        // Two-row NORTH: stats then table area
        JPanel north = new JPanel(new BorderLayout(0, 8));
        north.add(statRow, BorderLayout.NORTH);
 
        root.add(north,  BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        return root;
    }
 
    /** BUG FIX 1: now adds all 8 cells matching APPT_COLS. */
    private void populateDashTable() {
        dashTableModel.setRowCount(0);
        for (Appointment a : appointments) {
            dashTableModel.addRow(new Object[]{
                a.getAppointmentID(),
                a.getCustomerID(),
                a.getServiceType(),
                a.getScheduledDateStr(),  
                a.getScheduledTimeStr(),
                a.getDuration() + "h",
                a.getStatus() != null ? a.getStatus().toString() : "—",
                "Details"
            });
        }
    }
 
    private JPanel statPanel(String title, String value) {
        JPanel p = new JPanel(new GridLayout(2, 1));
        p.setBorder(BorderFactory.createTitledBorder(title));
        JLabel val = new JLabel(value, SwingConstants.CENTER);
        val.setFont(new Font("Dialog", Font.BOLD, 20));
        p.add(val);
        return p;
    }
    
    
    // APPOINTMENTS VIEW
    
     private JPanel buildAppointmentsView() {
        JPanel root = new JPanel(new BorderLayout(0, 6));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        root.add(new JLabel("My Appointments"), BorderLayout.NORTH);
 
        // Filter bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterBar.add(new JLabel("Filter by status:"));
        String[] statuses = {"All Status", "Pending", "Assigned", "Completed"};
        JComboBox<String> filterBox = new JComboBox<>(statuses);
 
        apptTableModel = new DefaultTableModel(APPT_COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == COL_ACTION; }
        };
        refreshApptTable("All Status");
 
        JTable table = new JTable(apptTableModel);
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        installButtonColumn(table, apptTableModel, COL_ACTION, "Details",
            row -> onApptAction(row));
 
        filterBox.addActionListener(e ->
            refreshApptTable((String) filterBox.getSelectedItem()));
        filterBar.add(filterBox);
 
        root.add(filterBar,             BorderLayout.NORTH);
        root.add(new JScrollPane(table),BorderLayout.CENTER);
        return root;
    }
 
    //single refreshApptTable method, writes 8 cells into 8-column model.
    private void refreshApptTable(String filter) {
        if (apptTableModel == null) return;
        apptTableModel.setRowCount(0);
        for (Appointment a : appointments) {
            if (!"All Status".equals(filter)) {
                AppointmentStatus fs = AppointmentStatus.fromFileLabel(filter);
                if (a.getStatus() != fs) continue;
            }
            apptTableModel.addRow(new Object[]{
                a.getAppointmentID(),
                a.getCustomerID(),
                a.getServiceType(),
                a.getScheduledDateStr(),
                a.getScheduledTimeStr(),
                a.getDuration() + "h",
                a.getStatus() != null ? a.getStatus().toString() : "—",
                "Details"
            });
        }
    }
 
    private void onApptAction(int row) {
        String apptId = (String) apptTableModel.getValueAt(row, 0);
        Appointment target = findById(apptId);
        if (target == null) return;
 
        String[] options = {"View Details", "Mark as Completed", "Write Feedback", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this,
            "Appointment: " + apptId + "  –  " + target.getCustomerID(),
            "Appointment Actions",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
            null, options, options[0]);
 
        switch (choice) {
            case 0 -> showAppointmentDetails(target);
            case 1 -> markCompleted(target);
            case 2 -> openFeedbackFor(target);
        }
    }
    
    
    // FEEDBACK VIEW
    
     private JPanel buildFeedbackView() {
        JPanel root = new JPanel(new BorderLayout(0, 6));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
 
        fbApptLabel = new JLabel("No appointment selected – go to Appointments and choose 'Write Feedback'.");
        fbApptLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        root.add(fbApptLabel, BorderLayout.NORTH);
 
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(5, 5, 5, 5);
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weightx = 0;
 
        // Customer comments (read-only display)
        g.gridx = 0; g.gridy = 0;
        form.add(new JLabel("Customer comments:"), g);
        fbCommentsValue = new JLabel("—");
        fbCommentsValue.setBorder(BorderFactory.createEtchedBorder());
        g.gridx = 1; g.weightx = 1;
        form.add(fbCommentsValue, g);
 
        // Service performed
        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        form.add(new JLabel("Service performed:"), g);
        fbServiceField = new JLabel("—");
        fbServiceField.setBorder(BorderFactory.createEtchedBorder());
        g.gridx = 1; g.weightx = 1;
        form.add(fbServiceField, g);
 
        // Technician remarks
        g.gridx = 0; g.gridy = 2; g.weightx = 0; g.anchor = GridBagConstraints.NORTHWEST;
        form.add(new JLabel("Technician remarks:"), g);
        fbRemarksArea = new JTextArea(5, 30);
        fbRemarksArea.setLineWrap(true);
        fbRemarksArea.setWrapStyleWord(true);
        g.gridx = 1; g.weightx = 1;
        form.add(new JScrollPane(fbRemarksArea), g);
 
        // Submit
        JButton submitBtn = new JButton("Submit Feedback");
        submitBtn.addActionListener(e -> onSubmitFeedback());
        g.gridx = 1; g.gridy = 3; g.anchor = GridBagConstraints.WEST;
        form.add(submitBtn, g);
 
        root.add(form, BorderLayout.CENTER);
        return root;
    }
 
    private void openFeedbackFor(Appointment appt) {
        selectedForFeedback = appt;
        fbApptLabel.setText("Feedback for appointment: " + appt.getAppointmentID()
            + "  ·  " + appt.getCustomerID());
        fbCommentsValue.setText(
            appt.getCustomerComments() == null || appt.getCustomerComments().isEmpty()
                ? "(no customer comments)" : appt.getCustomerComments());
        fbServiceField.setText(appt.getServiceType());
        fbRemarksArea.setText(appt.getTechnicianFeedback() != null
            ? appt.getTechnicianFeedback() : "");
        showCard("FEEDBACK");
    }
 
    private void onSubmitFeedback() {
        if (selectedForFeedback == null) {
            JOptionPane.showMessageDialog(this,
                "Please select an appointment from the Appointments panel first.",
                "No appointment selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String remarks = fbRemarksArea.getText().trim();
        
        if (remarks.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Feedback remarks cannot be empty.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String cleanedRemarks = remarks.replace("\n", " ");
        
        String service = fbServiceField.getText().trim();

        try {
            current.submitFeedback(selectedForFeedback.getAppointmentID(), cleanedRemarks);
            appointments = current.viewAssignedAppointments();
            refreshApptTable("All Status");
            populateDashTable();
            JOptionPane.showMessageDialog(this, "Feedback submitted successfully.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            showCard("APPOINTMENTS");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Save failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    // PROFILE VIEW
    
    private JPanel buildProfileView() {
        JPanel root = new JPanel(new BorderLayout(0, 6));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        root.add(new JLabel("Edit Profile"), BorderLayout.NORTH);
 
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(4, 5, 4, 5);
        g.fill    = GridBagConstraints.HORIZONTAL;
 
        JTextField tfUsername = new JTextField(nvl(current.getUsername()), 20);
        JTextField tfEmail = new JTextField(nvl(current.getEmail()), 20);
        JTextField tfPhone = new JTextField(nvl(current.getPhoneNumber()), 20);
        JTextField tfAge = new JTextField(String.valueOf(current.getAge()), 20);
        JTextField tfNationality = new JTextField(nvl(current.getNationality()), 20);
        JTextField tfAddress = new JTextField(nvl(current.getHomeAddress()), 20);
 
        String[] genders = {"Male", "Female"};
        JComboBox<String> cbGender = new JComboBox<>(genders);
        cbGender.setSelectedItem(current.getGender());
 
        JComboBox<TechnicianTrade>  cbTrade = new JComboBox<>(TechnicianTrade.values());
        JComboBox<TechnicianStatus> cbStatus = new JComboBox<>(TechnicianStatus.values());
        if (current.getTrade() != null) cbTrade.setSelectedItem(current.getTrade());
        if (current.getStatus() != null) cbStatus.setSelectedItem(current.getStatus());
 
        int row = 0;
        addRow(form, g, row++, "Username:", tfUsername);
        addRow(form, g, row++, "Email:", tfEmail);
        addRow(form, g, row++, "Phone:", tfPhone);
        addRow(form, g, row++, "Age:", tfAge);
        addRow(form, g, row++, "Gender:", cbGender);
        addRow(form, g, row++, "Nationality:", tfNationality);
        addRow(form, g, row++, "Address:", tfAddress);
        addRow(form, g, row++, "Trade:", cbTrade);
        addRow(form, g, row++, "Status:", cbStatus);
 
        JButton saveBtn = new JButton("Save Changes");
        g.gridx = 0; g.gridy = row; g.gridwidth = 2;
        g.insets = new Insets(10, 5, 5, 5);
        form.add(saveBtn, g);
 
        saveBtn.addActionListener(e -> onSaveProfile(
            tfUsername, tfEmail, tfPhone, tfAge,
            cbGender, tfNationality, tfAddress, cbTrade, cbStatus));
 
        root.add(new JScrollPane(form), BorderLayout.CENTER);
        return root;
    }
 
    private void addRow(JPanel p, GridBagConstraints g, int row, String lbl, JComponent field) {
        g.gridwidth = 1;
        g.gridx = 0; g.gridy = row; g.weightx = 0.25;
        g.insets = new Insets(4, 5, 4, 5);
        p.add(new JLabel(lbl), g);
        g.gridx = 1; g.weightx = 0.75;
        p.add(field, g);
    }
 
    private void onSaveProfile(JTextField tfUsername, JTextField tfEmail,
                                JTextField tfPhone,    JTextField tfAge,
                                JComboBox<?> cbGender, JTextField tfNat,
                                JTextField tfAddr,     JComboBox<TechnicianTrade>  cbTrade,
                                JComboBox<TechnicianStatus> cbStatus) {
        try {
            current.setUsername(tfUsername.getText().trim());
            current.setEmail(tfEmail.getText().trim());
            current.setPhoneNumber(tfPhone.getText().trim());
            current.setAge(Integer.parseInt(tfAge.getText().trim()));
            current.setGender((String) cbGender.getSelectedItem());
            current.setNationality(tfNat.getText().trim());
            current.setHomeAddress(tfAddr.getText().trim());
            current.setTrade((TechnicianTrade)  cbTrade.getSelectedItem());
            current.setStatus((TechnicianStatus) cbStatus.getSelectedItem());
            // saved to technician.txt
            new TechnicianRepository().saveTechnician(current);
            JOptionPane.showMessageDialog(this, "Profile updated.",
                "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Age must be a whole number.",
                "Validation error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(this, iae.getMessage(),
                "Validation error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // SHARED ACTIONS
    
    private void showAppointmentDetails(Appointment a) {
        String msg = String.format(
            "Appointment ID : %s%nCustomer ID    : %s%nService        : %s%n" +
            "Date / Time    : %s  %s  (%dh)%nStatus         : %s%n%n" +
            "Customer notes : %s%nTech feedback  : %s",
            a.getAppointmentID(), 
            a.getCustomerID(),
            a.getServiceType(), 
            a.getScheduledDateStr(), 
            a.getScheduledTimeStr(),
            a.getDuration(), 
            a.getStatus(),
            nvl(a.getCustomerComments()), 
            nvl(a.getTechnicianFeedback()));
        JTextArea ta = new JTextArea(msg);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ta.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(ta),
            "Appointment – " + a.getAppointmentID(), JOptionPane.PLAIN_MESSAGE);
    }
 
    private void markCompleted(Appointment appt) {
        if (appt.getStatus() == AppointmentStatus.COMPLETED) {
            JOptionPane.showMessageDialog(this,
                "This appointment is already completed.", "Info",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int c = JOptionPane.showConfirmDialog(this,
            "Mark " + appt.getAppointmentID() + " as Completed?",
            "Confirm", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            current.updateAppointmentStatus(appt.getAppointmentID());
            appointments = current.viewAssignedAppointments();
            refreshApptTable("All Status");
            populateDashTable();
            JOptionPane.showMessageDialog(this, "Status updated to Completed.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
 
    private Appointment findById(String id) {
        return appointments.stream()
            .filter(a -> id.equals(a.getAppointmentID()))
            .findFirst().orElse(null);
    }
    
    
    // BUTTON COLUMN HELPER
    
     private void installButtonColumn(JTable table, DefaultTableModel model,
                                      int col, String label,
                                      java.util.function.IntConsumer onClick) {
        table.getColumnModel().getColumn(col).setCellRenderer(
            (t, v, sel, foc, r, c) -> new JButton(label));
        table.getColumnModel().getColumn(col).setCellEditor(
            new DefaultCellEditor(new JCheckBox()) {
                private int currentRow;
                private final JButton btn = new JButton(label);
                {
                    btn.addActionListener(e -> { fireEditingStopped(); onClick.accept(currentRow); });
                }
                @Override
                public Component getTableCellEditorComponent(JTable t, Object v,
                        boolean sel, int r, int c) {
                    currentRow = r; return btn;
                }
                @Override public Object getCellEditorValue() { return label; }
            });
    }
     
    // UTILITIES
 
    private static String nvl(String s) { return s == null ? "—" : s; }
    
    
}
