package asc_system;

import asc_system.CounterStaff.ProfileManagement;
import asc_system.Manager.ManagerDashboard;
import asc_system.Technician.TechnicianDashboard;
import javax.swing.JOptionPane;

/**
 * Universal login for Manager, Counter Staff, Technician, and Customer.
 */
public class LoginFrame extends javax.swing.JFrame {

    public LoginFrame() {
        initComponents();
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        lblUserId = new javax.swing.JLabel();
        txtUserId = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        btnLogin = new javax.swing.JButton();
        btnRegister = new javax.swing.JButton();
        lblHint = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("APU-ASC | Login");
        setResizable(false);

        mainPanel.setBackground(new java.awt.Color(204, 229, 255));

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 22));
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("APU Automotive Service Centre");

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblSubtitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSubtitle.setText("Sign in to continue");

        lblUserId.setText("User ID (M001 / CS001 / T001 / C00001)");
        txtUserId.setFont(new java.awt.Font("Segoe UI", 0, 14));

        lblPassword.setText("Password");
        txtPassword.setFont(new java.awt.Font("Segoe UI", 0, 14));

        btnLogin.setBackground(new java.awt.Color(0, 102, 204));
        btnLogin.setFont(new java.awt.Font("Segoe UI", 1, 14));
        btnLogin.setForeground(new java.awt.Color(255, 255, 255));
        btnLogin.setText("Login");
        btnLogin.addActionListener(this::btnLoginActionPerformed);

        btnRegister.setText("New Customer? Register here");
        btnRegister.addActionListener(this::btnRegisterActionPerformed);

        lblHint.setFont(new java.awt.Font("Segoe UI", 2, 11));
        lblHint.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHint.setText("Staff accounts are created by the Manager.");

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                    .addComponent(lblSubtitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblUserId)
                    .addComponent(txtUserId)
                    .addComponent(lblPassword)
                    .addComponent(txtPassword)
                    .addComponent(btnLogin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRegister, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblHint, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(36, 36, 36))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSubtitle)
                .addGap(22, 22, 22)
                .addComponent(lblUserId)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtUserId, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(lblPassword)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnRegister)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblHint)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);
        pack();
    }

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {
        String userId = txtUserId.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (userId.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter User ID and password.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        AuthService.Session session = AuthService.login(userId, password);
        if (session == null) {
            JOptionPane.showMessageDialog(this, "Invalid User ID or password.", "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        openDashboard(session);
        dispose();
    }

    private void openDashboard(AuthService.Session session) {
        switch (session.role) {
            case MANAGER -> new ManagerDashboard(session.manager).setVisible(true);
            case COUNTER_STAFF -> new ProfileManagement().setVisible(true);
            case TECHNICIAN -> new TechnicianDashboard(session.technician).setVisible(true);
            case CUSTOMER -> new CustomerDashboard(session.customer).setVisible(true);
            default -> {
            }
        }
    }

    private void btnRegisterActionPerformed(java.awt.event.ActionEvent evt) {
        CustomerRegistrationFrame reg = new CustomerRegistrationFrame();
        reg.setLocationRelativeTo(this);
        reg.setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        applyNimbus();
        javax.swing.SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    static void applyNimbus() {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static void returnToLogin(javax.swing.JFrame from) {
        LoginFrame login = new LoginFrame();
        login.setLocationRelativeTo(from);
        login.setVisible(true);
        from.dispose();
    }

    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnRegister;
    private javax.swing.JLabel lblHint;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUserId;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUserId;
}
