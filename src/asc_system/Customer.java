package asc_system;

public class Customer extends User {

    private String customerID;
    private String carPlate;
    private String carModel;
    private String dateAdded;

    public String getCustomerID() { return customerID; }
    public String getCarPlate() { return carPlate; }
    public String getCarModel() { return carModel; }
    public String getDateAdded() { return dateAdded; }

    public void setCustomerID(String customerID) {
        if (customerID == null || !customerID.matches("C\\d{5}")) {
            throw new IllegalArgumentException("Invalid ID format (C00001)");
        }
        this.customerID = customerID;
    }

    public void setCarPlate(String carPlate) {
        if (carPlate == null || carPlate.isEmpty()) {
            throw new IllegalArgumentException("Car plate required");
        }
        this.carPlate = carPlate;
    }

    public void setCarModel(String carModel) {
        if (carModel == null || carModel.isEmpty()) {
            throw new IllegalArgumentException("Car model required");
        }
        this.carModel = carModel;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    // Polymorphism (method overloading)
    public void setCustomerDetails(String username) {
        setUsername(username);
    }

    public void setCustomerDetails(String username, String email) {
        setUsername(username);
        setEmail(email);
    }

    public void setCustomerDetails(String username, String email, String phoneNumber) {
        setUsername(username);
        setEmail(email);
        setPhoneNumber(phoneNumber);
    }

    public void setCustomerDetails(String username, String email, String phone, String address, String nationality) {
        setUsername(username);
        setEmail(email);
        setPhoneNumber(phone);
        setHomeAddress(address);
        setNationality(nationality);
    }
}
