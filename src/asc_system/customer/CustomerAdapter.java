package asc_system.customer;

import asc_system.CustomerAccountService;
import asc_system.models.Customer;

/**
 * Adapts the existing asc_system.Customer (profile) into the models.Customer
 * used by the updated customer UI package.
 */
public final class CustomerAdapter {

    private CustomerAdapter() {}

    public static Customer toModel(asc_system.Customer c) {
        if (c == null) return null;
        Customer m = new Customer();
        m.setId(c.getCustomerID());
        m.setName(c.getUsername());
        m.setEmail(c.getEmail());
        m.setPhone(c.getPhoneNumber());
        m.setTown(c.getHomeAddress());          // no "town" in original model; reuse address
        m.setRegistrationDate(c.getDateAdded());

        // Load password into the model so "Change Password" works.
        // (This does not change UI; it only enables internal validation.)
        String pwd = CustomerAccountService.getPasswordUnsafe(c.getCustomerID());
        m.setPassword(pwd);
        m.setRole("CUSTOMER");
        return m;
    }
}

