package forms.rentas;

import common.constants.ApplicationConstants;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import model.EstadoEvento;
import model.Tipo;
import model.Usuario;


public class FiltersConsultarRentas extends javax.swing.JDialog {
    
    private List<Tipo> typesGlobal;
    private List<EstadoEvento> statusListGlobal;
    private List<Usuario> choferes;


    public FiltersConsultarRentas(java.awt.Frame parent, boolean modal, List<Tipo> typesGlobal, List<EstadoEvento> statusListGlobal, List<Usuario> choferes ) {
        super(parent, modal);
        initComponents();
        this.typesGlobal = typesGlobal;
        this.statusListGlobal = statusListGlobal;
        this.choferes = choferes;
        initInfo();
        addEventListener();
    }

    private FiltersConsultarRentas(JFrame jFrame, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    private void addEventListener() {
        
        cmbUpdateCurrentStatus.addActionListener ((ActionEvent e) -> {
            setLblInfoStatusChange();
        });
        cmbUpdateChangeStatus.addActionListener ((ActionEvent e) -> {
            setLblInfoStatusChange();
        });
               
        txtUpdateStatusInit.getDateEditor().addPropertyChangeListener((PropertyChangeEvent e) -> {
            if ("date".equals(e.getPropertyName())) {
                setLblInfoStatusChange();
            }
        });
        
        txtUpdateStatusEnd.getDateEditor().addPropertyChangeListener((PropertyChangeEvent e) -> {
            if ("date".equals(e.getPropertyName())) {
                setLblInfoStatusChange();
            }
        });
        
        
        cmbUpdateCurrentType.addActionListener ((ActionEvent e) -> {
            setLblInfoTypeChange();
        });
        cmbUpdateChangeType.addActionListener ((ActionEvent e) -> {
            setLblInfoTypeChange();
        });
               
        txtUpdateTypeInit.getDateEditor().addPropertyChangeListener((PropertyChangeEvent e) -> {
            if ("date".equals(e.getPropertyName())) {
                setLblInfoTypeChange();
            }
        });
        
        txtUpdateTypeEnd.getDateEditor().addPropertyChangeListener((PropertyChangeEvent e) -> {
            if ("date".equals(e.getPropertyName())) {
                setLblInfoTypeChange();
            }
        });
        
        
    }
    
    private void setLblInfoStatusChange () {
        
        final String FORMAT_DATE = "dd/MM/yy"; 
        EstadoEvento currentStatus = (EstadoEvento) cmbUpdateCurrentStatus.getModel().getSelectedItem();
        EstadoEvento changeStatus = (EstadoEvento) cmbUpdateChangeStatus.getModel().getSelectedItem();
        String orderStatusChangeInitDate = txtUpdateStatusInit.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtUpdateStatusInit.getDate()) : null;
        String orderStatusChangeEndDate = txtUpdateStatusEnd.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtUpdateStatusEnd.getDate()) : null;
        
        StringBuilder sb = new StringBuilder();
        
        if (currentStatus != null && !currentStatus.getEstadoId().equals(0) && changeStatus != null && !changeStatus.getEstadoId().equals(0)) {
            sb.append("Estado de [").append(currentStatus.getDescripcion()).append("] a [").append(changeStatus.getDescripcion()).append("]");
        }
        if (currentStatus != null && !currentStatus.getEstadoId().equals(0) && changeStatus != null && !changeStatus.getEstadoId().equals(0) &&
                orderStatusChangeInitDate != null && orderStatusChangeEndDate != null) {
            sb.append(", entre ").append(orderStatusChangeInitDate).append(" y ").append(orderStatusChangeEndDate);
        }
        
        lblInfoStatusChange.setText(sb.toString());
    }
    
    private void setLblInfoTypeChange () {
        final String FORMAT_DATE = "dd/MM/yy"; 
        Tipo current = (Tipo) cmbUpdateCurrentType.getModel().getSelectedItem();
        Tipo change = (Tipo) cmbUpdateChangeType.getModel().getSelectedItem();
        String initDate = txtUpdateTypeInit.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtUpdateTypeInit.getDate()) : null;
        String endDate = txtUpdateTypeEnd.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtUpdateTypeEnd.getDate()) : null;
        
        StringBuilder sb = new StringBuilder();
        
        if (current != null && !current.getTipoId().equals(0) && change != null && !change.getTipoId().equals(0)) {
            sb.append("Tipo de [").append(current.getTipo()).append("] a [").append(change.getTipo()).append("]");
        }
        if (current != null && !current.getTipoId().equals(0) && change != null && !change.getTipoId().equals(0) &&
                initDate != null && endDate != null) {
            sb.append(", entre ").append(initDate).append(" y ").append(endDate);
        }
        
        lblInfoTypeChange.setText(sb.toString());
    }
    
    private void initInfo () {
        
        
        cmbStatus.removeAllItems();
        cmbUpdateCurrentStatus.removeAllItems();
        cmbUpdateChangeStatus.removeAllItems();
        cmbUpdateChangeStatus.addItem(
                new EstadoEvento(0, ApplicationConstants.CMB_SELECCIONE)
        );
        cmbStatus.addItem(
                new EstadoEvento(0, ApplicationConstants.CMB_SELECCIONE)
        );
        cmbUpdateCurrentStatus.addItem(
                new EstadoEvento(0, ApplicationConstants.CMB_SELECCIONE)
        );
        statusListGlobal.stream().forEach(t -> {
            cmbStatus.addItem(t);
            cmbUpdateCurrentStatus.addItem(t);
            cmbUpdateChangeStatus.addItem(t);
        });
        
        
        
        cmbEventType.removeAllItems();
        cmbUpdateCurrentType.removeAllItems();
        cmbUpdateChangeType.removeAllItems();
        
        cmbUpdateCurrentType.addItem(
                new Tipo(0, ApplicationConstants.CMB_SELECCIONE)
        );
        cmbUpdateChangeType.addItem(
                new Tipo(0, ApplicationConstants.CMB_SELECCIONE)
        );
        cmbEventType.addItem(
                new Tipo(0, ApplicationConstants.CMB_SELECCIONE)
        );
        typesGlobal.stream().forEach(t -> {
            cmbEventType.addItem(t);
            cmbUpdateCurrentType.addItem(t);
            cmbUpdateChangeType.addItem(t);
        });
        
        cmbDriver.removeAllItems();
        
        cmbDriver.addItem(
                new Usuario(0, ApplicationConstants.CMB_SELECCIONE)
        );
        choferes.stream().forEach(t -> {
            cmbDriver.addItem(t);
        });
        
        cmbLimit.removeAllItems();
        cmbLimit.addItem("100");
        cmbLimit.addItem("1000");
        cmbLimit.addItem("5000");
        
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtCustomer = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        cmbDriver = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtDeliveryInitDate = new com.toedter.calendar.JDateChooser();
        txtDeliveryEndDate = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        txtEventInitDate = new com.toedter.calendar.JDateChooser();
        txtEventEndDate = new com.toedter.calendar.JDateChooser();
        jLabel8 = new javax.swing.JLabel();
        cmbLimit = new javax.swing.JComboBox<>();
        btnApply = new javax.swing.JButton();
        cmbEventType = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtCreatedInitDate = new com.toedter.calendar.JDateChooser();
        txtCreatedEndDate = new com.toedter.calendar.JDateChooser();
        jPanel1 = new javax.swing.JPanel();
        txtUpdateStatusInit = new com.toedter.calendar.JDateChooser();
        txtUpdateStatusEnd = new com.toedter.calendar.JDateChooser();
        jLabel10 = new javax.swing.JLabel();
        cmbUpdateCurrentStatus = new javax.swing.JComboBox<>();
        cmbUpdateChangeStatus = new javax.swing.JComboBox<>();
        lblInfoStatusChange = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        txtUpdateTypeInit = new com.toedter.calendar.JDateChooser();
        txtUpdateTypeEnd = new com.toedter.calendar.JDateChooser();
        jLabel14 = new javax.swing.JLabel();
        cmbUpdateCurrentType = new javax.swing.JComboBox<>();
        cmbUpdateChangeType = new javax.swing.JComboBox<>();
        lblInfoTypeChange = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Ingresa la información para realizar la busqueda");

        txtCustomer.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCustomerActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Estado:");

        cmbStatus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbStatus.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Cliente:");

        cmbDriver.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbDriver.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Chofer:");

        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Fecha de entrega: (es necesario indicar fecha inicial y fecha final)");

        txtDeliveryInitDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtDeliveryInitDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDeliveryInitDateMouseClicked(evt);
            }
        });
        txtDeliveryInitDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDeliveryInitDateKeyPressed(evt);
            }
        });

        txtDeliveryEndDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtDeliveryEndDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDeliveryEndDateMouseClicked(evt);
            }
        });
        txtDeliveryEndDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDeliveryEndDateKeyPressed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Fecha del evento: (es necesario indicar fecha inicial y fecha final)");

        txtEventInitDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtEventInitDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtEventInitDateMouseClicked(evt);
            }
        });
        txtEventInitDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtEventInitDateKeyPressed(evt);
            }
        });

        txtEventEndDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtEventEndDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtEventEndDateMouseClicked(evt);
            }
        });
        txtEventEndDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtEventEndDateKeyPressed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Limiar resultados:");

        cmbLimit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbLimit.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbLimit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btnApply.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnApply.setText("Aplicar filtro");
        btnApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyActionPerformed(evt);
            }
        });

        cmbEventType.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbEventType.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Tipo de evento:");

        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Fecha de elaboración: (es necesario indicar fecha inicial y fecha final)");

        txtCreatedInitDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtCreatedInitDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCreatedInitDateMouseClicked(evt);
            }
        });
        txtCreatedInitDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCreatedInitDateKeyPressed(evt);
            }
        });

        txtCreatedEndDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtCreatedEndDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCreatedEndDateMouseClicked(evt);
            }
        });
        txtCreatedEndDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCreatedEndDateKeyPressed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtUpdateStatusInit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtUpdateStatusInit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtUpdateStatusInitMouseClicked(evt);
            }
        });
        txtUpdateStatusInit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtUpdateStatusInitKeyPressed(evt);
            }
        });

        txtUpdateStatusEnd.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtUpdateStatusEnd.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtUpdateStatusEndMouseClicked(evt);
            }
        });
        txtUpdateStatusEnd.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtUpdateStatusEndKeyPressed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Ingresa el estado inicial y final.");

        cmbUpdateCurrentStatus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbUpdateCurrentStatus.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        cmbUpdateChangeStatus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbUpdateChangeStatus.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        lblInfoStatusChange.setFont(new java.awt.Font("Arial", 0, 9)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cmbUpdateCurrentStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(cmbUpdateChangeStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtUpdateStatusInit, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtUpdateStatusEnd, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE))
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblInfoStatusChange, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbUpdateCurrentStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbUpdateChangeStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtUpdateStatusInit, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUpdateStatusEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblInfoStatusChange, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtUpdateTypeInit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtUpdateTypeInit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtUpdateTypeInitMouseClicked(evt);
            }
        });
        txtUpdateTypeInit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtUpdateTypeInitKeyPressed(evt);
            }
        });

        txtUpdateTypeEnd.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtUpdateTypeEnd.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtUpdateTypeEndMouseClicked(evt);
            }
        });
        txtUpdateTypeEnd.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtUpdateTypeEndKeyPressed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Ingresa el tipo inicial y final.");

        cmbUpdateCurrentType.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbUpdateCurrentType.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        cmbUpdateChangeType.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbUpdateChangeType.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        lblInfoTypeChange.setFont(new java.awt.Font("Arial", 0, 9)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(cmbUpdateCurrentType, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(cmbUpdateChangeType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtUpdateTypeInit, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtUpdateTypeEnd, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE))
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblInfoTypeChange, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbUpdateCurrentType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbUpdateChangeType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtUpdateTypeInit, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUpdateTypeEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblInfoTypeChange, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtDeliveryInitDate, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtDeliveryEndDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtEventInitDate, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtEventEndDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtCreatedInitDate, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCreatedEndDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtCustomer, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbStatus, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbDriver, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbEventType, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbLimit, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnApply, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbDriver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbEventType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDeliveryInitDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDeliveryEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEventInitDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEventEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCreatedInitDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCreatedEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(cmbLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnApply))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCustomerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCustomerActionPerformed

    private void txtDeliveryInitDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDeliveryInitDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDeliveryInitDateMouseClicked

    private void txtDeliveryInitDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDeliveryInitDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDeliveryInitDateKeyPressed

    private void txtDeliveryEndDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDeliveryEndDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDeliveryEndDateMouseClicked

    private void txtDeliveryEndDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDeliveryEndDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDeliveryEndDateKeyPressed

    private void txtEventInitDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtEventInitDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEventInitDateMouseClicked

    private void txtEventInitDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEventInitDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEventInitDateKeyPressed

    private void txtEventEndDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtEventEndDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEventEndDateMouseClicked

    private void txtEventEndDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEventEndDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEventEndDateKeyPressed

    private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyActionPerformed
        try {
           
           final String FORMAT_DATE = "dd/MM/yyyy"; 
           final String FORMAT_DATE_QUERY = "yyyy-MM-dd"; 
           Usuario chofer = (Usuario) cmbDriver.getModel().getSelectedItem(); 
           EstadoEvento estadoEvento = (EstadoEvento) cmbStatus.getModel().getSelectedItem();
           EstadoEvento currentStatus = (EstadoEvento) cmbUpdateCurrentStatus.getModel().getSelectedItem();
           EstadoEvento changeStatus = (EstadoEvento) cmbUpdateChangeStatus.getModel().getSelectedItem();
           Tipo eventType = (Tipo) cmbEventType.getModel().getSelectedItem();
           Tipo changeType = (Tipo) cmbUpdateChangeType.getModel().getSelectedItem();
           Tipo currentType = (Tipo) cmbUpdateCurrentType.getModel().getSelectedItem();
           Integer limit = Integer.parseInt(cmbLimit.getSelectedItem().toString());
           String customer = txtCustomer.getText();
           String initDeliveryDate = txtDeliveryInitDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtDeliveryInitDate.getDate()) : null;
           String endDeliveryDate = txtDeliveryEndDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtDeliveryEndDate.getDate()) : null;
           String initEventDate = txtEventInitDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtEventInitDate.getDate()) : null;
           String endEventDate = txtEventEndDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtEventEndDate.getDate()) : null;       
           String initCreatedDate = txtCreatedInitDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtCreatedInitDate.getDate()) : null;
           String endCreatedDate = txtCreatedEndDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtCreatedEndDate.getDate()) : null;
           String orderStatusChangeInitDate = txtUpdateStatusInit.getDate() != null ? new SimpleDateFormat(FORMAT_DATE_QUERY).format(txtUpdateStatusInit.getDate()) + " 00:00:01" : null;
           String orderStatusChangeEndDate = txtUpdateStatusEnd.getDate() != null ? new SimpleDateFormat(FORMAT_DATE_QUERY).format(txtUpdateStatusEnd.getDate()) + " 23:59:59" : null;
           String orderTypeChangeInitDate = txtUpdateTypeInit.getDate() != null ? new SimpleDateFormat(FORMAT_DATE_QUERY).format(txtUpdateTypeInit.getDate()) + " 00:00:01" : null;
           String orderTypeChangeEndDate = txtUpdateTypeEnd.getDate() != null ? new SimpleDateFormat(FORMAT_DATE_QUERY).format(txtUpdateTypeEnd.getDate()) + " 23:59:59" : null;
           
           Map<String, Object> parameters = new HashMap<>();
           parameters.put("initCreatedDate", initCreatedDate);
           parameters.put("endCreatedDate", endCreatedDate);
           parameters.put("limit", limit);
           parameters.put("type", eventType.getTipoId());
           parameters.put("customer", customer);
           parameters.put("initDeliveryDate", initDeliveryDate);
           parameters.put("endDeliveryDate", endDeliveryDate);
           parameters.put("initEventDate", initEventDate);
           parameters.put("endEventDate", endEventDate);
           parameters.put("statusId", estadoEvento.getEstadoId());
           parameters.put("driverId", chofer.getUsuarioId());
           parameters.put("currentStatusId", currentStatus.getEstadoId());
           parameters.put("changeStatusId", changeStatus.getEstadoId());
           parameters.put("orderStatusChangeInitDate", orderStatusChangeInitDate );
           parameters.put("orderStatusChangeEndDate", orderStatusChangeEndDate);
           
           parameters.put("currentTypeId", currentType.getTipoId());
           parameters.put("changeTypeId", changeType.getTipoId());
           parameters.put("orderTypeChangeInitDate", orderTypeChangeInitDate );
           parameters.put("orderTypeChangeEndDate", orderTypeChangeEndDate);
           
           ConsultarRentas.tabla_consultar_renta(parameters);
           this.dispose();
           
        } catch (Exception e) {
           JOptionPane.showMessageDialog(null, "Ocurrió un error inesperado\n "+e, "Error", JOptionPane.ERROR_MESSAGE);  
        }
    }//GEN-LAST:event_btnApplyActionPerformed

    private void txtCreatedInitDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCreatedInitDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCreatedInitDateMouseClicked

    private void txtCreatedInitDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCreatedInitDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCreatedInitDateKeyPressed

    private void txtCreatedEndDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCreatedEndDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCreatedEndDateMouseClicked

    private void txtCreatedEndDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCreatedEndDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCreatedEndDateKeyPressed

    private void txtUpdateStatusEndMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtUpdateStatusEndMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUpdateStatusEndMouseClicked

    private void txtUpdateStatusEndKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUpdateStatusEndKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUpdateStatusEndKeyPressed

    private void txtUpdateStatusInitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtUpdateStatusInitMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUpdateStatusInitMouseClicked

    private void txtUpdateStatusInitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUpdateStatusInitKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUpdateStatusInitKeyPressed

    private void txtUpdateTypeInitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtUpdateTypeInitMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUpdateTypeInitMouseClicked

    private void txtUpdateTypeInitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUpdateTypeInitKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUpdateTypeInitKeyPressed

    private void txtUpdateTypeEndMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtUpdateTypeEndMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUpdateTypeEndMouseClicked

    private void txtUpdateTypeEndKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUpdateTypeEndKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUpdateTypeEndKeyPressed

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FiltersConsultarRentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FiltersConsultarRentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FiltersConsultarRentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FiltersConsultarRentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                FiltersConsultarRentas dialog = new FiltersConsultarRentas(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApply;
    private javax.swing.JComboBox<Usuario> cmbDriver;
    private javax.swing.JComboBox<Tipo> cmbEventType;
    private javax.swing.JComboBox<String> cmbLimit;
    private javax.swing.JComboBox<EstadoEvento> cmbStatus;
    private javax.swing.JComboBox<EstadoEvento> cmbUpdateChangeStatus;
    private javax.swing.JComboBox<Tipo> cmbUpdateChangeType;
    private javax.swing.JComboBox<EstadoEvento> cmbUpdateCurrentStatus;
    private javax.swing.JComboBox<Tipo> cmbUpdateCurrentType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblInfoStatusChange;
    private javax.swing.JLabel lblInfoTypeChange;
    private com.toedter.calendar.JDateChooser txtCreatedEndDate;
    private com.toedter.calendar.JDateChooser txtCreatedInitDate;
    private javax.swing.JTextField txtCustomer;
    private com.toedter.calendar.JDateChooser txtDeliveryEndDate;
    private com.toedter.calendar.JDateChooser txtDeliveryInitDate;
    private com.toedter.calendar.JDateChooser txtEventEndDate;
    private com.toedter.calendar.JDateChooser txtEventInitDate;
    private com.toedter.calendar.JDateChooser txtUpdateStatusEnd;
    private com.toedter.calendar.JDateChooser txtUpdateStatusInit;
    private com.toedter.calendar.JDateChooser txtUpdateTypeEnd;
    private com.toedter.calendar.JDateChooser txtUpdateTypeInit;
    // End of variables declaration//GEN-END:variables
}
