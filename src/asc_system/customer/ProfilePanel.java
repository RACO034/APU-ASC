package asc_system.customer;

import asc_system.UIConstants;
import asc_system.Session;
import asc_system.models.Customer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ProfilePanel — view and edit customer profile, and change password.
 * UI is preserved; persistence is handled by this project's adapted CustomerRepository.
 */
public class ProfilePanel extends JPanel {
    private final Customer customer;

    private JTextField nameField, emailField, phoneField, townField;
    private JLabel idLabel, regDateLabel;
    private JLabel statusLabel;

    private JPasswordField oldPwdField, newPwdField, confirmPwdField;
    private JLabel pwdStatusLabel;

    public ProfilePanel(Customer customer) {
        this.customer = customer == null ? Session.getCurrentCustomer() : customer;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_MAIN);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;
        idLabel = new JLabel(customer.getId());
        regDateLabel = new JLabel(customer.getRegistrationDate());

        nameField = new JTextField(customer.getName(), 20);
        emailField = new JTextField(customer.getEmail(), 20);
        phoneField = new JTextField(customer.getPhone(), 15);
        townField = new JTextField(customer.getTown(), 15);

        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Customer ID:"), gbc); gbc.gridx = 1; form.add(idLabel, gbc); y++;
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Registration Date:"), gbc); gbc.gridx = 1; form.add(regDateLabel, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Name:"), gbc); gbc.gridx = 1; form.add(nameField, gbc); y++;
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Email:"), gbc); gbc.gridx = 1; form.add(emailField, gbc); y++;
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Phone:"), gbc); gbc.gridx = 1; form.add(phoneField, gbc); y++;
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Town:"), gbc); gbc.gridx = 1; form.add(townField, gbc); y++;

        JButton saveBtn = new JButton("Save Changes");
        stylePrimary(saveBtn);
        gbc.gridx = 1; gbc.gridy = y; form.add(saveBtn, gbc); y++;

        statusLabel = new JLabel(" ");
        gbc.gridx = 1; gbc.gridy = y; form.add(statusLabel, gbc); y++;

        add(form, BorderLayout.NORTH);

        JPanel pwdPanel = new JPanel(new GridBagLayout());
        pwdPanel.setOpaque(false);
        pwdPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        GridBagConstraints pgbc = new GridBagConstraints();
        pgbc.insets = new Insets(6, 6, 6, 6);
        pgbc.anchor = GridBagConstraints.WEST;

        oldPwdField = new JPasswordField(15);
        newPwdField = new JPasswordField(15);
        confirmPwdField = new JPasswordField(15);

        int py = 0;
        pgbc.gridx = 0; pgbc.gridy = py; pwdPanel.add(new JLabel("Old Password:"), pgbc); pgbc.gridx = 1; pwdPanel.add(oldPwdField, pgbc); py++;
        pgbc.gridx = 0; pgbc.gridy = py; pwdPanel.add(new JLabel("New Password:"), pgbc); pgbc.gridx = 1; pwdPanel.add(newPwdField, pgbc); py++;
        pgbc.gridx = 0; pgbc.gridy = py; pwdPanel.add(new JLabel("Confirm New Password:"), pgbc); pgbc.gridx = 1; pwdPanel.add(confirmPwdField, pgbc); py++;

        JButton changePwdBtn = new JButton("Change Password");
        stylePrimary(changePwdBtn);
        pgbc.gridx = 1; pgbc.gridy = py; pwdPanel.add(changePwdBtn, pgbc); py++;

        pwdStatusLabel = new JLabel(" ");
        pgbc.gridx = 1; pgbc.gridy = py; pwdPanel.add(pwdStatusLabel, pgbc);

        add(pwdPanel, BorderLayout.CENTER);

        saveBtn.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { saveProfile(); }
        });
        changePwdBtn.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { changePassword(); }
        });
    }

    private void saveProfile() {
        statusLabel.setForeground(UIConstants.ERROR);
        statusLabel.setText(" ");

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String town = townField.getText().trim();

        if (name.isEmpty()) { statusLabel.setText("Name cannot be empty"); return; }
        if (!email.contains("@") || !email.contains(".")) { statusLabel.setText("Invalid email"); return; }
        if (!phone.matches("\\d{10,11}")) { statusLabel.setText("Phone must be 10-11 digits"); return; }

        customer.setName(name);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setTown(town);

        new CustomerRepository().updateCustomer(customer);
        statusLabel.setForeground(UIConstants.SUCCESS);
        statusLabel.setText("Profile updated.");
    }

    private void changePassword() {
        pwdStatusLabel.setForeground(UIConstants.ERROR);
        pwdStatusLabel.setText(" ");

        String oldp = new String(oldPwdField.getPassword());
        String np = new String(newPwdField.getPassword());
        String cp = new String(confirmPwdField.getPassword());

        if (oldp.isEmpty() || np.isEmpty() || cp.isEmpty()) { pwdStatusLabel.setText("All password fields are required"); return; }

        String current = customer.getPassword() == null ? "" : customer.getPassword();
        if (!current.equals(oldp)) { pwdStatusLabel.setText("Old password is incorrect"); return; }
        if (np.length() < 8) { pwdStatusLabel.setText("New password must be at least 8 characters"); return; }
        if (!np.equals(cp)) { pwdStatusLabel.setText("New passwords do not match"); return; }

        customer.setPassword(np);
        new CustomerRepository().updateCustomer(customer);

        pwdStatusLabel.setForeground(UIConstants.SUCCESS);
        pwdStatusLabel.setText("Password changed.");
        oldPwdField.setText("");
        newPwdField.setText("");
        confirmPwdField.setText("");
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

