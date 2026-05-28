package asc_system.customer;

import asc_system.FileConstants;
import asc_system.FileHandler;
import asc_system.UIConstants;
import asc_system.Session;
import asc_system.models.Customer;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

/**
 * My Comments tab — unchanged UI, adapted to existing appointments.txt structure.
 *
 * appointments.txt:
 * apptId|custId|techId|service|date|time|duration|status|customerComment|technicianFeedback
 *
 * Note: the file does not contain counter staff ID, so the "Counter Staff ID" field is left blank.
 */
public class CommentsPanel extends JPanel {
    private final Customer customer;
    private DefaultTableModel tableModel;
    private JTable table;

    private JComboBox<String> apptCombo;
    private JTextField csField;
    private JTextField techField;
    private JTextArea commentArea;
    private JLabel charCountLabel;
    private JLabel[] starLabels = new JLabel[5];
    private int selectedRating = 0;
    private JLabel statusLabel;

    public CommentsPanel(Customer customer) {
        this.customer = customer == null ? Session.getCurrentCustomer() : customer;
        initUI();
        loadComments();
        loadCompletedAppointments();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_MAIN);

        String[] cols = {"Comment ID", "Appt ID", "Counter Staff", "Technician", "Rating", "Date", "Comment"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        JScrollPane topScroll = new JScrollPane(table);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Appointment:"), gbc);
        apptCombo = new JComboBox<>(); gbc.gridx = 1; form.add(apptCombo, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Counter Staff ID:"), gbc);
        csField = new JTextField(12); csField.setEditable(false); gbc.gridx = 1; form.add(csField, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Technician ID:"), gbc);
        techField = new JTextField(12); techField.setEditable(false); gbc.gridx = 1; form.add(techField, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Comment:"), gbc);
        commentArea = new JTextArea(6, 40); gbc.gridx = 1; form.add(new JScrollPane(commentArea), gbc); y++;

        charCountLabel = new JLabel("0 / 500");
        gbc.gridx = 1; gbc.gridy = y; gbc.anchor = GridBagConstraints.EAST; form.add(charCountLabel, gbc);
        y++;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Rating:"), gbc);
        JPanel starPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); starPanel.setOpaque(false);
        for (int i = 0; i < 5; i++) {
            final int idx = i + 1;
            starLabels[i] = new JLabel("★");
            starLabels[i].setFont(new Font("Segoe UI", Font.PLAIN, 20));
            starLabels[i].setForeground(new Color(180, 180, 180));
            starLabels[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            starLabels[i].addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    selectedRating = idx;
                    updateStarDisplay();
                }
            });
            starPanel.add(starLabels[i]);
        }
        gbc.gridx = 1; form.add(starPanel, gbc); y++;

        JButton submitBtn = new JButton("Submit");
        submitBtn.setBackground(UIConstants.PRIMARY_DARK);
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setOpaque(true);
        submitBtn.setBorderPainted(false);
        gbc.gridx = 1; gbc.gridy = y; form.add(submitBtn, gbc); y++;

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(UIConstants.SUCCESS);
        gbc.gridx = 1; gbc.gridy = y; form.add(statusLabel, gbc);

        apptCombo.addActionListener(e -> fillApptIds());
        commentArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateCount(); }
            public void removeUpdate(DocumentEvent e) { updateCount(); }
            public void changedUpdate(DocumentEvent e) { updateCount(); }
            private void updateCount() { charCountLabel.setText(commentArea.getText().length() + " / 500"); }
        });
        submitBtn.addActionListener(e -> submitComment());

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topScroll, form);
        split.setResizeWeight(0.5);
        add(split, BorderLayout.CENTER);
    }

    private void loadComments() {
        tableModel.setRowCount(0);
        FileHandler.ensureFileExists(FileConstants.COMMENTS);
        String custId = getCustomerId();
        try (BufferedReader br = new BufferedReader(new FileReader(FileConstants.COMMENTS))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length < 8) continue;
                String cId = p[2];
                if (!cId.equals(custId)) continue;
                tableModel.addRow(new Object[]{p[0], p[1], p[3], p[4], p[6], p[7], p[5]});
            }
        } catch (IOException ignored) {
        }
    }

    private void loadCompletedAppointments() {
        apptCombo.removeAllItems();
        FileHandler.ensureFileExists(FileConstants.APPOINTMENTS);
        String custId = getCustomerId();
        try (BufferedReader br = new BufferedReader(new FileReader(FileConstants.APPOINTMENTS))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length < 8) continue;
                if (!p[1].equals(custId)) continue;
                if (!"Completed".equalsIgnoreCase(p[7])) continue;
                apptCombo.addItem(p[0]);
            }
        } catch (IOException ignored) {
        }

        if (apptCombo.getItemCount() > 0) {
            apptCombo.setSelectedIndex(0);
        }
        fillApptIds();
    }

    private void fillApptIds() {
        String appt = (String) apptCombo.getSelectedItem();
        if (appt == null) { csField.setText(""); techField.setText(""); return; }

        FileHandler.ensureFileExists(FileConstants.APPOINTMENTS);
        try (BufferedReader br = new BufferedReader(new FileReader(FileConstants.APPOINTMENTS))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(appt + "|")) {
                    String[] p = line.split("\\|", -1);
                    csField.setText(""); // no counter staff field in current data format
                    techField.setText(p.length > 2 ? p[2] : "");
                    break;
                }
            }
        } catch (IOException ignored) {
        }
    }

    private void submitComment() {
        statusLabel.setForeground(UIConstants.ERROR);
        statusLabel.setText(" ");

        String appt = (String) apptCombo.getSelectedItem();
        if (appt == null) { statusLabel.setText("No completed appointment selected."); return; }

        String text = commentArea.getText().trim();
        if (text.isEmpty()) { statusLabel.setText("Comment cannot be empty."); return; }
        if (text.length() > 500) { statusLabel.setText("Comment exceeds 500 characters."); return; }

        int rating = selectedRating;
        if (rating == 0) { statusLabel.setText("Please select a rating."); return; }

        String custId = getCustomerId();
        FileHandler.ensureFileExists(FileConstants.COMMENTS);
        try (BufferedReader br = new BufferedReader(new FileReader(FileConstants.COMMENTS))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length < 3) continue;
                if (p[1].equals(appt) && p[2].equals(custId)) {
                    statusLabel.setText("You have already commented on this appointment.");
                    return;
                }
            }
        } catch (IOException ignored) {
        }

        String newId = generateCommentId();
        String safeText = text.replace("|", " ").replace("\n", " ");
        String record = String.join("|",
                newId,
                appt,
                custId,
                csField.getText(),
                techField.getText(),
                safeText,
                String.valueOf(rating),
                LocalDate.now().toString()
        );

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FileConstants.COMMENTS, true))) {
            bw.write(record);
            bw.newLine();
            statusLabel.setForeground(UIConstants.SUCCESS);
            statusLabel.setText("Comment saved.");
            loadComments();
            commentArea.setText("");
            selectedRating = 0;
            updateStarDisplay();
        } catch (IOException ignored) {
            statusLabel.setForeground(UIConstants.ERROR);
            statusLabel.setText("Failed to save comment.");
        }
    }

    private void updateStarDisplay() {
        for (int i = 0; i < 5; i++) {
            if (starLabels[i] != null) {
                if (i < selectedRating) {
                    starLabels[i].setForeground(new Color(255, 193, 7));
                } else {
                    starLabels[i].setForeground(new Color(180, 180, 180));
                }
            }
        }
    }

    private String generateCommentId() {
        int count = 0;
        FileHandler.ensureFileExists(FileConstants.COMMENTS);
        try (BufferedReader br = new BufferedReader(new FileReader(FileConstants.COMMENTS))) {
            while (br.readLine() != null) {
                count++;
            }
        } catch (IOException ignored) {
        }
        return String.format("CMT-%04d", count + 1);
    }

    private String getCustomerId() {
        if (customer != null && customer.getId() != null) return customer.getId();
        if (Session.getCurrentCustomer() != null) return Session.getCurrentCustomer().getId();
        return "";
    }
}

