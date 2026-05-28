package asc_system.models;

public class Customer extends User {
    private String town;
    private String registrationDate;

    public String getTown() { return town; }
    public void setTown(String town) { this.town = town; }

    public String getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(String registrationDate) { this.registrationDate = registrationDate; }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s",
                getName() == null ? "" : getName(),
                getId() == null ? "" : getId(),
                town == null ? "" : town);
    }
}

