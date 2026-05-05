package asc_system;

public class User {
    protected String username;
    protected String phoneNumber;
    protected String email;
    protected String address;
    protected String nationality;

    // These are the "Getters" the computer is looking for
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getHomeAddress() { return address; }
    public String getNationality() { return nationality; }

    // These are the "Setters" to save information
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setHomeAddress(String address) { this.address = address; }
    public void setNationality(String nationality) { this.nationality = nationality; }
}

