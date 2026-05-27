/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asc_system.PublicClasses;


public enum ServiceType {

    // Major services
    ENGINE_REPAIR        ("Engine Repair",          300, 3),
    TRANSMISSION_REPAIR  ("Transmission Repair",    300, 3),
    ENGINE_DIAGNOSTIC    ("Engine Diagnostic",       300, 3),
    BODYWORK             ("Bodywork",                300, 3),
    AC_OVERHAUL          ("Air Conditioning Overhaul", 300, 3),
    SUSPENSION_OVERHAUL  ("Suspension Overhaul",    300, 3),

    // Minor services
    BRAKE_INSPECTION     ("Brake Inspection",        100, 1),
    OIL_CHANGE           ("Oil Change",              100, 1),
    TYRE_ROTATION        ("Tyre Rotation",           100, 1),
    BATTERY_REPLACEMENT  ("Battery Replacement",     100, 1),
    ELECTRICAL_INSPECTION("Electrical Inspection",   100, 1),
    GENERAL_MAINTENANCE  ("General Maintenance",     100, 1),
    FLUID_TOPUP          ("Fluid Top-Up",            100, 1);

    private final String displayName;
    private final int    price;       // RM
    private final int    duration;    // hours

    ServiceType(String displayName, int price, int duration) {
        this.displayName = displayName;
        this.price       = price;
        this.duration    = duration;
    }

    public String getDisplayName() { return displayName; }
    public int    getPrice()       { return price; }
    public int    getDuration()    { return duration; }

    public boolean isMajor() { return duration == 3; }

    /** Parse display name string from file back to enum */
    public static ServiceType fromDisplayName(String name) {
        for (ServiceType s : values()) {
            if (s.displayName.equalsIgnoreCase(name.trim())) return s;
        }
        return null;
    }
}
