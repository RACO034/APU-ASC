package asc_system;

/**
 * Central file paths used by the customer module.
 *
 * Note: this project already uses `Payments.txt` (capital P), so we keep it consistent.
 */
public final class FileConstants {
    public static final String CUSTOMERS = "data/customers.txt";
    public static final String APPOINTMENTS = "data/appointments.txt";
    public static final String PAYMENTS = "data/Payments.txt";
    public static final String FEEDBACK = "data/feedback.txt";
    public static final String COMMENTS = "data/comments.txt";
    public static final String EXPORTS_DIR = "data/exports/";

    private FileConstants() {}
}
