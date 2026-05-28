package asc_system.customer;

import asc_system.LoginFrame;
import asc_system.Session;
import asc_system.UIConstants;
import asc_system.models.Customer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

/**
 * CustomerDashboard — main tabbed window for customers.
 * UI is preserved; logout is wired back to the app's universal LoginFrame.
 */
public class CustomerDashboard extends JFrame {
    private final Customer customer;

    public CustomerDashboard(Customer customer) {
        this.customer = customer;
        initUI();
    }

    private void initUI() {
        setTitle("Customer Dashboard - " + (customer != null ? customer.getName() : "Customer"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 650));
        setSize(1000, 700);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UIConstants.BG_MAIN);
        tabs.setForeground(UIConstants.TEXT_PRIMARY);

        HomePanel home = new HomePanel(Session.getCurrentCustomer());
        ServiceHistoryPanel history = new ServiceHistoryPanel(Session.getCurrentCustomer());
        FeedbackViewPanel feedback = new FeedbackViewPanel(Session.getCurrentCustomer());
        CommentsPanel comments = new CommentsPanel(Session.getCurrentCustomer());
        ProfilePanel profile = new ProfilePanel(customer);

        tabs.addTab("Home", home);
        tabs.addTab("Service & Payment History", history);
        tabs.addTab("Technician Feedback", feedback);
        tabs.addTab("My Comments", comments);
        tabs.addTab("My Profile", profile);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(239, 68, 68));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setOpaque(true);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.addActionListener(e -> {
            Session.clear();
            LoginFrame.returnToLogin(this);
        });
        topBar.add(logoutBtn, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }
}

