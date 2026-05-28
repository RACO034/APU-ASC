package asc_system.customer;

import asc_system.FileConstants;
import asc_system.FileHandler;
import asc_system.UIConstants;
import asc_system.Session;
import asc_system.models.Customer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Adapted to existing appointments.txt structure.
 *
 * Current appointments.txt:
 * apptId|custId|techId|service|date|time|duration|status|customerComment|technicianFeedback
 *
 * We display technician feedback from column 9 (if present).
 */
public class FeedbackViewPanel extends JPanel {
    private final Customer customer;

    public FeedbackViewPanel(Customer customer) {
        this.customer = customer == null ? Session.getCurrentCustomer() : customer;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_MAIN);

        JLabel title = new JLabel("Technician Feedback");
        title.setFont(UIConstants.TITLE_FONT);
        title.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        title.setForeground(UIConstants.TEXT_PRIMARY);
        add(title, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);
        list.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        String custId = customer != null ? customer.getId() : "";
        if (custId.isEmpty()) {
            list.add(centerLabel("No technician feedback yet."));
            add(new JScrollPane(list), BorderLayout.CENTER);
            return;
        }

        // apptId -> [date, serviceType, techId, feedbackText]
        Map<String, String[]> apptMap = new HashMap<>();
        FileHandler.ensureFileExists(FileConstants.APPOINTMENTS);
        try (BufferedReader br = new BufferedReader(new FileReader(FileConstants.APPOINTMENTS))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] a = line.split("\\|", -1);
                if (a.length < 8) continue;
                if (!custId.equals(a[1])) continue;

                String apptId = a[0];
                String techId = a.length > 2 ? a[2] : "";
                String serviceType = a.length > 3 ? a[3] : "";
                String date = a.length > 4 ? a[4] : "";
                String feedbackText = a.length > 9 ? a[9] : "";

                if (feedbackText == null || feedbackText.trim().isEmpty()) {
                    continue;
                }
                apptMap.put(apptId, new String[]{date, serviceType, techId, feedbackText});
            }
        } catch (IOException ignored) {
        }

        boolean any = false;
        for (Map.Entry<String, String[]> e : apptMap.entrySet()) {
            any = true;
            String apptId = e.getKey();
            String[] info = e.getValue();
            String apptDate = info[0];
            String serviceType = info[1];
            String techId = info[2];
            String feedbackText = info[3];

            JPanel cardWrap = new JPanel(new BorderLayout());
            cardWrap.setOpaque(false);

            JPanel card = new JPanel(new BorderLayout(8, 8));
            card.setBackground(UIConstants.BG_CARD);
            card.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER));

            JPanel accent = new JPanel();
            accent.setBackground(UIConstants.PRIMARY);
            accent.setPreferredSize(new Dimension(8, 80));
            card.add(accent, BorderLayout.WEST);

            JPanel content = new JPanel(new BorderLayout());
            content.setOpaque(false);

            JLabel header = new JLabel(String.format("%s — %s", apptDate, serviceType));
            header.setFont(UIConstants.SUBHEADING_FONT);
            header.setForeground(UIConstants.TEXT_PRIMARY);

            JLabel tech = new JLabel("Technician: " + techId + "   |   Appointment: " + apptId);
            tech.setFont(UIConstants.BODY_FONT);
            tech.setForeground(UIConstants.TEXT_SECONDARY);

            JTextArea ta = new JTextArea(feedbackText);
            ta.setLineWrap(true);
            ta.setWrapStyleWord(true);
            ta.setEditable(false);
            ta.setFont(UIConstants.BODY_FONT);
            ta.setBackground(UIConstants.BG_CARD);

            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            top.add(header, BorderLayout.NORTH);
            top.add(tech, BorderLayout.SOUTH);

            content.add(top, BorderLayout.NORTH);
            content.add(new JScrollPane(ta), BorderLayout.CENTER);

            // No explicit feedback date in current appointments file; show appointment date.
            JLabel dateLbl = new JLabel(apptDate);
            dateLbl.setFont(UIConstants.LABEL_FONT);
            dateLbl.setForeground(UIConstants.TEXT_SECONDARY);
            content.add(dateLbl, BorderLayout.SOUTH);

            card.add(content, BorderLayout.CENTER);
            cardWrap.add(card, BorderLayout.NORTH);

            list.add(cardWrap);
            list.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        if (!any) {
            list.add(centerLabel("No technician feedback yet."));
        }

        add(new JScrollPane(list), BorderLayout.CENTER);
    }

    private JLabel centerLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(UIConstants.BODY_FONT);
        l.setForeground(UIConstants.TEXT_SECONDARY);
        l.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        return l;
    }
}

