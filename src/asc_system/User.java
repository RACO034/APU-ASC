package asc_system;

public class User {
    protected String username;
    protected String phoneNumber;
    protected String email;
    protected String address;
    protected String nationality;
    protected String gender;
    protected int age;

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getHomeAddress() { return address; }
    public String getNationality() { return nationality; }
    public String getGender() { return gender; }
    public int getAge() { return age; }

    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setHomeAddress(String address) { this.address = address; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public void setGender(String gender) { this.gender = gender; }
    public void setAge(int age) { this.age = age; }
}

