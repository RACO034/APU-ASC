package asc_system.Manager;

import asc_system.LoginFrame;
import asc_system.Technician.Technician;
import asc_system.Technician.TechnicianStatus;
import asc_system.Technician.TechnicianTrade;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridLayout;
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
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * Manager dashboard: staff CRUD, prices, feedback, analysed reports.
 */
public class ManagerDashboard extends javax.swing.JFrame {

    private final Manager currentManager;
    private final StaffManagementService staffService = new StaffManagementService();
    private final PriceService priceService = new PriceService();
    private final FeedbackService feedbackService = new FeedbackService();
    private final ReportService reportService = new ReportService();

    private CardLayout cardLayout;
    private JPanel panelStaff;
    private JPanel panelPrices;
    private JPanel panelFeedback;
    private JPanel panelReports;

    private DefaultTableModel managerTableModel;
    private DefaultTableModel csTableModel;
    private DefaultTableModel techTableModel;
    private DefaultTableModel feedbackTableModel;

    private JTextField mfId, mfName, mfEmail, mfPhone, mfAge, mfAddress, mfNationality, mfPassword;
    private JComboBox<String> mfGender;

    private JTextField cfId, cfName, cfPhone, cfEmail, cfAddress, cfNationality, cfPassword;
    private JComboBox<String> cfGender;

    private JTextField tfId, tfName, tfEmail, tfPhone, tfAge, tfAddress, tfNationality, tfPassword;
    private JComboBox<String> tfGender, tfTrade, tfStatus;

    private JTextField txtMinorPrice, txtMajorPrice;
    private JTextArea txtReport;

    public ManagerDashboard(Manager manager) {
        this.currentManager = manager;
        initComponents();
        populateCardPanels();
        setLocationRelativeTo(null);
        lblWelcome.setText("Manager: " + manager.getUsername() + " (" + manager.getManagerID() + ")");
        showPanel("staff");
        refreshAllTables();
        loadPricesIntoForm();
    }

    private void populateCardPanels() {
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);

        panelStaff = buildStaffPanel();
        panelPrices = buildPricesPanel();
        panelFeedback = buildFeedbackPanel();
        panelReports = buildReportsPanel();

        cardPanel.add(panelStaff, "staff");
        cardPanel.add(panelPrices, "prices");
        cardPanel.add(panelFeedback, "feedback");
        cardPanel.add(panelReports, "reports");
    }

    private void showPanel(String name) {
        cardLayout.show(cardPanel, name);
    }

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {
        LoginFrame.returnToLogin(this);
    }

    private void btnManageStaffActionPerformed(java.awt.event.ActionEvent evt) {
        showPanel("staff");
    }

    private void btnSetPricesActionPerformed(java.awt.event.ActionEvent evt) {
        showPanel("prices");
    }

    private void btnViewFeedbackActionPerformed(java.awt.event.ActionEvent evt) {
        showPanel("feedback");
        refreshFeedbackTable();
    }

    private void btnAnalysedReportsActionPerformed(java.awt.event.ActionEvent evt) {
        showPanel("reports");
        txtReport.setText(reportService.buildSummaryReport());
    }

    private JPanel buildStaffPanel() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Managers", buildManagerCrud());
        tabs.addTab("Counter Staff", buildCounterStaffCrud());
        tabs.addTab("Technicians", buildTechnicianCrud());
        JPanel p = new JPanel(new BorderLayout());
        p.add(tabs, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildManagerCrud() {
        managerTableModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Email", "Phone", "Age", "Gender"}, 0);
        JTable table = new JTable(managerTableModel);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int r = table.getSelectedRow();
                mfId.setText(managerTableModel.getValueAt(r, 0).toString());
                mfName.setText(managerTableModel.getValueAt(r, 1).toString());
                mfEmail.setText(managerTableModel.getValueAt(r, 2).toString());
                mfPhone.setText(managerTableModel.getValueAt(r, 3).toString());
                mfAge.setText(managerTableModel.getValueAt(r, 4).toString());
                mfGender.setSelectedItem(managerTableModel.getValueAt(r, 5).toString());
            }
        });

        mfId = field(); mfId.setEditable(false);
        mfName = field(); mfEmail = field(); mfPhone = field(); mfAge = field();
        mfAddress = field(); mfNationality = field(); mfPassword = field();
        mfGender = new JComboBox<>(new String[]{"Male", "Female"});

        JPanel form = formGrid(
                row("Manager ID", mfId), row("Username", mfName), row("Password", mfPassword),
                row("Email", mfEmail), row("Phone", mfPhone), row("Age", mfAge),
                row("Gender", mfGender), row("Address", mfAddress), row("Nationality", mfNationality));

        JPanel actions = new JPanel();
        JButton add = new JButton("Add");
        JButton upd = new JButton("Update");
        JButton del = new JButton("Delete");
        add.addActionListener(e -> saveManager(false));
        upd.addActionListener(e -> saveManager(true));
        del.addActionListener(e -> deleteManager());
        actions.add(add); actions.add(upd); actions.add(del);

        return splitTableForm(table, form, actions);
    }

    private JPanel buildCounterStaffCrud() {
        csTableModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Gender", "Phone", "Email"}, 0);
        JTable table = new JTable(csTableModel);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int r = table.getSelectedRow();
                cfId.setText(csTableModel.getValueAt(r, 0).toString());
                cfName.setText(csTableModel.getValueAt(r, 1).toString());
                cfGender.setSelectedItem(csTableModel.getValueAt(r, 2).toString());
                cfPhone.setText(csTableModel.getValueAt(r, 3).toString());
                cfEmail.setText(csTableModel.getValueAt(r, 4).toString());
            }
        });

        cfId = field(); cfId.setEditable(false);
        cfName = field(); cfPassword = field();
        cfPhone = field(); cfEmail = field(); cfAddress = field(); cfNationality = field();
        cfGender = new JComboBox<>(new String[]{"Male", "Female"});

        JPanel form = formGrid(
                row("Staff ID", cfId), row("Name", cfName), row("Password", cfPassword),
                row("Gender", cfGender), row("Phone", cfPhone), row("Email", cfEmail),
                row("Address", cfAddress), row("Nationality", cfNationality));

        JPanel actions = new JPanel();
        JButton add = new JButton("Add");
        JButton upd = new JButton("Update");
        JButton del = new JButton("Delete");
        add.addActionListener(e -> saveCounterStaff(false));
        upd.addActionListener(e -> saveCounterStaff(true));
        del.addActionListener(e -> deleteCounterStaff());
        actions.add(add); actions.add(upd); actions.add(del);

        return splitTableForm(table, form, actions);
    }

    private JPanel buildTechnicianCrud() {
        techTableModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Trade", "Status", "Phone"}, 0);
        JTable table = new JTable(techTableModel);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int r = table.getSelectedRow();
                String id = techTableModel.getValueAt(r, 0).toString();
                for (String[] row : staffService.getTechnicianRepository().getAllTechnicians()) {
                    if (row[0].equals(id)) {
                        tfId.setText(row[0]);
                        tfName.setText(row[1]);
                        tfEmail.setText(row[3]);
                        tfPhone.setText(row[4]);
                        tfAge.setText(row[5]);
                        tfGender.setSelectedItem(row[6]);
                        tfAddress.setText(row[7]);
                        tfNationality.setText(row[8]);
                        tfTrade.setSelectedItem(row[10]);
                        tfStatus.setSelectedItem(row[11]);
                        break;
                    }
                }
            }
        });

        tfId = field(); tfId.setEditable(false);
        tfName = field(); tfEmail = field(); tfPhone = field(); tfAge = field();
        tfAddress = field(); tfNationality = field(); tfPassword = field();
        tfGender = new JComboBox<>(new String[]{"Male", "Female"});
        tfTrade = new JComboBox<>(enumNames(TechnicianTrade.values()));
        tfStatus = new JComboBox<>(enumNames(TechnicianStatus.values()));

        JPanel form = formGrid(
                row("Technician ID", tfId), row("Username", tfName), row("Password", tfPassword),
                row("Email", tfEmail), row("Phone", tfPhone), row("Age", tfAge),
                row("Gender", tfGender), row("Address", tfAddress), row("Nationality", tfNationality),
                row("Trade", tfTrade), row("Status", tfStatus));

        JPanel actions = new JPanel();
        JButton add = new JButton("Add");
        JButton upd = new JButton("Update");
        JButton del = new JButton("Delete");
        add.addActionListener(e -> saveTechnician(false));
        upd.addActionListener(e -> saveTechnician(true));
        del.addActionListener(e -> deleteTechnician());
        actions.add(add); actions.add(upd); actions.add(del);

        return splitTableForm(table, form, actions);
    }

    private JPanel buildPricesPanel() {
        JPanel p = new JPanel(new GridLayout(4, 2, 10, 10));
        p.setBorder(BorderFactory.createTitledBorder("Service prices (Normal = 1 hour, Major = 3 hours)"));
        txtMinorPrice = field();
        txtMajorPrice = field();
        p.add(new JLabel("Minor / Normal service (RM):"));
        p.add(txtMinorPrice);
        p.add(new JLabel("Major service (RM):"));
        p.add(txtMajorPrice);
        JButton save = new JButton("Save Prices");
        save.addActionListener(e -> {
            try {
                double minor = Double.parseDouble(txtMinorPrice.getText().trim());
                double major = Double.parseDouble(txtMajorPrice.getText().trim());
                priceService.savePrices(minor, major);
                JOptionPane.showMessageDialog(this, "Prices updated.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        p.add(new JLabel());
        p.add(save);
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(p, BorderLayout.NORTH);
        return wrap;
    }

    private JPanel buildFeedbackPanel() {
        feedbackTableModel = new DefaultTableModel(
                new String[]{"Appt ID", "Customer", "Technician", "Service", "Date", "Status", "Comment", "Feedback"}, 0);
        JTable table = new JTable(feedbackTableModel);
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildReportsPanel() {
        txtReport = new JTextArea();
        txtReport.setEditable(false);
        JPanel p = new JPanel(new BorderLayout());
        JButton refresh = new JButton("Refresh Report");
        refresh.addActionListener(e -> txtReport.setText(reportService.buildSummaryReport()));
        p.add(refresh, BorderLayout.NORTH);
        p.add(new JScrollPane(txtReport), BorderLayout.CENTER);
        return p;
    }

    private void refreshAllTables() {
        managerTableModel.setRowCount(0);
        for (String[] row : staffService.getManagerRepository().getAllManagers()) {
            managerTableModel.addRow(new Object[]{row[0], row[1], row[3], row[4], row[5], row[6]});
        }
        if (mfId != null && mfId.getText().isEmpty()) {
            mfId.setText(staffService.getManagerRepository().generateManagerID());
        }

        csTableModel.setRowCount(0);
        for (String[] row : staffService.getAllCounterStaff()) {
            csTableModel.addRow(new Object[]{row[0], row[1], row[2], row[4], row[5]});
        }
        if (cfId != null && cfId.getText().isEmpty()) {
            cfId.setText(staffService.generateCounterStaffId());
        }

        techTableModel.setRowCount(0);
        for (String[] row : staffService.getTechnicianRepository().getAllTechnicians()) {
            techTableModel.addRow(new Object[]{row[0], row[1], row[10], row[11], row[4]});
        }
        if (tfId != null && tfId.getText().isEmpty()) {
            tfId.setText(staffService.getTechnicianRepository().generateTechnicianId());
        }
    }

    private void refreshFeedbackTable() {
        feedbackTableModel.setRowCount(0);
        for (String[] r : feedbackService.getAllFeedbackRows()) {
            feedbackTableModel.addRow(r);
        }
    }

    private void loadPricesIntoForm() {
        var prices = priceService.getAllPrices();
        txtMinorPrice.setText(String.valueOf(prices.getOrDefault(PriceService.MINOR_SERVICE, 100.0)));
        txtMajorPrice.setText(String.valueOf(prices.getOrDefault(PriceService.MAJOR_SERVICE, 300.0)));
    }

    private void saveManager(boolean update) {
        try {
            Manager m = new Manager();
            String id = mfId.getText().trim();
            if (!update) {
                id = staffService.getManagerRepository().generateManagerID();
                mfId.setText(id);
            }
            m.setManagerID(id);
            m.setUsername(mfName.getText().trim());
            String pass = mfPassword.getText();
            if (!pass.isEmpty()) {
                m.setPassword(pass);
            } else if (update) {
                for (String[] row : staffService.getManagerRepository().getAllManagers()) {
                    if (row[0].equals(id)) {
                        m.password = row[2];
                        break;
                    }
                }
            } else {
                throw new IllegalArgumentException("Password required for new manager.");
            }
            m.setEmail(mfEmail.getText().trim());
            m.setPhoneNumber(mfPhone.getText().trim());
            m.setAge(Integer.parseInt(mfAge.getText().trim()));
            m.setGender(mfGender.getSelectedItem().toString());
            m.setHomeAddress(mfAddress.getText().trim());
            m.setNationality(mfNationality.getText().trim());
            if (!update) {
                m.setDateJoinedNow();
            }
            staffService.getManagerRepository().saveManager(m);
            JOptionPane.showMessageDialog(this, update ? "Manager updated." : "Manager created.");
            mfId.setText(staffService.getManagerRepository().generateManagerID());
            mfPassword.setText("");
            refreshAllTables();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteManager() {
        String id = mfId.getText().trim();
        if (id.equals(currentManager.getManagerID())) {
            JOptionPane.showMessageDialog(this, "You cannot delete your own account while logged in.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Delete manager " + id + "?") == JOptionPane.YES_OPTION) {
            staffService.deleteManager(id);
            refreshAllTables();
        }
    }

    private void saveCounterStaff(boolean update) {
        try {
            String id = cfId.getText().trim();
            if (!update) {
                id = staffService.generateCounterStaffId();
                cfId.setText(id);
            }
            String pass = cfPassword.getText();
            if (pass.isEmpty() && update) {
                for (String[] row : staffService.getAllCounterStaff()) {
                    if (row[0].equals(id)) {
                        pass = row[3];
                        break;
                    }
                }
            }
            if (pass.isEmpty()) {
                throw new IllegalArgumentException("Password required.");
            }
            staffService.saveCounterStaff(id, cfName.getText().trim(), cfGender.getSelectedItem().toString(),
                    pass, cfPhone.getText().trim(), cfEmail.getText().trim(),
                    cfAddress.getText().trim(), cfNationality.getText().trim());
            JOptionPane.showMessageDialog(this, update ? "Counter staff updated." : "Counter staff created.");
            cfId.setText(staffService.generateCounterStaffId());
            cfPassword.setText("");
            refreshAllTables();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCounterStaff() {
        String id = cfId.getText().trim();
        if (JOptionPane.showConfirmDialog(this, "Delete counter staff " + id + "?") == JOptionPane.YES_OPTION) {
            staffService.deleteCounterStaff(id);
            refreshAllTables();
        }
    }

    private void saveTechnician(boolean update) {
        try {
            String id = tfId.getText().trim();
            if (!update) {
                id = staffService.getTechnicianRepository().generateTechnicianId();
                tfId.setText(id);
            }
            Technician t = new Technician(id,
                    TechnicianTrade.valueOf(tfTrade.getSelectedItem().toString()),
                    TechnicianStatus.valueOf(tfStatus.getSelectedItem().toString()));
            t.setUsername(tfName.getText().trim());
            t.setEmail(tfEmail.getText().trim());
            t.setPhoneNumber(tfPhone.getText().trim());
            t.setAge(Integer.parseInt(tfAge.getText().trim()));
            t.setGender(tfGender.getSelectedItem().toString());
            t.setHomeAddress(tfAddress.getText().trim());
            t.setNationality(tfNationality.getText().trim());
            String pass = tfPassword.getText();
            if (update) {
                staffService.getTechnicianRepository().saveTechnician(t, pass.isEmpty() ? null : pass);
            } else {
                if (pass.isEmpty()) {
                    throw new IllegalArgumentException("Password required for new technician.");
                }
                staffService.getTechnicianRepository().addTechnician(t, pass, FileHandler.getTimestamp());
            }
            JOptionPane.showMessageDialog(this, update ? "Technician updated." : "Technician created.");
            tfId.setText(staffService.getTechnicianRepository().generateTechnicianId());
            tfPassword.setText("");
            refreshAllTables();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTechnician() {
        String id = tfId.getText().trim();
        if (JOptionPane.showConfirmDialog(this, "Delete technician " + id + "?") == JOptionPane.YES_OPTION) {
            staffService.deleteTechnician(id);
            refreshAllTables();
        }
    }

    private static JTextField field() {
        return new JTextField();
    }

    private static JPanel row(String label, java.awt.Component field) {
        JPanel p = new JPanel(new BorderLayout(6, 0));
        p.add(new JLabel(label), BorderLayout.WEST);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private static JPanel formGrid(JPanel... rows) {
        JPanel g = new JPanel(new GridLayout(rows.length, 1, 4, 4));
        for (JPanel r : rows) {
            g.add(r);
        }
        return g;
    }

    private static JPanel splitTableForm(JTable table, JPanel form, JPanel actions) {
        JPanel right = new JPanel(new BorderLayout());
        right.add(form, BorderLayout.CENTER);
        right.add(actions, BorderLayout.SOUTH);
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    private static String[] enumNames(Enum<?>[] values) {
        String[] a = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            a[i] = values[i].name();
        }
        return a;
    }

    public Manager getCurrentManager() {
        return currentManager;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerPanel = new javax.swing.JPanel();
        lblWelcome = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        sidebarPanel = new javax.swing.JPanel();
        btnManageStaff = new javax.swing.JButton();
        btnSetPrices = new javax.swing.JButton();
        btnViewFeedback = new javax.swing.JButton();
        btnAnalysedReports = new javax.swing.JButton();
        cardPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("APU-ASC | Manager Dashboard");
        setMinimumSize(new java.awt.Dimension(960, 640));

        headerPanel.setBackground(new java.awt.Color(0, 102, 204));

        lblWelcome.setFont(new java.awt.Font("Segoe UI", 1, 16));
        lblWelcome.setForeground(new java.awt.Color(255, 255, 255));
        lblWelcome.setText("Welcome, Manager");

        btnLogout.setText("Logout");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblWelcome, javax.swing.GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLogout)
                .addGap(16, 16, 16))
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblWelcome)
                    .addComponent(btnLogout))
                .addGap(12, 12, 12))
        );

        sidebarPanel.setBackground(new java.awt.Color(204, 229, 255));
        sidebarPanel.setLayout(new java.awt.GridLayout(4, 1, 6, 6));

        btnManageStaff.setText("Manage Staff");
        btnManageStaff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManageStaffActionPerformed(evt);
            }
        });
        sidebarPanel.add(btnManageStaff);

        btnSetPrices.setText("Set Prices");
        btnSetPrices.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetPricesActionPerformed(evt);
            }
        });
        sidebarPanel.add(btnSetPrices);

        btnViewFeedback.setText("View Feedback");
        btnViewFeedback.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewFeedbackActionPerformed(evt);
            }
        });
        sidebarPanel.add(btnViewFeedback);

        btnAnalysedReports.setText("Analysed Reports");
        btnAnalysedReports.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnalysedReportsActionPerformed(evt);
            }
        });
        sidebarPanel.add(btnAnalysedReports);

        cardPanel.setLayout(new java.awt.BorderLayout());

        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(headerPanel, java.awt.BorderLayout.NORTH);

        javax.swing.JPanel bodyPanel = new javax.swing.JPanel();
        bodyPanel.setLayout(new java.awt.BorderLayout());
        bodyPanel.add(sidebarPanel, java.awt.BorderLayout.WEST);
        bodyPanel.add(cardPanel, java.awt.BorderLayout.CENTER);
        getContentPane().add(bodyPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAnalysedReports;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnManageStaff;
    private javax.swing.JButton btnSetPrices;
    private javax.swing.JButton btnViewFeedback;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel lblWelcome;
    private javax.swing.JPanel sidebarPanel;
    // End of variables declaration//GEN-END:variables
}
