
package forms.rentas;

import clases.sqlclass;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import mobiliario.ApplicationConstants;
import model.EstadoEvento;
import model.Tipo;
import model.Usuario;
import services.EstadoEventoService;
import services.TipoEventoService;
import services.UserService;


public class FiltersConsultarRentas extends javax.swing.JDialog {

    private final UserService userService = UserService.getInstance();
    private final EstadoEventoService estadoEventoService = EstadoEventoService.getInstance();
    private final TipoEventoService tipoEventoService = TipoEventoService.getInstance();
    private final sqlclass funcion;


    public FiltersConsultarRentas(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        funcion = new sqlclass();
        funcion.conectate();
        initInfo();
    }
    
    private void initInfo () {
        
        List<EstadoEvento> list = estadoEventoService.get();
        cmbStatus.removeAllItems();
        cmbStatus.addItem(
                new EstadoEvento(0, ApplicationConstants.CMB_SELECCIONE)
        );
        list.stream().forEach(t -> {
            cmbStatus.addItem(t);
        });
        
        
        List<Tipo> types = tipoEventoService.get();
        cmbEventType.removeAllItems();
        
        cmbEventType.addItem(
                new Tipo(0, ApplicationConstants.CMB_SELECCIONE)
        );
        types.stream().forEach(t -> {
            cmbEventType.addItem(t);
        });
        
        
        List<Usuario> users = userService.obtenerUsuarios(funcion);
        cmbDriver.removeAllItems();
        
        cmbDriver.addItem(
                new Usuario(0, ApplicationConstants.CMB_SELECCIONE)
        );
        users.stream().forEach(t -> {
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCustomer)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbDriver, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbEventType, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmbLimit, 0, 180, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnApply, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtCreatedInitDate, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCreatedEndDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbDriver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbEventType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addComponent(btnApply)
                .addContainerGap())
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
           Usuario chofer = (Usuario) cmbDriver.getModel().getSelectedItem(); 
           EstadoEvento estadoEvento = (EstadoEvento) cmbStatus.getModel().getSelectedItem();
           Tipo eventType = (Tipo) cmbEventType.getModel().getSelectedItem();
           Integer limit = Integer.parseInt(cmbLimit.getSelectedItem().toString());
           String customer = txtCustomer.getText();
           String initDeliveryDate = txtDeliveryInitDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtDeliveryInitDate.getDate()) : null;
           String endDeliveryDate = txtDeliveryEndDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtDeliveryEndDate.getDate()) : null;
           String initEventDate = txtEventInitDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtEventInitDate.getDate()) : null;
           String endEventDate = txtEventEndDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtEventEndDate.getDate()) : null;       
           String initCreatedDate = txtCreatedInitDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtCreatedInitDate.getDate()) : null;
           String endCreatedDate = txtCreatedEndDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtCreatedEndDate.getDate()) : null;
           
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
           
           ConsultarRentas.tabla_consultar_renta(parameters);
           this.dispose();
           
        } catch (Exception e) {
           JOptionPane.showMessageDialog(null, "Ocurrio un inesperado\n "+e, "Error", JOptionPane.ERROR_MESSAGE);  
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private com.toedter.calendar.JDateChooser txtCreatedEndDate;
    private com.toedter.calendar.JDateChooser txtCreatedInitDate;
    private javax.swing.JTextField txtCustomer;
    private com.toedter.calendar.JDateChooser txtDeliveryEndDate;
    private com.toedter.calendar.JDateChooser txtDeliveryInitDate;
    private com.toedter.calendar.JDateChooser txtEventEndDate;
    private com.toedter.calendar.JDateChooser txtEventInitDate;
    // End of variables declaration//GEN-END:variables
}
