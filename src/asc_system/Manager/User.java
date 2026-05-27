package asc_system.Manager;

/**
 * Superclass for manager accounts (inheritance & encapsulation).
 */
public class User {
    protected String username;
    protected String password;
    protected String phoneNumber;
    protected String email;
    protected int age;
    protected String gender;
    protected String address;
    protected String nationality;
    protected String dateJoined;

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getGender() { return gender; }
    public String getHomeAddress() { return address; }
    public String getNationality() { return nationality; }
    public String getDateJoined() { return dateJoined; }
    public int getAge() { return age; }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        this.username = username.trim();
    }

    public void setPassword(String password) {
        if (password == null || password.length() < 11
                || !password.matches(".*[A-Z].*")
                || !password.matches(".*[a-z].*")
                || !password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")
                || !password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException(
                    "Password must be at least 11 characters and include upper, lower, number, and symbol.");
        }
        this.password = password;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email.trim();
    }

    public void setAge(int age) {
        if (age < 18 || age > 120) {
            throw new IllegalArgumentException("Age must be between 18 and 120");
        }
        this.age = age;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.replaceAll("\\s", "").length() < 7) {
            throw new IllegalArgumentException("Invalid phone number");
        }
        this.phoneNumber = phoneNumber.trim();
    }

    public void setGender(String gender) {
        if (gender == null || gender.trim().isEmpty()) {
            throw new IllegalArgumentException("Gender required");
        }
        this.gender = gender.trim();
    }

    public void setHomeAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address required");
        }
        this.address = address.trim();
    }

    public void setNationality(String nationality) {
        if (nationality == null || nationality.trim().isEmpty()) {
            throw new IllegalArgumentException("Nationality required");
        }
        this.nationality = nationality.trim();
    }

    public void setDateJoined(String dateJoined) {
        this.dateJoined = dateJoined;
    }

    public void setDateJoinedNow() {
        this.dateJoined = FileHandler.getTimestamp();
    }
}
