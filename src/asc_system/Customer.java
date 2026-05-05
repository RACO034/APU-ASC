package asc_system;

public class Customer extends User {
    private String customerID;

    public String getCustomerID() { return customerID; }
    
    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    // This is the special method for your group project requirements
    public void setCustomerDetails(String user, String mail, String phone, String addr, String nation) {
        this.username = user;
        this.email = mail;
        this.phoneNumber = phone;
        this.address = addr;
        this.nationality = nation;
    }
}