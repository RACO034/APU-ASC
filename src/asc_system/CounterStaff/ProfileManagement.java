package asc_system.CounterStaff;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */



// SELF NOTES 
// ALWAYS CHECK INIT COMPONENTS (TABLE LISTENERS ONLY NEED TO BE INITIALIZED ONCE 
// 

import java.awt.CardLayout;
    import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Ian
 */
public class ProfileManagement extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ProfileManagement.class.getName());
    private CardLayout cardLayout;
    
    public DefaultTableModel appointmentModel;
    private DefaultTableModel technicianModel;
    private DefaultTableModel createAppoinmentModel;
    private DefaultTableModel customerModel;
    private DefaultTableModel paymentModel; 
    

    /**
     * Creates new form ProfileManagement
     */
    public ProfileManagement() {
        initComponents();
        
        CustID.setText(CustomerManagement.generateCustomerID());
        
//        SubmitButton1.addMouseListener(
//    new HoverEffect(SubmitButton1, Color.WHITE, new Color(0,87,184))
//    );
        
    
        // Initializes Tables the first time to receive updated data
    setupAppointmentAssignmentTables();
    loadPaymentAppointmentsTable();
    loadCustomers("");
    setupCustomerTableListener();
    loadAppointments();
    loadUsersTable("CS");
    setupUserTableListener();
    loadTechniciansService();
    setupPaymentTableListener();
    loadPaymentDropdown();
    loadCustomerDropdown();
    loadCSNameHomePage();
    
    

          
    formatTableWithScroll(userTable, jScrollPane1);
formatTableWithScroll(jTableCustomer, jScrollPane11);
formatTableWithScroll(jTable6, jScrollPane6);
formatTableWithScroll(jTable7, jScrollPane7);
formatTableWithScroll(createAppointTable, jScrollPane5);
formatTableWithScroll(PaymentAppointmentTable, jScrollPane2);

    
 
    }
    
    

private CS_AppointmentRepository repo;
private CS_TechnicianRepository techRepo;
private AppointmentService service;
private CS_CustomerRepository customerRepo;


private void setupAppointmentAssignmentTables() {
    
    appointmentModel = (DefaultTableModel) jTable6.getModel();
    technicianModel = (DefaultTableModel) jTable7.getModel();
    customerModel = (DefaultTableModel) jTableCustomer.getModel();
    paymentModel = (DefaultTableModel) PaymentAppointmentTable.getModel();
    createAppoinmentModel = (DefaultTableModel) createAppointTable.getModel();
            
    // Initialize Repos/Services
    repo = new CS_AppointmentRepository();
    techRepo = new CS_TechnicianRepository();
    service = new AppointmentService();
    customerRepo = new CS_CustomerRepository();
    
    // Load initial data
    loadAppointmentsService();
    
    setupTableListeners();
    
    
    
}





private void loadCustomers(String searchID) {

    customerModel.setRowCount(0);

    for (String[] data : customerRepo.getAllCustomers()) {

        // Show all if no search
        if (searchID == null || searchID.trim().isEmpty()) {
            customerModel.addRow(data);
        }
        // Filter by ID (partial or exact)
        else if (data[0].toLowerCase().contains(searchID.toLowerCase())) {
            customerModel.addRow(data);
        }
    }
}

private void loadCustomerDropdown() {
    javax.swing.DefaultComboBoxModel<String> model = new javax.swing.DefaultComboBoxModel<>();
    
    for (String[] data : customerRepo.getAllCustomers()) {
        model.addElement(data[0]);
    }
    
    CustAppoinment.setModel(model);
}

private void loadPaymentDropdown() {
    
    AppointmentCombo.setText(""); 

    PaymentAppointmentTable.getSelectionModel().addListSelectionListener(e -> {
        
        if (!e.getValueIsAdjusting()) {    
            int row = PaymentAppointmentTable.getSelectedRow();
            
            // Only access the table if a row is actually selected
            if (row != -1) {
                try {
                    Object value = PaymentAppointmentTable.getValueAt(row, 1);
                    if (value != null) {
                        AppointmentCombo.setText(value.toString());
                    }
                } catch (Exception ex) {
                    // This catches potential issues if the row index changes 
                    // during a rapid table refresh
                    System.err.println("Selection error: " + ex.getMessage());
                }
            } else {
                // Optional: Clear the text if the user clicks off the table
                AppointmentCombo.setText("");
            }
        }
    });
}

private void loadPaymentAppointmentsTable() {

    paymentModel.setRowCount(0);

    for (String[] appt : repo.getAllAppointments()) {

        String appID = appt[0];
        String customerID = appt[1];
        String serviceType = appt[3];
        String status = appt[7];

        // 1. Fetch the Customer Name
        String customerName = CustomerManagement.getCustomerNameByID(customerID);

        // 2. CHECK for an existing Payment ID
        // We use the helper method we created earlier in PaymentProcessing
        String[] paymentData = PaymentProcessing.getExistingPaymentDetails(appID);
        
        String displayPaymentID = "-"; // Default value
        if (paymentData != null) {
            displayPaymentID = paymentData[0]; // Get actual ID (e.g., "P001")
        }

        // 3. Add to the table model
        paymentModel.addRow(new Object[]{
            displayPaymentID, // Now shows the ID if paid, or "-" if not
            appID,
            customerName,
            serviceType,
            status
        });
    }
}


private void loadUsersTable(String prefix) {

    DefaultTableModel model = (DefaultTableModel) userTable.getModel();
    model.setRowCount(0);

    for (String[] data : UserDetailsEdit.getAllUsers()) {


        String userID = data[0];
        String name = data[1];
        String gender = data[2];
        String password = data[3];
        String phone = data[4];
        String email = data[5];
        String address = data[6];
        String nationality = data[7];

     if (userID.startsWith(prefix)){
        model.addRow(new Object[]{
            userID,
            name,
            gender,
            password,
            phone,
            email,
            address,
            nationality
        });
     }
    }
}


private void loadCSNameHomePage() {
    for (String[] data : UserDetailsEdit.getAllUsers()) {  
       
        String name = data[1];
        
        jTextField94.setText(name);
    }
}


private void clearFields() {
    CustID.setText("");
    CustName.setText("");
    CustGender.setSelectedIndex(-1); 
    CustAge.setText("");
    CustPhoneNum.setText("");
    CustEmail.setText("");
    CustAddress.setText("");
    CustNationality.setText("");
    CustCarPlate.setText("");
    CustCarModel.setText("");
    CustDateCreated.setDate(null);
}

private void setupUserTableListener() {
    userTable.getSelectionModel().addListSelectionListener(e -> {

        if (!e.getValueIsAdjusting()) {

            int row = userTable.getSelectedRow();

            if (row != -1) {

                CS_UserID.setText(userTable.getValueAt(row, 0).toString());
                CS_Name.setText(userTable.getValueAt(row, 1).toString());
                CS_Gender.setSelectedItem(userTable.getValueAt(row, 2).toString());
                CS_Password.setText(userTable.getValueAt(row,3).toString());
                CS_PhoneNum.setText(userTable.getValueAt(row, 4).toString());
                CS_Email.setText(userTable.getValueAt(row, 5).toString());
                CS_Address.setText(userTable.getValueAt(row, 6).toString());
                CS_Nationality.setText(userTable.getValueAt(row, 7).toString());
            }
        }
    });
}






private void setupCustomerTableListener() {
    jTableCustomer.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            int row = jTableCustomer.getSelectedRow();

            if (row != -1) {
                // ROW IS SELECTED: Fill fields with existing data
                CustID.setText(jTableCustomer.getValueAt(row, 0).toString());
                CustName.setText(jTableCustomer.getValueAt(row, 1).toString());
                CustGender.setSelectedItem(jTableCustomer.getValueAt(row, 2).toString());
                CustAge.setText(jTableCustomer.getValueAt(row, 3).toString());
                CustPhoneNum.setText(jTableCustomer.getValueAt(row, 4).toString());
                CustEmail.setText(jTableCustomer.getValueAt(row, 5).toString());
                CustAddress.setText(jTableCustomer.getValueAt(row, 6).toString());
                CustNationality.setText(jTableCustomer.getValueAt(row, 7).toString());
                CustCarPlate.setText(jTableCustomer.getValueAt(row, 8).toString());
                CustCarModel.setText(jTableCustomer.getValueAt(row, 9).toString());

                try {
                    String dateStr = jTableCustomer.getValueAt(row, 10).toString();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    CustDateCreated.setDate(sdf.parse(dateStr));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                // NOTHING IS SELECTED: Reset fields and generate new ID
                CustID.setText(CustomerManagement.generateCustomerID());
                
                // Clear other fields so you don't accidentally edit the wrong person
                CustName.setText("");
                CustAge.setText("");
                CustPhoneNum.setText("");
                CustEmail.setText("");
                CustAddress.setText("");
                CustNationality.setText("");
                CustCarPlate.setText("");
                CustCarModel.setText("");
                CustDateCreated.setDate(new java.util.Date()); // Set to today's date
            }
        }
    });
}




private void loadAppointmentsService() {
    appointmentModel.setRowCount(0);
    for (String[] data : repo.getAllAppointments()) {
        if (data[7].equals("Pending")) {  // Status column
            appointmentModel.addRow(data);
        }
    }
}



private void loadTechniciansService() {
    technicianModel.setRowCount(0);
    
    for (String[] data : techRepo.getAllTechnicians()) {
        String techID = data[0];
        String name = data[1]; // Index 2 is the Name in your file
        
        // Calculate dynamic data
        int workload = service.calculateWorkload(techID);
        String status = service.getTechnicianStatus(techID);
        
        // Since no specific time is provided in this method, 
        // we show general availability status
        String availability = service.getAvailabilityStatus(techID);

        technicianModel.addRow(new Object[]{
            techID,       // Col 0
            name,         // Col 1
            status,       // Col 2
            workload,     // Col 3
            availability  // Col 4
        });
    }
}


private void setupPaymentTableListener() {

    PaymentAppointmentTable.getSelectionModel().addListSelectionListener(e -> {

        if (!e.getValueIsAdjusting()) {

            int row = PaymentAppointmentTable.getSelectedRow();

            if (row != -1) {

                String serviceType = PaymentAppointmentTable.getValueAt(row, 3).toString();
                String customerName = PaymentAppointmentTable.getValueAt(row, 2).toString();
                
                double amount = PaymentProcessing.getServicePrice(serviceType);

                PaymentAmount.setText(String.format("%.2f", amount));
                PaymentCustName.setText(customerName);
            }
        }
    });
}

private void loadAppointments() { 
    createAppoinmentModel.setRowCount(0);

    for (String[] data : repo.getAllAppointments()) {
        
        // Safety check: ensure the row has enough columns to have a status at index 7
        if (data.length < 8) continue; 

        if (data[7].equalsIgnoreCase("Completed")) continue; 

        String customerID = data[1];
        String serviceType = data[3];
        String date = data[4];
        String time = data[5];

        String customerName = CustomerManagement.getCustomerNameByID(customerID);

        createAppoinmentModel.addRow(new Object[]{
            customerID,
            customerName,
            serviceType,
            date,
            time
                
                
        });
    }
}

private void setupTableListeners() {
    // Row Selection (UNCHANGED)
    jTable6.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            int row = jTable6.getSelectedRow();
            if (row != -1) {
                String date = jTable6.getValueAt(row, 4).toString();  // Date column
                String time = jTable6.getValueAt(row, 5).toString();  // Time column  
                String duration = jTable6.getValueAt(row, 6).toString(); // Duration column
                
                loadAvailableTechnicians(date, time, duration);
            }
        }
    });
    
    
}

private void loadAvailableTechnicians(String date, String time, String duration) {

    technicianModel.setRowCount(0); // clear table

    for (String[] tech : techRepo.getAllTechnicians()) {

        String techID = tech[0];
        String name = tech[1];

        boolean isAvailable = service.isTechnicianAvailable(techID, date, time, duration);

        int workload = service.calculateWorkload(techID);
        String status = service.getTechnicianStatus(techID);
        String availability = isAvailable ? "Available" : "Busy"; 

        // OPTION 1: Show ONLY available technicians
        

        
        
            technicianModel.addRow(new Object[]{
                techID,
                name,
                status,
                workload,
                availability
            });
        
    }
}
    


public void formatTableWithScroll(JTable table, JScrollPane scrollPane) {
    Color headerColor = new Color(33, 52, 72);
    
    // 1. Force the ScrollPane corner to match the header color
    // (This prevents a tiny white square in the top-right corner)
    scrollPane.getViewport().setBackground(Color.WHITE);
    scrollPane.setBackground(Color.WHITE);
    scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

    // 2. Create a custom Header Renderer to override the OS style
    DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            setBackground(headerColor);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.GRAY)); // Thin separator
            setHorizontalAlignment(JLabel.CENTER);
            
            return this;
        }
    };

    // 3. Apply the renderer to the header
    table.getTableHeader().setDefaultRenderer(headerRenderer);
    
    // Set height (Note: setPreferredSize is ignored by some Layouts, 
    // so we set it on the Header object specifically)
    table.getTableHeader().setPreferredSize(new Dimension(100, 40));
    
    // 4. Basic Table Body Styling
    table.setRowHeight(30);
    table.setShowVerticalLines(true);
    table.setGridColor(new Color(230, 230, 230));
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ButtonPanel = new javax.swing.JPanel();
        SubmitButton54 = new javax.swing.JButton();
        SubmitButton55 = new javax.swing.JButton();
        MainPanel = new javax.swing.JPanel();
        ContentPanel = new javax.swing.JPanel();
        C_S_CounterStaffMasterPage = new javax.swing.JPanel();
        jTextField93 = new javax.swing.JTextField();
        SubmitButton21 = new javax.swing.JButton();
        SubmitButton22 = new javax.swing.JButton();
        SubmitButton23 = new javax.swing.JButton();
        SubmitButton24 = new javax.swing.JButton();
        SubmitButton25 = new javax.swing.JButton();
        jTextField94 = new javax.swing.JTextField();
        jTextField99 = new javax.swing.JTextField();
        C_S_UserEditPage = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        userTable = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        CS_UserID = new javax.swing.JTextField();
        CS_Password = new javax.swing.JTextField();
        CS_PhoneNum = new javax.swing.JTextField();
        CS_Email = new javax.swing.JTextField();
        SubmitButton28 = new javax.swing.JButton();
        CS_Name = new javax.swing.JTextField();
        CS_Gender = new javax.swing.JComboBox<>();
        jTextField8 = new javax.swing.JTextField();
        CS_Address = new javax.swing.JTextField();
        jTextField11 = new javax.swing.JTextField();
        CS_Nationality = new javax.swing.JTextField();
        jTextField9 = new javax.swing.JTextField();
        SubmitButton29 = new javax.swing.JButton();
        C_S_createNewCustomers = new javax.swing.JPanel();
        jTextField19 = new javax.swing.JTextField();
        jTextField24 = new javax.swing.JTextField();
        jTextField27 = new javax.swing.JTextField();
        jTextField28 = new javax.swing.JTextField();
        jTextField29 = new javax.swing.JTextField();
        jTextField30 = new javax.swing.JTextField();
        CustName = new javax.swing.JTextField();
        CustAge = new javax.swing.JTextField();
        CustPhoneNum = new javax.swing.JTextField();
        SubmitButton12 = new javax.swing.JButton();
        jScrollPane11 = new javax.swing.JScrollPane();
        jTableCustomer = new javax.swing.JTable();
        jTextField95 = new javax.swing.JTextField();
        CustNationality = new javax.swing.JTextField();
        jTextField97 = new javax.swing.JTextField();
        jTextField98 = new javax.swing.JTextField();
        CustEmail = new javax.swing.JTextField();
        CustCarPlate = new javax.swing.JTextField();
        jTextField101 = new javax.swing.JTextField();
        CustCarModel = new javax.swing.JTextField();
        SubmitButton14 = new javax.swing.JButton();
        SubmitButton26 = new javax.swing.JButton();
        jTextField69 = new javax.swing.JTextField();
        CustID = new javax.swing.JTextField();
        jTextField105 = new javax.swing.JTextField();
        CustDateCreated = new com.toedter.calendar.JDateChooser();
        CustGender = new javax.swing.JComboBox<>();
        SubmitButton27 = new javax.swing.JButton();
        custIDSearch = new javax.swing.JTextField();
        jTextField20 = new javax.swing.JTextField();
        CustAddress = new java.awt.TextArea();
        SubmitButton30 = new javax.swing.JButton();
        C_S_viewCustomersList = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTable8 = new javax.swing.JTable();
        jTextField51 = new javax.swing.JTextField();
        jComboBox9 = new javax.swing.JComboBox<>();
        jTextField56 = new javax.swing.JTextField();
        jTextField58 = new javax.swing.JTextField();
        jTextField59 = new javax.swing.JTextField();
        jTextField60 = new javax.swing.JTextField();
        jTextField61 = new javax.swing.JTextField();
        jTextField62 = new javax.swing.JTextField();
        jTextField63 = new javax.swing.JTextField();
        jTextField64 = new javax.swing.JTextField();
        jTextField65 = new javax.swing.JTextField();
        jTextField66 = new javax.swing.JTextField();
        jTextField67 = new javax.swing.JTextField();
        SubmitButton15 = new javax.swing.JButton();
        C_S_AppointmentManagementPage = new javax.swing.JPanel();
        jTextField92 = new javax.swing.JTextField();
        SubmitButton18 = new javax.swing.JButton();
        SubmitButton19 = new javax.swing.JButton();
        SubmitButton20 = new javax.swing.JButton();
        C_S_createAppointments = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        createAppointTable = new javax.swing.JTable();
        jTextField45 = new javax.swing.JTextField();
        CustAppoinment = new javax.swing.JComboBox<>();
        jTextField46 = new javax.swing.JTextField();
        jTextField47 = new javax.swing.JTextField();
        jTextField48 = new javax.swing.JTextField();
        jTextField49 = new javax.swing.JTextField();
        jTextField50 = new javax.swing.JTextField();
        CreateCustName = new javax.swing.JTextField();
        TimeField = new javax.swing.JTextField();
        SubmitButton13 = new javax.swing.JButton();
        ServiceDropdown = new javax.swing.JComboBox<>();
        datePicker = new com.toedter.calendar.JDateChooser();
        jTextField10 = new javax.swing.JTextField();
        SubmitButton31 = new javax.swing.JButton();
        C_S_assignAppointments = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable6 = new javax.swing.JTable();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable7 = new javax.swing.JTable();
        jTextField57 = new javax.swing.JTextField();
        assignButton = new javax.swing.JButton();
        jTextField23 = new javax.swing.JTextField();
        jTextField25 = new javax.swing.JTextField();
        SubmitButton32 = new javax.swing.JButton();
        C_S_PaymentProcessing = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        PaymentAppointmentTable = new javax.swing.JTable();
        jTextField13 = new javax.swing.JTextField();
        jTextField14 = new javax.swing.JTextField();
        jTextField15 = new javax.swing.JTextField();
        jTextField16 = new javax.swing.JTextField();
        jTextField17 = new javax.swing.JTextField();
        PaymentCustName = new javax.swing.JTextField();
        PaymentAmount = new javax.swing.JTextField();
        SubmitButton2 = new javax.swing.JButton();
        PaymentMethod = new javax.swing.JComboBox<>();
        SubmitButton17 = new javax.swing.JButton();
        jTextField31 = new javax.swing.JTextField();
        AppointmentCombo = new javax.swing.JTextField();
        SubmitButton33 = new javax.swing.JButton();
        C_S_ReceiptGeneration = new javax.swing.JPanel();
        SubmitButton4 = new javax.swing.JButton();
        jScrollPane10 = new javax.swing.JScrollPane();
        ReceiptArea = new javax.swing.JTextArea();
        jTextField96 = new javax.swing.JTextField();
        receiptToTxt = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        SubmitButton54.setBackground(new java.awt.Color(0, 87, 184));
        SubmitButton54.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        SubmitButton54.setForeground(new java.awt.Color(255, 255, 255));
        SubmitButton54.setText("Edit Current Users Details");
        SubmitButton54.addActionListener(this::SubmitButton54ActionPerformed);

        SubmitButton55.setBackground(new java.awt.Color(0, 87, 184));
        SubmitButton55.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        SubmitButton55.setForeground(new java.awt.Color(255, 255, 255));
        SubmitButton55.setText("Edit Current Users Details");
        SubmitButton55.addActionListener(this::SubmitButton55ActionPerformed);

        javax.swing.GroupLayout ButtonPanelLayout = new javax.swing.GroupLayout(ButtonPanel);
        ButtonPanel.setLayout(ButtonPanelLayout);
        ButtonPanelLayout.setHorizontalGroup(
            ButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ButtonPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(SubmitButton55)
                .addContainerGap())
            .addGroup(ButtonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(SubmitButton54)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ButtonPanelLayout.setVerticalGroup(
            ButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ButtonPanelLayout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addComponent(SubmitButton55)
                .addGap(48, 48, 48)
                .addComponent(SubmitButton54)
                .addContainerGap(910, Short.MAX_VALUE))
        );

        getContentPane().add(ButtonPanel, java.awt.BorderLayout.WEST);

        ContentPanel.setLayout(new java.awt.CardLayout());

        C_S_CounterStaffMasterPage.setBackground(new java.awt.Color(253, 253, 253));
        C_S_CounterStaffMasterPage.setPreferredSize(new java.awt.Dimension(1280, 680));

        jTextField93.setEditable(false);
        jTextField93.setBackground(new java.awt.Color(255, 255, 255));
        jTextField93.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jTextField93.setForeground(new java.awt.Color(0, 0, 0));
        jTextField93.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField93.setText("COUNTER STAFF HOMEPAGE");
        jTextField93.setBorder(null);
        jTextField93.addActionListener(this::jTextField93ActionPerformed);

        SubmitButton21.setBackground(new java.awt.Color(0, 87, 184));
        SubmitButton21.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SubmitButton21.setForeground(new java.awt.Color(255, 255, 255));
        SubmitButton21.setText("Return");
        SubmitButton21.addActionListener(this::SubmitButton21ActionPerformed);

        SubmitButton22.setBackground(new java.awt.Color(0, 87, 184));
        SubmitButton22.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        SubmitButton22.setForeground(new java.awt.Color(255, 255, 255));
        SubmitButton22.setText("Customer Management");
        SubmitButton22.addActionListener(this::SubmitButton22ActionPerformed);

        SubmitButton23.setBackground(new java.awt.Color(0, 87, 184));
        SubmitButton23.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        SubmitButton23.setForeground(new java.awt.Color(255, 255, 255));
        SubmitButton23.setText("Manage Appointments");
        SubmitButton23.addActionListener(this::SubmitButton23ActionPerformed);

        SubmitButton24.setBackground(new java.awt.Color(0, 87, 184));
        SubmitButton24.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        SubmitButton24.setForeground(new java.awt.Color(255, 255, 255));
        SubmitButton24.setText("Edit Current Users Details");
        SubmitButton24.addActionListener(this::SubmitButton24ActionPerformed);

        SubmitButton25.setBackground(new java.awt.Color(0, 87, 184));
        SubmitButton25.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        SubmitButton25.setForeground(new java.awt.Color(255, 255, 255));
        SubmitButton25.setText("Process Payments");
        SubmitButton25.addActionListener(this::SubmitButton25ActionPerformed);

        jTextField94.setEditable(false);
        jTextField94.setBackground(new java.awt.Color(255, 255, 255));
        jTextField94.setFont(new java.awt.Font("Segoe UI", 3, 16)); // NOI18N
        jTextField94.setForeground(new java.awt.Color(0, 0, 0));
        jTextField94.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField94.setText("<<insertCSname>>");
        jTextField94.setBorder(null);
        jTextField94.addActionListener(this::jTextField94ActionPerformed);

        jTextField99.setEditable(false);
        jTextField99.setBackground(new java.awt.Color(255, 255, 255));
        jTextField99.setFont(new java.awt.Font("Segoe UI", 2, 16)); // NOI18N
        jTextField99.setForeground(new java.awt.Color(0, 0, 0));
        jTextField99.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField99.setText("HELLO");
        jTextField99.setBorder(null);
        jTextField99.addActionListener(this::jTextField99ActionPerformed);

        javax.swing.GroupLayout C_S_CounterStaffMasterPageLayout = new javax.swing.GroupLayout(C_S_CounterStaffMasterPage);
        C_S_CounterStaffMasterPage.setLayout(C_S_CounterStaffMasterPageLayout);
        C_S_CounterStaffMasterPageLayout.setHorizontalGroup(
            C_S_CounterStaffMasterPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(C_S_CounterStaffMasterPageLayout.createSequentialGroup()
                .addGroup(C_S_CounterStaffMasterPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(C_S_CounterStaffMasterPageLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(SubmitButton21, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(C_S_CounterStaffMasterPageLayout.createSequentialGroup()
                        .addGap(335, 335, 335)
                        .addGroup(C_S_CounterStaffMasterPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(SubmitButton24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(SubmitButton23, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(49, 49, 49)
                        .addGroup(C_S_CounterStaffMasterPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(SubmitButton22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(SubmitButton25, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(C_S_CounterStaffMasterPageLayout.createSequentialGroup()
                        .addGap(534, 534, 534)
                        .addComponent(jTextField99, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField94, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(C_S_CounterStaffMasterPageLayout.createSequentialGroup()
                        .addGap(366, 366, 366)
                        .addComponent(jTextField93, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(1433, Short.MAX_VALUE))
        );
        C_S_CounterStaffMasterPageLayout.setVerticalGroup(
            C_S_CounterStaffMasterPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(C_S_CounterStaffMasterPageLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jTextField93, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(C_S_CounterStaffMasterPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField94, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField99, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(116, 116, 116)
                .addGroup(C_S_CounterStaffMasterPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(SubmitButton24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(SubmitButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(184, 184, 184)
                .addGroup(C_S_CounterStaffMasterPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(SubmitButton25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(SubmitButton23, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 523, Short.MAX_VALUE)
                .addComponent(SubmitButton21)
                .addContainerGap())
        );

        ContentPanel.add(C_S_CounterStaffMasterPage, "CounterStaffMasterPage");

        C_S_UserEditPage.setBackground(new java.awt.Color(253, 253, 253));

        jScrollPane1.setBackground(new java.awt.Color(33, 52, 72));
        jScrollPane1.setForeground(new java.awt.Color(221, 221, 221));

        userTable.setBackground(new java.awt.Color(204, 204, 204));
        userTable.setForeground(new java.awt.Color(102, 102, 102));
        userTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, "", null, null},
                {null, null, null, null, null, "", null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "UserID", "Name", "Role", "Password", "Phone", "Email", "Home Address", "Nationality"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        userTable.setGridColor(new java.awt.Color(255, 255, 255));
        userTable.setSelectionForeground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(userTable);

        jTextField1.setBackground(new java.awt.Color(255, 255, 255));
        jTextField1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jTextField1.setForeground(new java.awt.Color(0, 0, 0));
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField1.setText("Personal Details");
        jTextField1.setBorder(null);
        jTextField1.addActionListener(this::jTextField1ActionPerformed);

        jTextField2.setBackground(new java.awt.Color(102, 102, 102));
        jTextField2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField2.setForeground(new java.awt.Color(255, 249, 234));
        jTextField2.setText("UserID");
        jTextField2.addActionListener(this::jTextField2ActionPerformed);

        jTextField3.setBackground(new java.awt.Color(102, 102, 102));
        jTextField3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField3.setForeground(new java.awt.Color(255, 249, 234));
        jTextField3.setText("Gender");
        jTextField3.addActionListener(this::jTextField3ActionPerformed);

        jTextField4.setBackground(new java.awt.Color(102, 102, 102));
        jTextField4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField4.setForeground(new java.awt.Color(255, 249, 234));
        jTextField4.setText("Update Password");
        jTextField4.addActionListener(this::jTextField4ActionPerformed);

        jTextField5.setEditable(false);
        jTextField5.setBackground(new java.awt.Color(102, 102, 102));
        jTextField5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField5.setForeground(new java.awt.Color(255, 249, 234));
        jTextField5.setText("Update Name");
        jTextField5.addActionListener(this::jTextField5ActionPerformed);

        jTextField6.setBackground(new java.awt.Color(102, 102, 102));
        jTextField6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField6.setForeground(new java.awt.Color(255, 249, 234));
        jTextField6.setText("Update Phone");
        jTextField6.addActionListener(this::jTextField6ActionPerformed);

        jTextField7.setBackground(new java.awt.Color(102, 102, 102));
        jTextField7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField7.setForeground(new java.awt.Color(255, 249, 234));
        jTextField7.setText("Update Email");
        jTextField7.addActionListener(this::jTextField7ActionPerformed);

        CS_UserID.setEditable(false);

        CS_Password.addActionListener(this::CS_PasswordActionPerformed);

        SubmitButton28.setBackground(new java.awt.Color(0, 87, 184));
        SubmitButton28.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SubmitButton28.setForeground(new java.awt.Color(255, 255, 255));
        SubmitButton28.setText("Update");
        SubmitButton28.addActionListener(this::SubmitButton28ActionPerformed);

        CS_Name.addActionListener(this::CS_NameActionPerformed);

        CS_Gender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female" }));
        CS_Gender.addActionListener(this::CS_GenderActionPerformed);

        jTextField8.setBackground(new java.awt.Color(102, 102, 102));
        jTextField8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField8.setForeground(new java.awt.Color(255, 249, 234));
        jTextField8.setText("Update Home Address");
        jTextField8.addActionListener(this::jTextField8ActionPerformed);

        jTextField11.setBackground(new java.awt.Color(102, 102, 102));
        jTextField11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField11.setForeground(new java.awt.Color(255, 249, 234));
        jTextField11.setText("Update Nationality");
        jTextField11.addActionListener(this::jTextField11ActionPerformed);

        jTextField9.setBackground(new java.awt.Color(255, 255, 255));
        jTextField9.setFont(new java.awt.Font("Malgun Gothic", 1, 36)); // NOI18N
        jTextField9.setForeground(new java.awt.Color(33, 52, 72));
        jTextField9.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField9.setText("Edit Profile");
        jTextField9.setBorder(null);
        jTextField9.addActionListener(this::jTextField9ActionPerformed);

        SubmitButton29.setBackground(new java.awt.Color(0, 87, 184));
        SubmitButton29.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SubmitButton29.setForeground(new java.awt.Color(255, 255, 255));
        SubmitButton29.setText("Return");
        SubmitButton29.addActionListener(this::SubmitButton29ActionPerformed);

        javax.swing.GroupLayout C_S_UserEditPageLayout = new javax.swing.GroupLayout(C_S_UserEditPage);
        C_S_UserEditPage.setLayout(C_S_UserEditPageLayout);
        C_S_UserEditPageLayout.setHorizontalGroup(
            C_S_UserEditPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(C_S_UserEditPageLayout.createSequentialGroup()
                .addGroup(C_S_UserEditPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(C_S_UserEditPageLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(585, 585, 585)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(C_S_UserEditPageLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 914, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(C_S_UserEditPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(C_S_UserEditPageLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(C_S_UserEditPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(30, 30, 30)
                                .addGroup(C_S_UserEditPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(CS_Address, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(CS_UserID, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                                    .addComponent(CS_Password, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                                    .addComponent(CS_PhoneNum, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                                    .addComponent(CS_Email, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                                    .addComponent(CS_Name, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                                    .addComponent(CS_Gender, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(CS_Nationality)))
                            .addGroup(C_S_UserEditPageLayout.createSequentialGroup()
                                .addGap(107, 107, 107)
                                .addComponent(SubmitButton28, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(C_S_UserEditPageLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(SubmitButton29, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(1027, Short.MAX_VALUE))
        );
        C_S_UserEditPageLayout.setVerticalGroup(
            C_S_UserEditPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(C_S_UserEditPageLayout.createSequentialGroup()
                .addGroup(C_S_UserEditPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(C_S_UserEditPageLayout.createSequentialGroup()
                        .addGap(67, 67, 67)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(C_S_UserEditPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CS_UserID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(C_S_UserEditPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CS_Name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(C_S_UserEditPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CS_Gender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(C_S_UserEditPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CS_Password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(C_S_UserEditPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(CS_PhoneNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(C_S_UserEditPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(CS_Email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(19, 19, 19)
                        .addGroup(C_S_UserEditPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CS_Address, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(19, 19, 19)
                        .addGroup(C_S_UserEditPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CS_Nationality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(SubmitButton28, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(C_S_UserEditPageLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(171, 171, 171)
                .addComponent(SubmitButton29)
                .addContainerGap(382, Short.MAX_VALUE))
        );

        ContentPanel.add(C_S_UserEditPage, "userEditPage");

        C_S_createNewCustomers.setBackground(new java.awt.Color(253, 253, 253));

        jTextField19.setEditable(false);
        jTextField19.setBackground(new java.awt.Color(255, 255, 255));
        jTextField19.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jTextField19.setForeground(new java.awt.Color(0, 0, 0));
        jTextField19.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField19.setText("Please Fill In All Fields");
        jTextField19.setBorder(null);
        jTextField19.addActionListener(this::jTextField19ActionPerformed);

        jTextField24.setBackground(new java.awt.Color(102, 102, 102));
        jTextField24.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField24.setForeground(new java.awt.Color(255, 249, 234));
        jTextField24.setText("Select Gender");
        jTextField24.addActionListener(this::jTextField24ActionPerformed);

        jTextField27.setBackground(new java.awt.Color(102, 102, 102));
        jTextField27.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField27.setForeground(new java.awt.Color(255, 249, 234));
        jTextField27.setText("Enter Age");
        jTextField27.setPreferredSize(null);
        jTextField27.addActionListener(this::jTextField27ActionPerformed);

        jTextField28.setEditable(false);
        jTextField28.setBackground(new java.awt.Color(102, 102, 102));
        jTextField28.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField28.setForeground(new java.awt.Color(255, 249, 234));
        jTextField28.setText("Enter Name");
        jTextField28.setPreferredSize(null);
        jTextField28.addActionListener(this::jTextField28ActionPerformed);

        jTextField29.setBackground(new java.awt.Color(102, 102, 102));
        jTextField29.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField29.setForeground(new java.awt.Color(255, 249, 234));
        jTextField29.setText("Enter Phone Number");
        jTextField29.addActionListener(this::jTextField29ActionPerformed);

        jTextField30.setBackground(new java.awt.Color(102, 102, 102));
        jTextField30.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField30.setForeground(new java.awt.Color(255, 249, 234));
        jTextField30.setText("Enter Home Address");
        jTextField30.addActionListener(this::jTextField30ActionPerformed);

        CustAge.addActionListener(this::CustAgeActionPerformed);

        SubmitButton12.setBackground(new java.awt.Color(0, 87, 184));
        SubmitButton12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        SubmitButton12.setForeground(new java.awt.Color(255, 255, 255));
        SubmitButton12.setText("Update");
        SubmitButton12.addActionListener(this::SubmitButton12ActionPerformed);

        jScrollPane11.setForeground(new java.awt.Color(221, 221, 221));

        jTableCustomer.setBackground(new java.awt.Color(204, 204, 204));
        jTableCustomer.setForeground(new java.awt.Color(102, 102, 102));
        jTableCustomer.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, "", null, null, null, null, null},
                {null, null, null, null, null, "", null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "CustomerID", "Name", "Gender", "Age", "Phone", "Email", "Home Address", "Nationality", "Car Plate Number", "Car Model", "Date Added"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTableCustomer.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTableCustomer.setGridColor(new java.awt.Color(255, 255, 255));
        jScrollPane11.setViewportView(jTableCustomer);

        jTextField95.setBackground(new java.awt.Color(102, 102, 102));
        jTextField95.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField95.setForeground(new java.awt.Color(255, 249, 234));
        jTextField95.setText("Enter Nationality");
        jTextField95.addActionListener(this::jTextField95ActionPerformed);

        jTextField97.setBackground(new java.awt.Color(102, 102, 102));
        jTextField97.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField97.setForeground(new java.awt.Color(255, 249, 234));
        jTextField97.setText("Enter Car Plate Number");
        jTextField97.addActionListener(this::jTextField97ActionPerformed);

        jTextField98.setBackground(new java.awt.Color(102, 102, 102));
        jTextField98.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField98.setForeground(new java.awt.Color(255, 249, 234));
        jTextField98.setText("Enter Email");
        jTextField98.addActionListener(this::jTextField98ActionPerformed);

        CustEmail.addActionListener(this::CustEmailActionPerformed);

        jTextField101.setBackground(new java.awt.Color(102, 102, 102));
        jTextField101.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField101.setForeground(new java.awt.Color(255, 249, 234));
        jTextField101.setText("Enter Car Model");
        jTextField101.addActionListener(this::jTextField101ActionPerformed);

        SubmitButton14.setBackground(new java.awt.Color(0, 87, 184));
        SubmitButton14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        SubmitButton14.setForeground(new java.awt.Color(255, 255, 255));
        SubmitButton14.setText("Add ");
        SubmitButton14.addActionListener(this::SubmitButton14ActionPerformed);

        SubmitButton26.setBackground(new java.awt.Color(0, 87, 184));
        SubmitButton26.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        SubmitButton26.setForeground(new java.awt.Color(255, 255, 255));
        SubmitButton26.setText("Delete");
        SubmitButton26.addActionListener(this::SubmitButton26ActionPerformed);

        jTextField69.setEditable(false);
        jTextField69.setBackground(new java.awt.Color(102, 102, 102));
        jTextField69.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField69.setForeground(new java.awt.Color(255, 249, 234));
        jTextField69.setText("CustomerID");
        jTextField69.setPreferredSize(null);
        jTextField69.addActionListener(this::jTextField69ActionPerformed);

        CustID.setEditable(false);
        CustID.addActionListener(this::CustIDActionPerformed);

        jTextField105.setBackground(new java.awt.Color(102, 102, 102));
        jTextField105.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField105.setForeground(new java.awt.Color(255, 249, 234));
        jTextField105.setText("Select Date");
        jTextField105.addActionListener(this::jTextField105ActionPerformed);

        CustDateCreated.setDateFormatString("yyyy-MM-dd");

        CustGender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female" }));
        CustGender.addActionListener(this::CustGenderActionPerformed);

        SubmitButton27.setBackground(new java.awt.Color(0, 87, 184));
        SubmitButton27.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SubmitButton27.setForeground(new java.awt.Color(255, 255, 255));
        SubmitButton27.setText("Return");
        SubmitButton27.addActionListener(this::SubmitButton27ActionPerformed);

        custIDSearch.addActionListener(this::custIDSearchActionPerformed);

        jTextField20.setBackground(new java.awt.Color(255, 255, 255));
        jTextField20.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jTextField20.setForeground(new java.awt.Color(0, 0, 0));
        jTextField20.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField20.setText("Search By CustomerID");
        jTextField20.setBorder(null);
        jTextField20.addActionListener(this::jTextField20ActionPerformed);

        SubmitButton30.setBackground(new java.awt.Color(0, 87, 184));
        SubmitButton30.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SubmitButton30.setForeground(new java.awt.Color(255, 255, 255));
        SubmitButton30.setText("Return");
        SubmitButton30.addActionListener(this::SubmitButton30ActionPerformed);

        javax.swing.GroupLayout C_S_createNewCustomersLayout = new javax.swing.GroupLayout(C_S_createNewCustomers);
        C_S_createNewCustomers.setLayout(C_S_createNewCustomersLayout);
        C_S_createNewCustomersLayout.setHorizontalGroup(
            C_S_createNewCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(C_S_createNewCustomersLayout.createSequentialGroup()
                .addGroup(C_S_createNewCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(C_S_createNewCustomersLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(SubmitButton27, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(C_S_createNewCustomersLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(C_S_createNewCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(C_S_createNewCustomersLayout.createSequentialGroup()
                                .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(custIDSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(76, 76, 76))
                            .addGroup(C_S_createNewCustomersLayout.createSequentialGroup()
                                .addGroup(C_S_createNewCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(C_S_createNewCustomersLayout.createSequentialGroup()
                                        .addComponent(SubmitButton26, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(47, 47, 47)
                                        .addComponent(SubmitButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(C_S_createNewCustomersLayout.createSequentialGroup()
                                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 876, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(C_S_createNewCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(C_S_createNewCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, C_S_createNewCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jTextField28, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jTextField24, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jTextField27, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jTextField29, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jTextField30, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jTextField95, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jTextField98, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addComponent(jTextField97, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(jTextField101, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextField105, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGroup(C_S_createNewCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(C_S_createNewCustomersLayout.createSequentialGroup()
                                        .addGap(30, 30, 30)
                                        .addGroup(C_S_createNewCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(CustCarPlate)
                                            .addComponent(CustName)
                                            .addComponent(CustAge)
                                            .addComponent(CustPhoneNum)
                                            .addComponent(CustEmail)
                                            .addComponent(CustNationality)
                                            .addComponent(CustCarModel)
                                            .addComponent(CustDateCreated, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(CustGender, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGroup(C_S_createNewCustomersLayout.createSequentialGroup()
                                                .addComponent(CustAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE))))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, C_S_createNewCustomersLayout.createSequentialGroup()
                                        .addGap(48, 48, 48)
                                        .addComponent(SubmitButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(16, 16, 16))))
                            .addGroup(C_S_createNewCustomersLayout.createSequentialGroup()
                                .addComponent(SubmitButton30, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(C_S_createNewCustomersLayout.createSequentialGroup()
                        .addGap(894, 894, 894)
                        .addComponent(jTextField69, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(CustID)))
                .addGap(1010, 1010, 1010))
        );
        C_S_createNewCustomersLayout.setVerticalGroup(
            C_S_createNewCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(C_S_createNewCustomersLayout.createSequentialGroup()
                .addGroup(C_S_createNewCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(C_S_createNewCustomersLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(C_S_createNewCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(custIDSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(C_S_createNewCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(C_S_createNewCustomersLayout.createSequentialGroup()
                                .addGroup(C_S_createNewCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(CustID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField69, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(C_S_createNewCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(CustName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(C_S_createNewCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(CustGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(C_S_createNewCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(CustAge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(C_S_createNewCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(CustPhoneNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(C_S_createNewCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField98, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(CustEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(C_S_createNewCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(C_S_createNewCustomersLayout.createSequentialGroup()
                                        .addComponent(jTextField30, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(CustAddress, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(C_S_createNewCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField95, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(CustNationality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(C_S_createNewCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField97, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(CustCarPlate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(16, 16, 16)
                                .addGroup(C_S_createNewCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField101, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(CustCarModel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(C_S_createNewCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField105, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(CustDateCreated, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jScrollPane11))
                        .addGap(85, 85, 85))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, C_S_createNewCustomersLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(C_S_createNewCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(SubmitButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SubmitButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SubmitButton26, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(15, 15, 15)))
                .addComponent(SubmitButton30)
                .addGap(339, 339, 339)
                .addComponent(SubmitButton27)
                .addContainerGap())
        );

        ContentPanel.add(C_S_createNewCustomers, "CustomerManagement");

        C_S_viewCustomersList.setBackground(new java.awt.Color(253, 253, 253));

        jScrollPane8.setForeground(new java.awt.Color(221, 221, 221));

        jTable8.setBackground(new java.awt.Color(204, 204, 204));
        jTable8.setForeground(new java.awt.Color(221, 221, 221));
        jTable8.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, "", null, null, null, null},
                {null, null, null, null, null, "", null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "UserID", "Name", "Gender", "Age", "Phone", "Email", "Home Address", "Nationality", "Car Plate Number", "Car Model"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable8.setGridColor(new java.awt.Color(255, 255, 255));
        jScrollPane8.setViewportView(jTable8);

        jTextField51.setBackground(new java.awt.Color(255, 255, 255));
        jTextField51.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jTextField51.setForeground(new java.awt.Color(0, 0, 0));
        jTextField51.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField51.setText("Please Fill In All Fields");
        jTextField51.addActionListener(this::jTextField51ActionPerformed);

        jComboBox9.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jTextField56.setBackground(new java.awt.Color(102, 102, 102));
        jTextField56.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField56.setForeground(new java.awt.Color(255, 249, 234));
        jTextField56.setText("Enter UserID");
        jTextField56.addActionListener(this::jTextField56ActionPerformed);

        jTextField58.setBackground(new java.awt.Color(102, 102, 102));
        jTextField58.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField58.setForeground(new java.awt.Color(255, 249, 234));
        jTextField58.setText("Gender");
        jTextField58.addActionListener(this::jTextField58ActionPerformed);

        jTextField59.setBackground(new java.awt.Color(102, 102, 102));
        jTextField59.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField59.setForeground(new java.awt.Color(255, 249, 234));
        jTextField59.setText("Update Password");
        jTextField59.addActionListener(this::jTextField59ActionPerformed);

        jTextField60.setEditable(false);
        jTextField60.setBackground(new java.awt.Color(102, 102, 102));
        jTextField60.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField60.setForeground(new java.awt.Color(255, 249, 234));
        jTextField60.setText("Current Name");
        jTextField60.addActionListener(this::jTextField60ActionPerformed);

        jTextField61.setBackground(new java.awt.Color(102, 102, 102));
        jTextField61.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField61.setForeground(new java.awt.Color(255, 249, 234));
        jTextField61.setText("Update Phone");
        jTextField61.addActionListener(this::jTextField61ActionPerformed);

        jTextField62.setBackground(new java.awt.Color(102, 102, 102));
        jTextField62.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField62.setForeground(new java.awt.Color(255, 249, 234));
        jTextField62.setText("Update Email");
        jTextField62.addActionListener(this::jTextField62ActionPerformed);

        jTextField63.setText("jTextField8");

        jTextField64.setText("jTextField8");
        jTextField64.addActionListener(this::jTextField64ActionPerformed);

        jTextField65.setText("jTextField8");

        jTextField66.setText("<rolePlaceholder>");

        jTextField67.setText("jTextField8");

        SubmitButton15.setBackground(new java.awt.Color(0, 87, 184));
        SubmitButton15.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SubmitButton15.setForeground(new java.awt.Color(255, 255, 255));
        SubmitButton15.setText("Confirm Changes");
        SubmitButton15.addActionListener(this::SubmitButton15ActionPerformed);

        javax.swing.GroupLayout C_S_viewCustomersListLayout = new javax.swing.GroupLayout(C_S_viewCustomersList);
        C_S_viewCustomersList.setLayout(C_S_viewCustomersListLayout);
        C_S_viewCustomersListLayout.setHorizontalGroup(
            C_S_viewCustomersListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(C_S_viewCustomersListLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 1924, Short.MAX_VALUE)
                .addGroup(C_S_viewCustomersListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(C_S_viewCustomersListLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(C_S_viewCustomersListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, C_S_viewCustomersListLayout.createSequentialGroup()
                                .addGroup(C_S_viewCustomersListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField60, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField56, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField58, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField59, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField61, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField62, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(30, 30, 30)
                                .addGroup(C_S_viewCustomersListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField63, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField64, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField65, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField66, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField67, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(17, 17, 17))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, C_S_viewCustomersListLayout.createSequentialGroup()
                                .addComponent(jTextField51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(128, 128, 128))))
                    .addGroup(C_S_viewCustomersListLayout.createSequentialGroup()
                        .addGap(105, 105, 105)
                        .addComponent(SubmitButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        C_S_viewCustomersListLayout.setVerticalGroup(
            C_S_viewCustomersListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(C_S_viewCustomersListLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(C_S_viewCustomersListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(C_S_viewCustomersListLayout.createSequentialGroup()
                        .addComponent(jTextField51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(C_S_viewCustomersListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(C_S_viewCustomersListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField63, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(C_S_viewCustomersListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField58, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField66, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(C_S_viewCustomersListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField59, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField64, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(C_S_viewCustomersListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField65, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(C_S_viewCustomersListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField67, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField62, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(SubmitButton15)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 1087, Short.MAX_VALUE))
                .addGap(15, 15, 15))
        );

        ContentPanel.add(C_S_viewCustomersList, "card6");

        C_S_AppointmentManagementPage.setBackground(new java.awt.Color(253, 253, 253));

        jTextField92.setBackground(new java.awt.Color(255, 255, 255));
        jTextField92.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jTextField92.setForeground(new java.awt.Color(0, 0, 0));
        jTextField92.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField92.setText("APPOINTMENT MANAGEMENT ");
        jTextField92.setBorder(null);
        jTextField92.addActionListener(this::jTextField92ActionPerformed);

        SubmitButton18.setBackground(new java.awt.Color(0, 87, 184));
        SubmitButton18.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SubmitButton18.setForeground(new java.awt.Color(255, 255, 255));
        SubmitButton18.setText("Return");
        SubmitButton18.addActionListener(this::SubmitButton18ActionPerformed);

        SubmitButton19.setBackground(new java.awt.Color(0, 87, 184));
        SubmitButton19.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        SubmitButton19.setForeground(new java.awt.Color(255, 255, 255));
        SubmitButton19.setText("Assign Techinicians to Appointments");
        SubmitButton19.addActionListener(this::SubmitButton19ActionPerformed);

        SubmitButton20.setBackground(new java.awt.Color(0, 87, 184));
        SubmitButton20.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        SubmitButton20.setForeground(new java.awt.Color(255, 255, 255));
        SubmitButton20.setText("Create New Appointments");
        SubmitButton20.addActionListener(this::SubmitButton20ActionPerformed);

        javax.swing.GroupLayout C_S_AppointmentManagementPageLayout = new javax.swing.GroupLayout(C_S_AppointmentManagementPage);
        C_S_AppointmentManagementPage.setLayout(C_S_AppointmentManagementPageLayout);
        C_S_AppointmentManagementPageLayout.setHorizontalGroup(
            C_S_AppointmentManagementPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(C_S_AppointmentManagementPageLayout.createSequentialGroup()
                .addGroup(C_S_AppointmentManagementPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(C_S_AppointmentManagementPageLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(SubmitButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(C_S_AppointmentManagementPageLayout.createSequentialGroup()
                        .addGap(366, 366, 366)
                        .addComponent(jTextField92, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(C_S_AppointmentManagementPageLayout.createSequentialGroup()
                        .addGap(457, 457, 457)
                        .addGroup(C_S_AppointmentManagementPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(SubmitButton20, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                            .addComponent(SubmitButton19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(1419, Short.MAX_VALUE))
        );
        C_S_AppointmentManagementPageLayout.setVerticalGroup(
            C_S_AppointmentManagementPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(C_S_AppointmentManagementPageLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jTextField92, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(167, 167, 167)
                .addComponent(SubmitButton20, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(122, 122, 122)
                .addComponent(SubmitButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(185, 185, 185)
                .addComponent(SubmitButton18)
                .addContainerGap(396, Short.MAX_VALUE))
        );

        ContentPanel.add(C_S_AppointmentManagementPage, "AppointManage");

        C_S_createAppointments.setBackground(new java.awt.Color(253, 253, 253));

        jScrollPane5.setForeground(new java.awt.Color(221, 221, 221));

        createAppointTable.setBackground(new java.awt.Color(204, 204, 204));
        createAppointTable.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        createAppointTable.setForeground(new java.awt.Color(102, 102, 102));
        createAppointTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "CustomerID", "Name", "Service Type", "Date", "Time"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        createAppointTable.setGridColor(new java.awt.Color(255, 255, 255));
        jScrollPane5.setViewportView(createAppointTable);

        jTextField45.setBackground(new java.awt.Color(255, 255, 255));
        jTextField45.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jTextField45.setForeground(new java.awt.Color(0, 0, 0));
        jTextField45.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField45.setText("Please Fill In All Fields");
        jTextField45.setBorder(null);
        jTextField45.addActionListener(this::jTextField45ActionPerformed);

        CustAppoinment.addActionListener(this::CustAppoinmentActionPerformed);

        jTextField46.setBackground(new java.awt.Color(102, 102, 102));
        jTextField46.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField46.setForeground(new java.awt.Color(255, 249, 234));
        jTextField46.setText("Enter CustomerID");
        jTextField46.addActionListener(this::jTextField46ActionPerformed);

        jTextField47.setBackground(new java.awt.Color(102, 102, 102));
        jTextField47.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField47.setForeground(new java.awt.Color(255, 249, 234));
        jTextField47.setText("Service Type");
        jTextField47.addActionListener(this::jTextField47ActionPerformed);

        jTextField48.setBackground(new java.awt.Color(102, 102, 102));
        jTextField48.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField48.setForeground(new java.awt.Color(255, 249, 234));
        jTextField48.setText("Enter Valid Date");
        jTextField48.addActionListener(this::jTextField48ActionPerformed);

        jTextField49.setEditable(false);
        jTextField49.setBackground(new java.awt.Color(102, 102, 102));
        jTextField49.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField49.setForeground(new java.awt.Color(255, 249, 234));
        jTextField49.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jTextField49.setText("Enter Name");
        jTextField49.addActionListener(this::jTextField49ActionPerformed);

        jTextField50.setBackground(new java.awt.Color(102, 102, 102));
        jTextField50.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField50.setForeground(new java.awt.Color(255, 249, 234));
        jTextField50.setText("Enter Valid Time");
        jTextField50.addActionListener(this::jTextField50ActionPerformed);

        CreateCustName.addActionListener(this::CreateCustNameActionPerformed);

        TimeField.setText("--hh:mm--");

        SubmitButton13.setBackground(new java.awt.Color(0, 87, 184));
        SubmitButton13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SubmitButton13.setForeground(new java.awt.Color(255, 255, 255));
        SubmitButton13.setText("Confirm Changes");
        SubmitButton13.addActionListener(this::SubmitButton13ActionPerformed);

        ServiceDropdown.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Major Service", "Minor Service" }));

        jTextField10.setBackground(new java.awt.Color(255, 255, 255));
        jTextField10.setFont(new java.awt.Font("Malgun Gothic", 1, 36)); // NOI18N
        jTextField10.setForeground(new java.awt.Color(0, 0, 0));
        jTextField10.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField10.setText("Create Appointments");
        jTextField10.setBorder(null);
        jTextField10.addActionListener(this::jTextField10ActionPerformed);

        SubmitButton31.setBackground(new java.awt.Color(0, 87, 184));
        SubmitButton31.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SubmitButton31.setForeground(new java.awt.Color(255, 255, 255));
        SubmitButton31.setText("Return");
        SubmitButton31.addActionListener(this::SubmitButton31ActionPerformed);

        javax.swing.GroupLayout C_S_createAppointmentsLayout = new javax.swing.GroupLayout(C_S_createAppointments);
        C_S_createAppointments.setLayout(C_S_createAppointmentsLayout);
        C_S_createAppointmentsLayout.setHorizontalGroup(
            C_S_createAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(C_S_createAppointmentsLayout.createSequentialGroup()
                .addGroup(C_S_createAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(C_S_createAppointmentsLayout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(C_S_createAppointmentsLayout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(C_S_createAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SubmitButton31, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 751, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(C_S_createAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(C_S_createAppointmentsLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(C_S_createAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(C_S_createAppointmentsLayout.createSequentialGroup()
                                .addGroup(C_S_createAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextField46, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jTextField47, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jTextField48, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jTextField50, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jTextField49, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(30, 30, 30)
                                .addGroup(C_S_createAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(CreateCustName)
                                    .addComponent(TimeField)
                                    .addComponent(datePicker, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                                    .addComponent(CustAppoinment, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ServiceDropdown, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(C_S_createAppointmentsLayout.createSequentialGroup()
                                .addComponent(jTextField45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(52, 52, 52))))
                    .addGroup(C_S_createAppointmentsLayout.createSequentialGroup()
                        .addGap(101, 101, 101)
                        .addComponent(SubmitButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        C_S_createAppointmentsLayout.setVerticalGroup(
            C_S_createAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(C_S_createAppointmentsLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(C_S_createAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(C_S_createAppointmentsLayout.createSequentialGroup()
                        .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(C_S_createAppointmentsLayout.createSequentialGroup()
                        .addComponent(jTextField45, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(C_S_createAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CustAppoinment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(C_S_createAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(CreateCustName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(C_S_createAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ServiceDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(C_S_createAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(datePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(C_S_createAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(TimeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(41, 41, 41)
                        .addComponent(SubmitButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(146, 146, 146)
                .addComponent(SubmitButton31)
                .addContainerGap(551, Short.MAX_VALUE))
        );

        ContentPanel.add(C_S_createAppointments, "createAppointments");

        C_S_assignAppointments.setBackground(new java.awt.Color(253, 253, 253));

        jScrollPane6.setForeground(new java.awt.Color(221, 221, 221));

        jTable6.setBackground(new java.awt.Color(204, 204, 204));
        jTable6.setForeground(new java.awt.Color(102, 102, 102));
        jTable6.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "AppointmentID", "CustomerID", "TechnicianID", "Service Type", "Date", "Time", "Duration", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable6.setGridColor(new java.awt.Color(255, 255, 255));
        jScrollPane6.setViewportView(jTable6);

        jScrollPane7.setForeground(new java.awt.Color(221, 221, 221));

        jTable7.setBackground(new java.awt.Color(204, 204, 204));
        jTable7.setForeground(new java.awt.Color(102, 102, 102));
        jTable7.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "TechnicianID", "Name", "Status", "Workload (Hours)", "Availability"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable7.setGridColor(new java.awt.Color(255, 255, 255));
        jScrollPane7.setViewportView(jTable7);

        jTextField57.setBackground(new java.awt.Color(255, 255, 255));
        jTextField57.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jTextField57.setForeground(new java.awt.Color(0, 0, 0));
        jTextField57.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField57.setText("Please Assign Appropriate Technicians to Customers");
        jTextField57.setBorder(null);
        jTextField57.addActionListener(this::jTextField57ActionPerformed);

        assignButton.setBackground(new java.awt.Color(0, 87, 184));
        assignButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        assignButton.setForeground(new java.awt.Color(255, 255, 255));
        assignButton.setText("Assign ");
        assignButton.addActionListener(this::assignButtonActionPerformed);

        jTextField23.setBackground(new java.awt.Color(255, 255, 255));
        jTextField23.setFont(new java.awt.Font("Malgun Gothic", 1, 36)); // NOI18N
        jTextField23.setForeground(new java.awt.Color(0, 0, 0));
        jTextField23.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField23.setText("Technicians");
        jTextField23.setBorder(null);
        jTextField23.addActionListener(this::jTextField23ActionPerformed);

        jTextField25.setBackground(new java.awt.Color(255, 255, 255));
        jTextField25.setFont(new java.awt.Font("Malgun Gothic", 1, 36)); // NOI18N
        jTextField25.setForeground(new java.awt.Color(0, 0, 0));
        jTextField25.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField25.setText("Appoinments");
        jTextField25.setBorder(null);
        jTextField25.addActionListener(this::jTextField25ActionPerformed);

        SubmitButton32.setBackground(new java.awt.Color(0, 87, 184));
        SubmitButton32.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SubmitButton32.setForeground(new java.awt.Color(255, 255, 255));
        SubmitButton32.setText("Return");
        SubmitButton32.addActionListener(this::SubmitButton32ActionPerformed);

        javax.swing.GroupLayout C_S_assignAppointmentsLayout = new javax.swing.GroupLayout(C_S_assignAppointments);
        C_S_assignAppointments.setLayout(C_S_assignAppointmentsLayout);
        C_S_assignAppointmentsLayout.setHorizontalGroup(
            C_S_assignAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(C_S_assignAppointmentsLayout.createSequentialGroup()
                .addGroup(C_S_assignAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(C_S_assignAppointmentsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 734, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 532, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(C_S_assignAppointmentsLayout.createSequentialGroup()
                        .addGap(102, 102, 102)
                        .addComponent(jTextField25, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(296, 296, 296)
                        .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(C_S_assignAppointmentsLayout.createSequentialGroup()
                        .addGap(438, 438, 438)
                        .addComponent(jTextField57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(C_S_assignAppointmentsLayout.createSequentialGroup()
                        .addGap(650, 650, 650)
                        .addComponent(assignButton, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(C_S_assignAppointmentsLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(SubmitButton32, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(1037, Short.MAX_VALUE))
        );
        C_S_assignAppointmentsLayout.setVerticalGroup(
            C_S_assignAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(C_S_assignAppointmentsLayout.createSequentialGroup()
                .addGroup(C_S_assignAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(C_S_assignAppointmentsLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jTextField25, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, C_S_assignAppointmentsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(C_S_assignAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jTextField57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(assignButton, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56)
                .addComponent(SubmitButton32)
                .addContainerGap(545, Short.MAX_VALUE))
        );

        ContentPanel.add(C_S_assignAppointments, "assignAppointments");

        C_S_PaymentProcessing.setBackground(new java.awt.Color(253, 253, 253));

        jScrollPane2.setForeground(new java.awt.Color(221, 221, 221));

        PaymentAppointmentTable.setBackground(new java.awt.Color(204, 204, 204));
        PaymentAppointmentTable.setForeground(new java.awt.Color(104, 104, 104));
        PaymentAppointmentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "PaymentID", "AppointmentID", "Customer Name", "Service Type", "Status?"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        PaymentAppointmentTable.setGridColor(new java.awt.Color(255, 255, 255));
        jScrollPane2.setViewportView(PaymentAppointmentTable);

        jTextField13.setBackground(new java.awt.Color(255, 255, 255));
        jTextField13.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jTextField13.setForeground(new java.awt.Color(0, 0, 0));
        jTextField13.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField13.setText("Please Fill In All Fields");
        jTextField13.setBorder(null);
        jTextField13.addActionListener(this::jTextField13ActionPerformed);

        jTextField14.setBackground(new java.awt.Color(102, 102, 102));
        jTextField14.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField14.setForeground(new java.awt.Color(255, 249, 234));
        jTextField14.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField14.setText("Select AppointmentID");
        jTextField14.addActionListener(this::jTextField14ActionPerformed);

        jTextField15.setBackground(new java.awt.Color(102, 102, 102));
        jTextField15.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField15.setForeground(new java.awt.Color(255, 249, 234));
        jTextField15.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField15.setText("Enter Amount");
        jTextField15.addActionListener(this::jTextField15ActionPerformed);

        jTextField16.setBackground(new java.awt.Color(102, 102, 102));
        jTextField16.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField16.setForeground(new java.awt.Color(255, 249, 234));
        jTextField16.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField16.setText("Enter Payment Method");
        jTextField16.addActionListener(this::jTextField16ActionPerformed);

        jTextField17.setEditable(false);
        jTextField17.setBackground(new java.awt.Color(102, 102, 102));
        jTextField17.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField17.setForeground(new java.awt.Color(255, 249, 234));
        jTextField17.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField17.setText("Name");
        jTextField17.addActionListener(this::jTextField17ActionPerformed);

        SubmitButton2.setBackground(new java.awt.Color(0, 87, 184));
        SubmitButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SubmitButton2.setForeground(new java.awt.Color(255, 255, 255));
        SubmitButton2.setText("Confirm Payment");
        SubmitButton2.addActionListener(this::SubmitButton2ActionPerformed);

        PaymentMethod.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cash", "Card", "Online Banking" }));

        SubmitButton17.setBackground(new java.awt.Color(0, 87, 184));
        SubmitButton17.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SubmitButton17.setForeground(new java.awt.Color(255, 255, 255));
        SubmitButton17.setText("Print Receipt");
        SubmitButton17.addActionListener(this::SubmitButton17ActionPerformed);

        jTextField31.setBackground(new java.awt.Color(255, 255, 255));
        jTextField31.setFont(new java.awt.Font("Malgun Gothic", 1, 36)); // NOI18N
        jTextField31.setForeground(new java.awt.Color(0, 0, 0));
        jTextField31.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField31.setText("Process Payments");
        jTextField31.setBorder(null);
        jTextField31.addActionListener(this::jTextField31ActionPerformed);

        SubmitButton33.setBackground(new java.awt.Color(0, 87, 184));
        SubmitButton33.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SubmitButton33.setForeground(new java.awt.Color(255, 255, 255));
        SubmitButton33.setText("Return");
        SubmitButton33.addActionListener(this::SubmitButton33ActionPerformed);

        javax.swing.GroupLayout C_S_PaymentProcessingLayout = new javax.swing.GroupLayout(C_S_PaymentProcessing);
        C_S_PaymentProcessing.setLayout(C_S_PaymentProcessingLayout);
        C_S_PaymentProcessingLayout.setHorizontalGroup(
            C_S_PaymentProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(C_S_PaymentProcessingLayout.createSequentialGroup()
                .addGroup(C_S_PaymentProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(C_S_PaymentProcessingLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 707, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(C_S_PaymentProcessingLayout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(jTextField31, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(C_S_PaymentProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(C_S_PaymentProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(C_S_PaymentProcessingLayout.createSequentialGroup()
                            .addGroup(C_S_PaymentProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, C_S_PaymentProcessingLayout.createSequentialGroup()
                                    .addGroup(C_S_PaymentProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jTextField17)
                                        .addComponent(jTextField14, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE))
                                    .addGap(70, 70, 70))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, C_S_PaymentProcessingLayout.createSequentialGroup()
                                    .addGroup(C_S_PaymentProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jTextField15, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jTextField16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE))
                                    .addGap(71, 71, 71)))
                            .addGroup(C_S_PaymentProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(PaymentCustName, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                                .addComponent(PaymentAmount, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                                .addComponent(PaymentMethod, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(AppointmentCombo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE))
                            .addGap(17, 17, 17))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, C_S_PaymentProcessingLayout.createSequentialGroup()
                            .addGroup(C_S_PaymentProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(SubmitButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                                .addComponent(SubmitButton17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGap(132, 132, 132)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, C_S_PaymentProcessingLayout.createSequentialGroup()
                        .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(82, 82, 82))))
            .addGroup(C_S_PaymentProcessingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(SubmitButton33, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        C_S_PaymentProcessingLayout.setVerticalGroup(
            C_S_PaymentProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(C_S_PaymentProcessingLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(C_S_PaymentProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField31, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(C_S_PaymentProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(C_S_PaymentProcessingLayout.createSequentialGroup()
                        .addGroup(C_S_PaymentProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(AppointmentCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(C_S_PaymentProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField17)
                            .addComponent(PaymentCustName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(C_S_PaymentProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField15)
                            .addComponent(PaymentAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(C_S_PaymentProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField16)
                            .addComponent(PaymentMethod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(88, 88, 88)
                        .addComponent(SubmitButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(SubmitButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(643, Short.MAX_VALUE))
                    .addGroup(C_S_PaymentProcessingLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(218, 218, 218)
                        .addComponent(SubmitButton33)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        ContentPanel.add(C_S_PaymentProcessing, "PaymentProcessing");

        C_S_ReceiptGeneration.setBackground(new java.awt.Color(253, 253, 253));

        SubmitButton4.setBackground(new java.awt.Color(0, 87, 184));
        SubmitButton4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SubmitButton4.setForeground(new java.awt.Color(255, 255, 255));
        SubmitButton4.setText("Return");
        SubmitButton4.addActionListener(this::SubmitButton4ActionPerformed);

        ReceiptArea.setColumns(20);
        ReceiptArea.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        ReceiptArea.setRows(5);
        jScrollPane10.setViewportView(ReceiptArea);

        jTextField96.setBackground(new java.awt.Color(255, 255, 255));
        jTextField96.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jTextField96.setForeground(new java.awt.Color(0, 0, 0));
        jTextField96.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField96.setText("CUSTOMER COPY");
        jTextField96.setBorder(null);
        jTextField96.addActionListener(this::jTextField96ActionPerformed);

        receiptToTxt.setBackground(new java.awt.Color(0, 87, 184));
        receiptToTxt.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        receiptToTxt.setForeground(new java.awt.Color(255, 255, 255));
        receiptToTxt.setText("Save To .txt");
        receiptToTxt.addActionListener(this::receiptToTxtActionPerformed);

        javax.swing.GroupLayout C_S_ReceiptGenerationLayout = new javax.swing.GroupLayout(C_S_ReceiptGeneration);
        C_S_ReceiptGeneration.setLayout(C_S_ReceiptGenerationLayout);
        C_S_ReceiptGenerationLayout.setHorizontalGroup(
            C_S_ReceiptGenerationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(C_S_ReceiptGenerationLayout.createSequentialGroup()
                .addGap(366, 366, 366)
                .addGroup(C_S_ReceiptGenerationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(C_S_ReceiptGenerationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(C_S_ReceiptGenerationLayout.createSequentialGroup()
                            .addComponent(SubmitButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(receiptToTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 564, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(C_S_ReceiptGenerationLayout.createSequentialGroup()
                        .addGap(83, 83, 83)
                        .addComponent(jTextField96, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(1399, Short.MAX_VALUE))
        );
        C_S_ReceiptGenerationLayout.setVerticalGroup(
            C_S_ReceiptGenerationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(C_S_ReceiptGenerationLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jTextField96, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(C_S_ReceiptGenerationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SubmitButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(receiptToTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(629, Short.MAX_VALUE))
        );

        ContentPanel.add(C_S_ReceiptGeneration, "ReceiptGeneration");

        javax.swing.GroupLayout MainPanelLayout = new javax.swing.GroupLayout(MainPanel);
        MainPanel.setLayout(MainPanelLayout);
        MainPanelLayout.setHorizontalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2341, Short.MAX_VALUE)
            .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainPanelLayout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ContentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );
        MainPanelLayout.setVerticalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1120, Short.MAX_VALUE)
            .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainPanelLayout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ContentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        getContentPane().add(MainPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField4ActionPerformed

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jTextField7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField7ActionPerformed

    private void CS_PasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CS_PasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CS_PasswordActionPerformed

    private void jTextField13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField13ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField13ActionPerformed

    private void SubmitButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButton2ActionPerformed
              
        try {
    
    int selectedRow = PaymentAppointmentTable.getSelectedRow();
    

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select an appointment!", "ERROR", JOptionPane.ERROR_MESSAGE);
        return;
    }

    String appointmentID = PaymentAppointmentTable.getValueAt(selectedRow, 1).toString();
    String customerName = PaymentAppointmentTable.getValueAt(selectedRow, 2).toString();
    String serviceType = PaymentAppointmentTable.getValueAt(selectedRow, 3).toString();
    String status = PaymentAppointmentTable.getValueAt(selectedRow, 4).toString();
    

    String paymentMethod = PaymentMethod.getSelectedItem().toString();
    
           
    

   
    if (!status.equals("Completed")) {
        JOptionPane.showMessageDialog(this, "Only completed appointments can be paid!", "ERROR", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (paymentMethod.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please select a payment method!", "ERROR", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // (Optional) get price for display
    double amount = PaymentProcessing.getServicePrice(serviceType);
    PaymentAmount.setText(String.format("%.2f", amount));

    
    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Payment Details:\n\n" +
        "Appointment ID: " + appointmentID + "\n" +
        "Customer: " + customerName + "\n" +
        "Service: " + serviceType + "\n" +
        "Amount: RM" + String.format("%.2f", amount) + "\n" +
        "Method: " + paymentMethod + "\n\n" +
        "Proceed with payment?",
        "CONFIRM PAYMENT",
        JOptionPane.YES_NO_OPTION
    );

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    
    String result = PaymentProcessing.processPayment(appointmentID, paymentMethod, true);

    if (result.startsWith("ERROR")) {
        JOptionPane.showMessageDialog(this, result, "Duplicate Payment Found!", JOptionPane.ERROR_MESSAGE);
        return; // Stop here if it's a duplicate or other error
    }
    
    JOptionPane.showMessageDialog(this, "Payment processed successfully!");
    
    loadPaymentAppointmentsTable();

}catch (Exception e) {
    e.printStackTrace(); // This will tell you the EXACT line that failed in your terminal
    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
}
    }//GEN-LAST:event_SubmitButton2ActionPerformed

    private void SubmitButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButton4ActionPerformed
        CardLayout backbutton = (CardLayout)ContentPanel.getLayout();
        backbutton.show(ContentPanel, "PaymentProcessing");
    }//GEN-LAST:event_SubmitButton4ActionPerformed

    private void jTextField19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField19ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField19ActionPerformed

    private void jTextField24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField24ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField24ActionPerformed

    private void jTextField27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField27ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField27ActionPerformed

    private void jTextField28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField28ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField28ActionPerformed

    private void jTextField29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField29ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField29ActionPerformed

    private void jTextField30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField30ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField30ActionPerformed

    private void CustAgeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CustAgeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CustAgeActionPerformed

    private void SubmitButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButton12ActionPerformed
        // REMEMBER TO FIX THIS 
        try {
    String CustomerCode = CustID.getText().toString();
    String UpdName = CustName.getText().trim();
    String UpdGender = CustGender.getSelectedItem().toString().trim();
    int UpdAge = Integer.parseInt(CustAge.getText().trim()); // Already validated
    String UpdPhoneNumb = CustPhoneNum.getText().trim();
    String UpdEmail = CustEmail.getText().trim();
    String UpdHomeAddress = CustAddress.getText().trim();
    String UpdNationality = CustNationality.getText().trim();
    String UpdCarNumb = CustCarPlate.getText().trim();
    String UpdCarModel = CustCarModel.getText().trim();
   
    Date date = CustDateCreated.getDate();
    if (date == null) {
        JOptionPane.showMessageDialog(this, "Please select a date!", "ERROR", JOptionPane.ERROR_MESSAGE);
        return;
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String UpdDateAdded = sdf.format(date);
    
    // Empty check
    String[] allFields = {UpdName, UpdGender, CustAge.getText().trim(), 
                         UpdPhoneNumb, UpdEmail, UpdHomeAddress, 
                         UpdNationality, UpdCarNumb, UpdCarModel, UpdDateAdded};
    
    if (Arrays.stream(allFields).anyMatch(String::isEmpty)) {
        JOptionPane.showMessageDialog(this, "Please Enter All Required Fields", "ERROR", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    // CONFIRMATION DIALOG - Uses variables declared above
    
   String formattedAddress = UpdHomeAddress.replace("\n", "<br>");

   int type = JOptionPane.showConfirmDialog(ContentPanel,
    "<html>" +
    "<b>Update Customer Details</b>:<br><br>" +

    "Name: <b>" + UpdName + "</b><br>" +
    "Gender: <b>" + UpdGender + "</b><br>" +
    "Age: <b>"+ CustAge.getText().trim()+ "</b><br>"+
    "Phone: <b>" + UpdPhoneNumb + "</b><br>" +
    "Email: <b>" + UpdEmail + "</b><br>" +
    "Address: <b>" + formattedAddress + "</b><br>" +
    "Nationality: <b>" + UpdNationality + "</b><br>" +
    "Car #: <b>" + UpdCarNumb + "</b><br>" +
    "Car Model: <b>" + UpdCarModel + "</b><br><br>" +
    "Date Added: <b>" + UpdDateAdded + "</b><br><br>" +

    "<b>PROCEED WITH CHANGES?</b>" +
    "</html>",
    "CONFIRM UPDATE",
    JOptionPane.YES_NO_OPTION
);
    
    
    switch(type) {
        case 0: // OK
            Customer customer = new Customer();
            customer.setCustomerID(CustomerCode);
            customer.setUsername(UpdName);
            customer.setGender(UpdGender);
            customer.setPhoneNumber(UpdPhoneNumb);
            customer.setAge(UpdAge);
            customer.setEmail(UpdEmail);
            customer.setHomeAddress(UpdHomeAddress);
            customer.setNationality(UpdNationality);
            customer.setCarPlate(UpdCarNumb);
            customer.setCarModel(UpdCarModel);
            customer.setDateAdded(UpdDateAdded);

        CustomerManagement.updateCustomer(customer);

        JOptionPane.showMessageDialog(this, "Customer updated successfully!");
        
        loadCustomers("");
            
            break;
            
        case 1: // Cancel
            return;
    }
    
} catch (NumberFormatException e) {
    JOptionPane.showMessageDialog(this, "Age must be a valid number!", "ERROR", JOptionPane.ERROR_MESSAGE);
} catch (Exception e) {
    e.printStackTrace(); // This will tell you the EXACT line that failed in your terminal
    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
}
        
        
        
        
    }//GEN-LAST:event_SubmitButton12ActionPerformed

    private void jTextField45ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField45ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField45ActionPerformed

    private void jTextField46ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField46ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField46ActionPerformed

    private void jTextField47ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField47ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField47ActionPerformed

    private void jTextField48ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField48ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField48ActionPerformed

    private void jTextField49ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField49ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField49ActionPerformed

    private void jTextField50ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField50ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField50ActionPerformed

    private void SubmitButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButton13ActionPerformed
                                                   
        try {
            // 1. Capture Raw Inputs
            Object selectedItem = CustAppoinment.getSelectedItem();
            Object selectedService = ServiceDropdown.getSelectedItem();
            Date rawDate = datePicker.getDate();
            String rawTime = TimeField.getText().trim();

            // 2. Empty Field Validation (Figure 4.2.2)
            if (selectedItem == null || selectedService == null || rawDate == null || rawTime.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields (Customer, Service, Date, and Time).", "Empty Fields", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 3. Date Validation: Prevent Past Dates (Figure 4.2.4)
            LocalDate date = rawDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            
            if (date.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "You cannot schedule an appointment for a past date.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 4. Time Format Validation (Figure 4.2.3)
            LocalTime time;
            try {
                time = LocalTime.parse(rawTime);
            } catch (java.time.format.DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid time format! Please use HH:mm (e.g., 14:30).", "Time Format Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            
            LocalDateTime dateTime = LocalDateTime.of(date, time);
            String customerID = selectedItem.toString();
            String serviceType = selectedService.toString();

            Appointment app = new Appointment();
            
            app.validateAndSave(dateTime, customerID, serviceType);             

            JOptionPane.showMessageDialog(this, "Appointment Created Successfully!");
            loadAppointments();

        } catch (IllegalArgumentException e) {
            
            JOptionPane.showMessageDialog(this, e.getMessage(), "Service Center Error", JOptionPane.ERROR_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace(); 
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_SubmitButton13ActionPerformed

    private void jTextField57ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField57ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField57ActionPerformed

    private void assignButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_assignButtonActionPerformed
      
    try {
        int appRow = jTable6.getSelectedRow();
        int techRow = jTable7.getSelectedRow();

    if (appRow == -1 || techRow == -1) {
        JOptionPane.showMessageDialog(null, "Select appointment and technician");
        return;
    }

    String appointmentID = jTable6.getValueAt(appRow, 0).toString();
    String technicianID = jTable7.getValueAt(techRow, 0).toString();

    service.assignTechnician(appointmentID, technicianID);

    JOptionPane.showMessageDialog(null, "Assigned Successfully!");
        loadAppointmentsService();
        loadTechniciansService();
    
        
        }catch (Exception e) {
            e.printStackTrace(); // This will tell you the EXACT line that failed in your terminal
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Excedeed Working Hours Limit ", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_assignButtonActionPerformed

    private void CustAppoinmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CustAppoinmentActionPerformed
        String selectedCustomerID = CustAppoinment.getSelectedItem().toString();

    List<String[]> customers = customerRepo.getAllCustomers();

    for (String[] data : customers) {
        // data[0] = Customer ID
        // data[1] = Customer Name

        if (data[0].equals(selectedCustomerID)) {
            CreateCustName.setText(data[1]); 
            break;
        }
    }
    }//GEN-LAST:event_CustAppoinmentActionPerformed

    private void jTextField51ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField51ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField51ActionPerformed

    private void jTextField56ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField56ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField56ActionPerformed

    private void jTextField58ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField58ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField58ActionPerformed

    private void jTextField59ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField59ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField59ActionPerformed

    private void jTextField60ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField60ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField60ActionPerformed

    private void jTextField61ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField61ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField61ActionPerformed

    private void jTextField62ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField62ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField62ActionPerformed

    private void jTextField64ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField64ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField64ActionPerformed

    private void SubmitButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButton15ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SubmitButton15ActionPerformed

    private void jTextField92ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField92ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField92ActionPerformed

    private void SubmitButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButton18ActionPerformed
        CardLayout backbutton = (CardLayout)ContentPanel.getLayout();
        backbutton.show(ContentPanel, "CounterStaffMasterPage");
    }//GEN-LAST:event_SubmitButton18ActionPerformed

    private void SubmitButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButton19ActionPerformed
        CardLayout backbutton = (CardLayout)ContentPanel.getLayout();
        backbutton.show(ContentPanel, "assignAppointments");
    
        if (appointmentModel == null) {
            setupAppointmentAssignmentTables();
        } else {
            // Refresh data only
            loadAppointmentsService();
            loadTechniciansService();
        }
        
    }//GEN-LAST:event_SubmitButton19ActionPerformed

    private void SubmitButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButton20ActionPerformed

        CardLayout backbutton = (CardLayout)ContentPanel.getLayout();
        backbutton.show(ContentPanel, "createAppointments");
        
        loadAppointments();
        loadCustomerDropdown();
    }//GEN-LAST:event_SubmitButton20ActionPerformed

    private void jTextField93ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField93ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField93ActionPerformed

    private void SubmitButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButton21ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SubmitButton21ActionPerformed

    private void SubmitButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButton22ActionPerformed
        CardLayout backbutton = (CardLayout)ContentPanel.getLayout();
        backbutton.show(ContentPanel, "CustomerManagement");
        
        loadCustomers("");
    }//GEN-LAST:event_SubmitButton22ActionPerformed

    private void SubmitButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButton23ActionPerformed
        CardLayout backbutton = (CardLayout)ContentPanel.getLayout();
        backbutton.show(ContentPanel, "AppointManage");
    }//GEN-LAST:event_SubmitButton23ActionPerformed

    private void SubmitButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButton24ActionPerformed
        CardLayout backbutton = (CardLayout)ContentPanel.getLayout();
        backbutton.show(ContentPanel, "userEditPage");
    }//GEN-LAST:event_SubmitButton24ActionPerformed

    private void SubmitButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButton25ActionPerformed
        CardLayout backbutton = (CardLayout)ContentPanel.getLayout();
        backbutton.show(ContentPanel, "PaymentProcessing");
        
       
        
    }//GEN-LAST:event_SubmitButton25ActionPerformed

    private void jTextField95ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField95ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField95ActionPerformed

    private void jTextField97ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField97ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField97ActionPerformed

    private void jTextField98ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField98ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField98ActionPerformed

    private void CustEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CustEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CustEmailActionPerformed

    private void jTextField101ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField101ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField101ActionPerformed

    private void SubmitButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButton14ActionPerformed
        
           try {
    String CustomerCode = CustID.getText().toString();
    String UpdName = CustName.getText().trim();
    String UpdGender = CustGender.getSelectedItem().toString().trim();
    int UpdAge = Integer.parseInt(CustAge.getText().trim()); // Already validated
    String UpdPhoneNumb = CustPhoneNum.getText().trim();
    String UpdEmail = CustEmail.getText().trim();
    String UpdHomeAddress = CustAddress.getText().trim();
    String UpdNationality = CustNationality.getText().trim();
    String UpdCarNumb = CustCarPlate.getText().trim();
    String UpdCarModel = CustCarModel.getText().trim();
   
    Date date = CustDateCreated.getDate();
    if (date == null) {
        JOptionPane.showMessageDialog(this, "Please select a date!", "ERROR", JOptionPane.ERROR_MESSAGE);
        return;
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String UpdDateAdded = sdf.format(date);
    
    // Empty check
    String[] allFields = {UpdName, UpdGender, CustAge.getText().trim(), 
                         UpdPhoneNumb, UpdEmail, UpdHomeAddress, 
                         UpdNationality, UpdCarNumb, UpdCarModel, UpdDateAdded};
    
    if (Arrays.stream(allFields).anyMatch(String::isEmpty)) {
        JOptionPane.showMessageDialog(this, "Please Enter All Required Fields", "ERROR", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    // CONFIRMATION DIALOG - Uses variables declared above
   int type = JOptionPane.showConfirmDialog(ContentPanel, 
    " Customer Details:\n\n" +
    "Name: " + UpdName + "\n" +
    "Gender: " + UpdGender + "\n" +
    "Phone: " + UpdPhoneNumb + "\n" +
    "Email: " + UpdEmail + "\n" +
    "Address: " + UpdHomeAddress + "\n" +
    "Nationality: " + UpdNationality + "\n" +
    "Car #: " + UpdCarNumb + "\n" +
    "Car Model: " + UpdCarModel + "\n\n" +
    "Date Added: " + UpdDateAdded + "\n\n" +
    "Proceed with changes?", 
    "CONFIRM ADDITION", JOptionPane.YES_NO_OPTION);
    
    
    switch(type) {
        case 0: // OK
            Customer customer = new Customer();
            customer.setCustomerID(CustomerManagement.generateCustomerID());
            customer.setUsername(UpdName);
            customer.setGender(UpdGender);
            customer.setPhoneNumber(UpdPhoneNumb);
            customer.setAge(UpdAge);
            customer.setEmail(UpdEmail);
            customer.setHomeAddress(UpdHomeAddress);
            customer.setNationality(UpdNationality);
            customer.setCarPlate(UpdCarNumb);
            customer.setCarModel(UpdCarModel);
            customer.setDateAdded(UpdDateAdded);


         CustomerManagement.addCustomer(customer);
            JOptionPane.showMessageDialog(this, "Customer added successfully!");
            
            loadCustomers("");
            CustID.setText(CustomerManagement.generateCustomerID());
            
            
            
        CustName.setText("");
        CustAge.setText(""); 
        CustPhoneNum.setText("");
        CustEmail.setText("");
        CustAddress.setText("");
        CustNationality.setText("");
        CustCarPlate.setText("");
        CustCarModel.setText("");
        CustDateCreated.setDate(null);
     
            break;
        case 1: // Cancel
            return;
    }
    
} catch (NumberFormatException e) {
    JOptionPane.showMessageDialog(this, "Age must be a valid number!", "ERROR", JOptionPane.ERROR_MESSAGE);
} catch (Exception e) {
    e.printStackTrace(); // This will tell you the EXACT line that failed in your terminal
    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
}
        
        
        
        
        
        
        
    }//GEN-LAST:event_SubmitButton14ActionPerformed

    private void SubmitButton26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButton26ActionPerformed
        
        int row = jTableCustomer.getSelectedRow();

    // Check if row selected
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Please select a customer to delete!");
        return;
    }

    String customerID = CustID.getText();

    // Confirmation dialog
    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Delete Customer?\n\n" +
        "ID: " + customerID + "\n" +
        "Name: " + CustName.getText() + "\n\n" +
        "This action CANNOT be undone!",
        "CONFIRM DELETE",
        JOptionPane.YES_NO_OPTION
    );

    // If user confirms
    if (confirm == JOptionPane.YES_OPTION) {

        CustomerManagement.deleteCustomer(customerID);

        JOptionPane.showMessageDialog(this, "Customer deleted successfully!");

        loadCustomers(""); // refresh table
        clearFields();   // clear form
    }
        
        
    }//GEN-LAST:event_SubmitButton26ActionPerformed

    private void jTextField69ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField69ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField69ActionPerformed

    private void jTextField105ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField105ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField105ActionPerformed

    private void CustIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CustIDActionPerformed
        
        
        
        
    }//GEN-LAST:event_CustIDActionPerformed

    private void CustGenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CustGenderActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CustGenderActionPerformed

    private void SubmitButton27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButton27ActionPerformed
        CardLayout backbutton = (CardLayout)ContentPanel.getLayout();
        backbutton.show(ContentPanel, "CounterStaffMasterPage");
    }//GEN-LAST:event_SubmitButton27ActionPerformed

    private void SubmitButton28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButton28ActionPerformed
        
          try {
    String UserCode = CS_UserID.getText().toString();
    String UserName = CS_Name.getText().trim();
    String UserGender = CS_Gender.getSelectedItem().toString().trim();
    String UserPhoneNumb = CS_PhoneNum.getText().trim();
    String UserEmail = CS_Email.getText().trim();
    String UserHomeAddress = CS_Address.getText().trim();
    String UserNationality = CS_Nationality.getText().trim();
    String UserPassword = CS_Password.getText().trim();
    
   
    
    
    // Empty check
    String[] allFields = {UserCode, UserGender, UserName, 
                         UserPhoneNumb, UserEmail, UserHomeAddress, 
                         UserNationality, UserPassword};
    
    if (Arrays.stream(allFields).anyMatch(String::isEmpty)) {
        JOptionPane.showMessageDialog(this, "Please Enter All Required Fields", "ERROR", JOptionPane.ERROR_MESSAGE);
        return;
    }

    
    if (UserPassword.length() < 8) {
        JOptionPane.showMessageDialog(this, "Password must be at least 8 characters!", "ERROR", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Checks if phone contains only digits and is between 10-11 chars (Standard MY format)
    if (!UserPhoneNumb.matches("\\d{10,11}")) {
        JOptionPane.showMessageDialog(this, "Invalid Phone Number! Must be 10-11 digits without symbols.", "ERROR", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // 4. EMAIL VALIDATION (Optional but good for marks)
    if (!UserEmail.contains("@") || !UserEmail.contains(".")) {
        JOptionPane.showMessageDialog(this, "Invalid Email Format!", "ERROR", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    // CONFIRMATION DIALOG - Uses variables declared above
   int type = JOptionPane.showConfirmDialog(ContentPanel, 
    "Update Current Details ?:\n\n" +
    "Name: " + UserCode + "\n" +
    "Gender: " + UserName + "\n" +
    "Password: " + UserPassword + "\n\n" +
    "Gender: " + UserGender + "\n" +        
    "Phone: " + UserPhoneNumb + "\n" +
    "Email: " + UserEmail + "\n" +
    "Address: " + UserHomeAddress + "\n" +
    "Nationality: " + UserNationality + "\n" +
    "Proceed with changes?", 
    "CONFIRM UPDATE", JOptionPane.YES_NO_OPTION);
    
    
    switch(type) {
        case 0: // OK
            UserDetailsEdit.updateUser(UserCode, UserName, UserGender, UserPassword, UserPhoneNumb,      
                                UserEmail, UserHomeAddress, UserNationality);
            
            loadUsersTable("CS");
            JOptionPane.showMessageDialog(this, "Profile Updated Successfully!");
            break;
            
        case 1: // Cancel
            return;
    }
    
} catch (NumberFormatException e) {
    JOptionPane.showMessageDialog(this, "Age must be a valid number!", "ERROR", JOptionPane.ERROR_MESSAGE);
} catch (Exception e) {
    JOptionPane.showMessageDialog(this, "Invalid input format!", "ERROR", JOptionPane.ERROR_MESSAGE);
}
        
        
    }//GEN-LAST:event_SubmitButton28ActionPerformed

    private void CS_NameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CS_NameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CS_NameActionPerformed

    private void CS_GenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CS_GenderActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CS_GenderActionPerformed

    private void jTextField8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField8ActionPerformed

    private void jTextField11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField11ActionPerformed

    private void jTextField16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField16ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField16ActionPerformed

    private void jTextField15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField15ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField15ActionPerformed

    private void jTextField17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField17ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField17ActionPerformed

    private void jTextField14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField14ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField14ActionPerformed

    private void jTextField96ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField96ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField96ActionPerformed

    private void receiptToTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_receiptToTxtActionPerformed
        
        String receiptText = ReceiptArea.getText();

        String result = PaymentProcessing.saveReceiptToFile(receiptText);

    if (result.startsWith("ERROR")) {
        JOptionPane.showMessageDialog(this, result, "ERROR", JOptionPane.ERROR_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(this, "Receipt saved!\n" + result);
    }
        
        
    }//GEN-LAST:event_receiptToTxtActionPerformed

    private void SubmitButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButton17ActionPerformed
        try {
    int selectedRow = PaymentAppointmentTable.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select an appointment!", "ERROR", JOptionPane.ERROR_MESSAGE);
        return;
    }

    
    String appointmentID = PaymentAppointmentTable.getValueAt(selectedRow, 1).toString();
    String paymentMethod = PaymentMethod.getSelectedItem().toString();
    
    
    String receiptContent = PaymentProcessing.processPayment(appointmentID, paymentMethod, false);
    
    
    if (receiptContent.startsWith("ERROR")) {
        JOptionPane.showMessageDialog(this, receiptContent, "Payment Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // 4. FILL the JTextArea with the receipt content
    ReceiptArea.setText(receiptContent);
    
    // 5. Switch the view to the Receipt screen
    CardLayout backbutton = (CardLayout)ContentPanel.getLayout();
    backbutton.show(ContentPanel, "ReceiptGeneration");
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error processing payment: " + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_SubmitButton17ActionPerformed

    private void custIDSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_custIDSearchActionPerformed
        
        String CustSearch = custIDSearch.getText().trim();
        
        loadCustomers("");

        loadCustomers(CustSearch); 
        
        
        
    }//GEN-LAST:event_custIDSearchActionPerformed

    private void jTextField20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField20ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField20ActionPerformed

    private void SubmitButton54ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButton54ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SubmitButton54ActionPerformed

    private void SubmitButton55ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButton55ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SubmitButton55ActionPerformed

    private void jTextField94ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField94ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField94ActionPerformed

    private void jTextField9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField9ActionPerformed

    private void jTextField10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField10ActionPerformed

    private void jTextField23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField23ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField23ActionPerformed

    private void jTextField25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField25ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField25ActionPerformed

    private void jTextField31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField31ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField31ActionPerformed

    private void SubmitButton29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButton29ActionPerformed
        CardLayout backbutton = (CardLayout)ContentPanel.getLayout();
        backbutton.show(ContentPanel, "CounterStaffMasterPage");
    }//GEN-LAST:event_SubmitButton29ActionPerformed

    private void SubmitButton30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButton30ActionPerformed
        CardLayout backbutton = (CardLayout)ContentPanel.getLayout();
        backbutton.show(ContentPanel, "CounterStaffMasterPage");
        
    }//GEN-LAST:event_SubmitButton30ActionPerformed

    private void CreateCustNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CreateCustNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CreateCustNameActionPerformed

    private void SubmitButton31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButton31ActionPerformed

        CardLayout backbutton = (CardLayout)ContentPanel.getLayout();
        backbutton.show(ContentPanel, "AppointManage");
    }//GEN-LAST:event_SubmitButton31ActionPerformed

    private void SubmitButton32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButton32ActionPerformed
        CardLayout backbutton = (CardLayout)ContentPanel.getLayout();
        backbutton.show(ContentPanel, "AppointManage");    }//GEN-LAST:event_SubmitButton32ActionPerformed

    private void SubmitButton33ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButton33ActionPerformed
        CardLayout backbutton = (CardLayout)ContentPanel.getLayout();
        backbutton.show(ContentPanel, "CounterStaffMasterPage");
    }//GEN-LAST:event_SubmitButton33ActionPerformed

    private void jTextField99ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField99ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField99ActionPerformed


public class HoverEffect extends MouseAdapter {

    private JButton button;
    private Color hoverColor;
    private Color normalColor;

    public HoverEffect(JButton button, Color hoverColor, Color normalColor) {
        this.button = button;
        this.hoverColor = hoverColor;
        this.normalColor = normalColor;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        button.setOpaque(true);
        button.setForeground(hoverColor);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        button.setOpaque(false);
        button.setBackground(normalColor);
    }
}
    
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new ProfileManagement().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField AppointmentCombo;
    private javax.swing.JPanel ButtonPanel;
    private javax.swing.JTextField CS_Address;
    private javax.swing.JTextField CS_Email;
    private javax.swing.JComboBox<String> CS_Gender;
    private javax.swing.JTextField CS_Name;
    private javax.swing.JTextField CS_Nationality;
    private javax.swing.JTextField CS_Password;
    private javax.swing.JTextField CS_PhoneNum;
    private javax.swing.JTextField CS_UserID;
    private javax.swing.JPanel C_S_AppointmentManagementPage;
    private javax.swing.JPanel C_S_CounterStaffMasterPage;
    private javax.swing.JPanel C_S_PaymentProcessing;
    private javax.swing.JPanel C_S_ReceiptGeneration;
    private javax.swing.JPanel C_S_UserEditPage;
    private javax.swing.JPanel C_S_assignAppointments;
    private javax.swing.JPanel C_S_createAppointments;
    private javax.swing.JPanel C_S_createNewCustomers;
    private javax.swing.JPanel C_S_viewCustomersList;
    private javax.swing.JPanel ContentPanel;
    private javax.swing.JTextField CreateCustName;
    private java.awt.TextArea CustAddress;
    private javax.swing.JTextField CustAge;
    private javax.swing.JComboBox<String> CustAppoinment;
    private javax.swing.JTextField CustCarModel;
    private javax.swing.JTextField CustCarPlate;
    private com.toedter.calendar.JDateChooser CustDateCreated;
    private javax.swing.JTextField CustEmail;
    private javax.swing.JComboBox<String> CustGender;
    private javax.swing.JTextField CustID;
    private javax.swing.JTextField CustName;
    private javax.swing.JTextField CustNationality;
    private javax.swing.JTextField CustPhoneNum;
    private javax.swing.JPanel MainPanel;
    private javax.swing.JTextField PaymentAmount;
    private javax.swing.JTable PaymentAppointmentTable;
    private javax.swing.JTextField PaymentCustName;
    private javax.swing.JComboBox<String> PaymentMethod;
    private javax.swing.JTextArea ReceiptArea;
    private javax.swing.JComboBox<String> ServiceDropdown;
    private javax.swing.JButton SubmitButton12;
    private javax.swing.JButton SubmitButton13;
    private javax.swing.JButton SubmitButton14;
    private javax.swing.JButton SubmitButton15;
    private javax.swing.JButton SubmitButton17;
    private javax.swing.JButton SubmitButton18;
    private javax.swing.JButton SubmitButton19;
    private javax.swing.JButton SubmitButton2;
    private javax.swing.JButton SubmitButton20;
    private javax.swing.JButton SubmitButton21;
    private javax.swing.JButton SubmitButton22;
    private javax.swing.JButton SubmitButton23;
    private javax.swing.JButton SubmitButton24;
    private javax.swing.JButton SubmitButton25;
    private javax.swing.JButton SubmitButton26;
    private javax.swing.JButton SubmitButton27;
    private javax.swing.JButton SubmitButton28;
    private javax.swing.JButton SubmitButton29;
    private javax.swing.JButton SubmitButton30;
    private javax.swing.JButton SubmitButton31;
    private javax.swing.JButton SubmitButton32;
    private javax.swing.JButton SubmitButton33;
    private javax.swing.JButton SubmitButton4;
    private javax.swing.JButton SubmitButton54;
    private javax.swing.JButton SubmitButton55;
    private javax.swing.JTextField TimeField;
    private javax.swing.JButton assignButton;
    private javax.swing.JTable createAppointTable;
    private javax.swing.JTextField custIDSearch;
    private com.toedter.calendar.JDateChooser datePicker;
    private javax.swing.JComboBox<String> jComboBox9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTable jTable6;
    private javax.swing.JTable jTable7;
    private javax.swing.JTable jTable8;
    private javax.swing.JTable jTableCustomer;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField101;
    private javax.swing.JTextField jTextField105;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField16;
    private javax.swing.JTextField jTextField17;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField20;
    private javax.swing.JTextField jTextField23;
    private javax.swing.JTextField jTextField24;
    private javax.swing.JTextField jTextField25;
    private javax.swing.JTextField jTextField27;
    private javax.swing.JTextField jTextField28;
    private javax.swing.JTextField jTextField29;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField30;
    private javax.swing.JTextField jTextField31;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField45;
    private javax.swing.JTextField jTextField46;
    private javax.swing.JTextField jTextField47;
    private javax.swing.JTextField jTextField48;
    private javax.swing.JTextField jTextField49;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField50;
    private javax.swing.JTextField jTextField51;
    private javax.swing.JTextField jTextField56;
    private javax.swing.JTextField jTextField57;
    private javax.swing.JTextField jTextField58;
    private javax.swing.JTextField jTextField59;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField60;
    private javax.swing.JTextField jTextField61;
    private javax.swing.JTextField jTextField62;
    private javax.swing.JTextField jTextField63;
    private javax.swing.JTextField jTextField64;
    private javax.swing.JTextField jTextField65;
    private javax.swing.JTextField jTextField66;
    private javax.swing.JTextField jTextField67;
    private javax.swing.JTextField jTextField69;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JTextField jTextField92;
    private javax.swing.JTextField jTextField93;
    private javax.swing.JTextField jTextField94;
    private javax.swing.JTextField jTextField95;
    private javax.swing.JTextField jTextField96;
    private javax.swing.JTextField jTextField97;
    private javax.swing.JTextField jTextField98;
    private javax.swing.JTextField jTextField99;
    private javax.swing.JButton receiptToTxt;
    private javax.swing.JTable userTable;
    // End of variables declaration//GEN-END:variables
}
