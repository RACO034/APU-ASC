package asc_system;

import asc_system.Manager.Manager;
import asc_system.Manager.ManagerRepository;
import asc_system.Technician.Technician;
import asc_system.Technician.TechnicianRepository;
import asc_system.Technician.TechnicianStatus;
import asc_system.Technician.TechnicianTrade;
import java.util.List;

/**
 * Central authentication: routes by user ID prefix (M / CS / T / C).
 */
public final class AuthService {

    public enum Role {
        MANAGER, COUNTER_STAFF, TECHNICIAN, CUSTOMER
    }

    public static final class Session {
        public final Role role;
        public final String userId;
        public final Manager manager;
        public final String[] counterStaffRow;
        public final Technician technician;
        public final Customer customer;

        private Session(Role role, String userId, Manager manager,
                String[] counterStaffRow, Technician technician, Customer customer) {
            this.role = role;
            this.userId = userId;
            this.manager = manager;
            this.counterStaffRow = counterStaffRow;
            this.technician = technician;
            this.customer = customer;
        }

        public static Session manager(Manager m) {
            return new Session(Role.MANAGER, m.getManagerID(), m, null, null, null);
        }

        public static Session counterStaff(String[] row) {
            return new Session(Role.COUNTER_STAFF, row[0], null, row, null, null);
        }

        public static Session technician(Technician t) {
            return new Session(Role.TECHNICIAN, t.getTechnicianId(), null, null, t, null);
        }

        public static Session customer(Customer c) {
            return new Session(Role.CUSTOMER, c.getCustomerID(), null, null, null, c);
        }
    }

    private AuthService() {
    }

    public static Session login(String userId, String password) {
        if (userId == null || password == null) {
            return null;
        }
        String id = userId.trim().toUpperCase();
        String pass = password;

        if (id.matches("M\\d{3}")) {
            Manager m = new ManagerRepository().findByCredentials(id, pass);
            return m != null ? Session.manager(m) : null;
        }
        if (id.matches("CS\\d{3}")) {
            String[] row = findCounterStaff(id, pass);
            return row != null ? Session.counterStaff(row) : null;
        }
        if (id.matches("T\\d{3}")) {
            Technician t = new TechnicianRepository().findByCredentials(id, pass);
            return t != null ? Session.technician(t) : null;
        }
        if (id.matches("C\\d{5}")) {
            Customer c = CustomerAccountService.loadCustomer(id);
            if (c != null && CustomerAccountService.verifyPassword(id, pass)) {
                return Session.customer(c);
            }
        }
        return null;
    }

    private static String[] findCounterStaff(String id, String password) {
        for (String[] row : asc_system.CounterStaff.UserDetailsEdit.getAllUsers()) {
            if (row.length < 4 || !row[0].equalsIgnoreCase(id)) {
                continue;
            }
            // Supports both legacy layout (password at index 3)
            // and replaced layout (password at index 4).
            String pass1 = row.length > 3 ? row[3] : "";
            String pass2 = row.length > 4 ? row[4] : "";
            if (password.equals(pass1) || password.equals(pass2)) {
                return row;
            }
        }
        return null;
    }
}
