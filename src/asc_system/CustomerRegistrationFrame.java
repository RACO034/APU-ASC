package asc_system;

import asc_system.CounterStaff.Customer;
import asc_system.CounterStaff.CustomerManagement;
import asc_system.CounterStaff.FileHandler;
import javax.swing.JOptionPane;

/**
 * Customer self-registration only (staff accounts are created by the manager).
 */
public class CustomerRegistrationFrame extends javax.swing.JFrame {

    public CustomerRegistrationFrame() {
        initComponents();
        setLocationRelativeTo(null);
        txtCustomerId.setText(CustomerManagement.generateCustomerID());
        txtCustomerId.setEditable(false);
    }

    private void btnRegisterActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            String password = new String(txtPassword.getPassword());
            String confirm = new String(txtConfirmPassword.getPassword());
            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            validateCustomerPassword(password);

            String id = txtCustomerId.getText().trim();
            if (CustomerCredentials.exists(id)) {
                JOptionPane.showMessageDialog(this, "Customer ID already registered.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Customer c = new Customer();
            c.setCustomerID(id);
            c.setUsername(txtUsername.getText().trim());
            c.setGender(cmbGender.getSelectedItem().toString());
            c.setAge(Integer.parseInt(txtAge.getText().trim()));
            c.setPhoneNumber(txtPhone.getText().trim());
            c.setEmail(txtEmail.getText().trim());
            c.setHomeAddress(txtAddress.getText().trim());
            c.setNationality(txtNationality.getText().trim());
            c.setCarPlate(txtCarPlate.getText().trim());
            c.setCarModel(txtCarModel.getText().trim());
            c.setDateAdded(FileHandler.getTimestamp());

            CustomerManagement.addCustomer(c);
            CustomerCredentials.save(id, password);

            JOptionPane.showMessageDialog(this,
                    "Registration successful!\nYour Customer ID: " + id,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            LoginFrame.returnToLogin(this);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a number.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {
        LoginFrame.returnToLogin(this);
    }

    private static void validateCustomerPassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters.");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();
        formPanel = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblCustomerId = new javax.swing.JLabel();
        txtCustomerId = new javax.swing.JTextField();
        lblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        lblConfirmPassword = new javax.swing.JLabel();
        txtConfirmPassword = new javax.swing.JPasswordField();
        lblGender = new javax.swing.JLabel();
        cmbGender = new javax.swing.JComboBox<>();
        lblAge = new javax.swing.JLabel();
        txtAge = new javax.swing.JTextField();
        lblPhone = new javax.swing.JLabel();
        txtPhone = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        lblAddress = new javax.swing.JLabel();
        txtAddress = new javax.swing.JTextField();
        lblNationality = new javax.swing.JLabel();
        txtNationality = new javax.swing.JTextField();
        lblCarPlate = new javax.swing.JLabel();
        txtCarPlate = new javax.swing.JTextField();
        lblCarModel = new javax.swing.JLabel();
        txtCarModel = new javax.swing.JTextField();
        btnRegister = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("APU-ASC | Customer Registration");

        formPanel.setBackground(new java.awt.Color(204, 229, 255));

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 18));
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("Customer Registration");

        lblCustomerId.setText("Customer ID");
        lblUsername.setText("Full name");
        lblPassword.setText("Password");
        lblConfirmPassword.setText("Confirm password");
        lblGender.setText("Gender");
        lblAge.setText("Age");
        lblPhone.setText("Phone");
        lblEmail.setText("Email");
        lblAddress.setText("Address");
        lblNationality.setText("Nationality");
        lblCarPlate.setText("Car plate");
        lblCarModel.setText("Car model");

        cmbGender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female" }));

        btnRegister.setBackground(new java.awt.Color(0, 102, 204));
        btnRegister.setForeground(new java.awt.Color(255, 255, 255));
        btnRegister.setText("Register");
        btnRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegisterActionPerformed(evt);
            }
        });

        btnBack.setText("Back to Login");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout formPanelLayout = new javax.swing.GroupLayout(formPanel);
        formPanel.setLayout(formPanelLayout);
        formPanelLayout.setHorizontalGroup(
            formPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formPanelLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(formPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addComponent(lblCustomerId)
                    .addComponent(txtCustomerId)
                    .addComponent(lblUsername)
                    .addComponent(txtUsername)
                    .addComponent(lblPassword)
                    .addComponent(txtPassword)
                    .addComponent(lblConfirmPassword)
                    .addComponent(txtConfirmPassword)
                    .addComponent(lblGender)
                    .addComponent(cmbGender, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblAge)
                    .addComponent(txtAge)
                    .addComponent(lblPhone)
                    .addComponent(txtPhone)
                    .addComponent(lblEmail)
                    .addComponent(txtEmail)
                    .addComponent(lblAddress)
                    .addComponent(txtAddress)
                    .addComponent(lblNationality)
                    .addComponent(txtNationality)
                    .addComponent(lblCarPlate)
                    .addComponent(txtCarPlate)
                    .addComponent(lblCarModel)
                    .addComponent(txtCarModel)
                    .addComponent(btnRegister, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnBack, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(28, 28, 28))
        );
        formPanelLayout.setVerticalGroup(
            formPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formPanelLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblTitle)
                .addGap(16, 16, 16)
                .addComponent(lblCustomerId)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCustomerId, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblUsername)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblPassword)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblConfirmPassword)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtConfirmPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblGender)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbGender, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblAge)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAge, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblPhone)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblEmail)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblAddress)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblNationality)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNationality, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblCarPlate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCarPlate, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblCarModel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCarModel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        scrollPane.setViewportView(formPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnRegister;
    private javax.swing.JComboBox<String> cmbGender;
    private javax.swing.JPanel formPanel;
    private javax.swing.JLabel lblAddress;
    private javax.swing.JLabel lblAge;
    private javax.swing.JLabel lblCarModel;
    private javax.swing.JLabel lblCarPlate;
    private javax.swing.JLabel lblConfirmPassword;
    private javax.swing.JLabel lblCustomerId;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblGender;
    private javax.swing.JLabel lblNationality;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblPhone;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtAge;
    private javax.swing.JTextField txtCarModel;
    private javax.swing.JTextField txtCarPlate;
    private javax.swing.JPasswordField txtConfirmPassword;
    private javax.swing.JTextField txtCustomerId;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtNationality;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
