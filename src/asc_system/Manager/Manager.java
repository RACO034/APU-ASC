package asc_system.Manager;

/**
 * Manager role (subclass of User).
 */
public class Manager extends User {

    private String managerID;

    public String getManagerID() {
        return managerID;
    }

    public void setManagerID(String managerID) {
        if (managerID == null || !managerID.matches("M\\d{3}")) {
            throw new IllegalArgumentException("Invalid manager ID format (e.g. M001)");
        }
        this.managerID = managerID;
    }

    /** Polymorphism: method overloading for partial profile updates. */
    public void setManagerDetails(String username) {
        setUsername(username);
    }

    public void setManagerDetails(String username, String email) {
        setUsername(username);
        setEmail(email);
    }

    public void setManagerDetails(String username, String email, String phone) {
        setUsername(username);
        setEmail(email);
        setPhoneNumber(phone);
    }

    public void setManagerDetails(String username, String email, String phone,
            String address, String nationality) {
        setUsername(username);
        setEmail(email);
        setPhoneNumber(phone);
        setHomeAddress(address);
        setNationality(nationality);
    }
}
