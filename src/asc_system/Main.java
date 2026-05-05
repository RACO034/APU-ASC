package asc_system;

public class Main {
    public static void main(String[] args) {
        Customer me = new Customer();
        me.setCustomerID("C00001");
        me.setUsername("Sibakhe");
        
        // This opens the Dashboard window
        new CustomerDashboard(me).setVisible(true);
    }
}

