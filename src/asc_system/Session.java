package asc_system;

import asc_system.models.Customer;

/**
 * Holds the currently logged-in customer for the customer module screens.
 */
public final class Session {
    private static Customer currentCustomer;

    public static void setCurrentCustomer(Customer c) {
        currentCustomer = c;
    }

    public static Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public static void clear() {
        currentCustomer = null;
    }

    private Session() {}
}
