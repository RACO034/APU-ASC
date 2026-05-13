/**
 * @author Ian
 * Concept: Inheritance (Superclass) & Encapsulation
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

    
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getGender() { return gender; }
    public String getHomeAddress() { return address; }
    public String getNationality() { return nationality; }
    public int getAge() { return age; }

    
    public void setUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        this.username = username;
    }

    public void setPassword(String password) {
        if (password.length() <= 10 ||
            !password.matches(".*[A-Z].*") ||
            !password.matches(".*[a-z].*") ||
            !password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*") ||
            !password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("Password must be at least 11 characters long, " +
                "contain at least one uppercase letter, one lowercase letter, " +
                "one symbol, and one number.");
        }
        this.password = password;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }

    public void setAge(int age) {
        if (age < 18 || age > 120) {
            throw new IllegalArgumentException("Age must be between 18 and 120");
        }
        this.age = age;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 7) {
            throw new IllegalArgumentException("Invalid Phone Number Length");
        }
        this.phoneNumber = phoneNumber;
    }

    public void setGender(String gender) {
        if (gender == null || gender.isEmpty()) throw new IllegalArgumentException("Gender required");
        this.gender = gender;
    }

    public void setHomeAddress(String address) {
        if (address == null || address.isEmpty()) throw new IllegalArgumentException("Address required");
        this.address = address;
    }

    public void setNationality(String nationality) {
        if (nationality == null || nationality.isEmpty()) throw new IllegalArgumentException("Nationality required");
        this.nationality = nationality;
    }
}