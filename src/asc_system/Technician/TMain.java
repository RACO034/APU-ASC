package asc_system.Technician;
import javax.swing.SwingUtilities;
import java.util.List;

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
//            // Use the existing 3-arg constructor and set User fields via setters
//            Technician tech = new Technician(
//                "T001", TechnicianTrade.ENGINE_REPAIR, TechnicianStatus.AVAILABLE);
//            tech.setUsername("xiaolongbao_tech");
//            tech.setEmail("xiaolongbao@apuasc.com");
//            tech.setPhoneNumber("+60123456789");
//            try { tech.setAge(28); } catch (IllegalArgumentException ignored) {}
//            tech.setGender("Male");
//            tech.setNationality("Malaysian");
//            tech.setHomeAddress("Jalan Ampang, KL");

            // load from technician.txt, t001
            TechnicianRepository repo = new TechnicianRepository();
            List<String[]> all = repo.getAllTechnicians();
            String[] row = all.get(1); // change param to get diff technician
            
            Technician tech = new Technician(
                row[0],
                TechnicianTrade.valueOf(row[10]),
                TechnicianStatus.valueOf(row[11])
            );
            
            tech.setUsername(row[1]);
            tech.setEmail(row[3]);
            tech.setPhoneNumber(row[4]);
            try{
                tech.setAge(Integer.parseInt(row[5]));
            }catch(Exception ignored){}
            tech.setGender(row[6]);
            tech.setHomeAddress(row[7]);
            tech.setNationality(row[8]);
            
//            // --- debug, remove after --- (cannot find txt file)
//            java.util.List<Appointment> test = AppointmentDAO.getByTechnicianID("T001");
//            System.out.println("Found: " + test.size());
//            
//            // Add this to see ALL appointments regardless of ID
//            List<Appointment> allApp = AppointmentDAO.getAll();
//            System.out.println("Total lines read: " + allApp.size());
//            for (Appointment a : allApp) {
//                System.out.println("[" + a.getAppointmentID() + "] | [" + a.getTechnicianID() + "]");
//            }
//            // --- end debug ---
            
            new TechnicianDashboard(tech);
        });
    }
}
    

