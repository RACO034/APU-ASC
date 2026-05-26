package asc_system.Technician;
import javax.swing.SwingUtilities;

/**
 *
 * @author XL
 */
public class TMain {

     /**
     * FOR STANDALONE TESTING ONLY
     */
    
    public static void main(String[] args) {
//        System.out.println(new java.io.File("data/appointments.txt").getAbsolutePath());

        SwingUtilities.invokeLater(() -> {
            // Use the existing 3-arg constructor and set User fields via setters
            Technician tech = new Technician(
                "TECH-001", TechnicianTrade.ENGINE_REPAIR, TechnicianStatus.AVAILABLE);
            tech.setUsername("xiaolongbao_tech");
            tech.setEmail("xiaolongbao@apuasc.com");
            tech.setPhoneNumber("+60123456789");
            try { tech.setAge(28); } catch (IllegalArgumentException ignored) {}
            tech.setGender("Male");
            tech.setNationality("Malaysian");
            tech.setHomeAddress("Jalan Ampang, KL");
            
            // --- debug, remove after --- (cannot find txt file)
            java.util.List<Appointment> test = AppointmentDAO.getByTechnicianID("TECH-001");
            System.out.println("Found: " + test.size());
            
            // Add this to see ALL appointments regardless of ID
            java.util.List<Appointment> all = AppointmentDAO.getAll();
            System.out.println("Total lines read: " + all.size());
            for (Appointment a : all) {
                System.out.println("[" + a.getAppointmentID() + "] | [" + a.getTechnicianID() + "]");
            }
            // --- end debug ---
            
            new TechnicianDashboard(tech);
        });
    }
}
    

