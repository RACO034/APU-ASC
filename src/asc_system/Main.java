package asc_system;

import javax.swing.SwingUtilities;

/**
 * Application entry: universal login for all roles.
 */
public class Main {

    public static void main(String[] args) {
        LoginFrame.applyNimbus();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
