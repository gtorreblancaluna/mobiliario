
package forms.socialMediaContact;

import common.constants.ApplicationConstants;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import common.model.CatalogSocialMediaContactModel;
import common.services.CatalogSocialMediaContactService;
import common.tables.TableCatalogSocialMediaContact;
import common.utilities.UtilityCommon;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;


public class AddCatalogSocialMediaFormDialog extends javax.swing.JDialog {

    private boolean successfulChangesDetected = false;
    private final CatalogSocialMediaContactService catalogSocialMediaContactService 
            = CatalogSocialMediaContactService.getInstance();
    
    private final TableCatalogSocialMediaContact tableCatalogSocialMediaContact;
    private Integer idToUpdate;
    
    
    public AddCatalogSocialMediaFormDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);

        initComponents();
        init();
        tableCatalogSocialMediaContact = new TableCatalogSocialMediaContact();
        UtilityCommon.addJtableToPane(950, 400, panelTable, tableCatalogSocialMediaContact);
        fillTable();
        addEventListenerTable();
        this.setTitle("Catalogo Medio de contacto.");
        addEscapeListener();
        
    }
    
    // close dialog when esc is pressed.
    private void addEscapeListener() {
        ActionListener escListener = (ActionEvent e) -> {
            setVisible(false);
            dispose();
        };

        this.getRootPane().registerKeyboardAction(escListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

    }
    
    private void addEventListenerTable () {
    
        tableCatalogSocialMediaContact.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table =(JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    
                    String description = 
                            table.getValueAt(table.getSelectedRow(), 
                                    TableCatalogSocialMediaContact.Column.DESCRIPTION.getNumber()).toString();
                    
                    String id = 
                            table.getValueAt(table.getSelectedRow(), 
                                    TableCatalogSocialMediaContact.Column.ID.getNumber()).toString();
                    
                    idToUpdate = Integer.parseInt(id);

                    txtDescription.setText(description);
                    txtDescription.selectAll();

                    btnSave.setEnabled(true);
                    txtDescription.requestFocus();
                }
            }
        });
    }
    
    private void delete () {
        
        try {
               List<String> ids = UtilityCommon.getIdsSelected(tableCatalogSocialMediaContact, 
                       TableCatalogSocialMediaContact.Column.BOOLEAN.getNumber(),
                       TableCatalogSocialMediaContact.Column.ID.getNumber());
               
               if (ids.isEmpty()) {
                   throw new BusinessException("Selecciona uno o mas elementos.");
               }
               
               if (ids.size() > 20) {
                   throw new BusinessException("Límite excedido [20].");
               }
               
                int seleccion = JOptionPane.showOptionDialog(this, 
                        "Elementos a eliminar: [" + ids.size() +"], (Esta acción no se puede deshacer). ¿Deseas continuar?", "ATENCIÓN", 
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
                //presiono que no
                if (seleccion != 0) {
                    return;
                }               

                for (String id : ids) {
                    CatalogSocialMediaContactModel model = 
                            new CatalogSocialMediaContactModel();
                    model.setId(Long.parseLong(id));
                    model.setFgActive("0");
                    catalogSocialMediaContactService.saveOrUpdate(model);
                }
                fillTable();
              
        } catch (BusinessException | DataOriginException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);   
        }
    
    }
    
    private void edit () {
        try {
               List<String> ids = UtilityCommon.getIdsSelected(tableCatalogSocialMediaContact, 
                       TableCatalogSocialMediaContact.Column.BOOLEAN.getNumber(),
                       TableCatalogSocialMediaContact.Column.ID.getNumber());
               
               if (ids.isEmpty() || ids.size() > 1) {
                   throw new BusinessException("Selecciona un checkbox de la tabla.");
               }
               
               idToUpdate = Integer.parseInt(ids.get(0));
               
               String valueToModify = UtilityCommon.getIdSelected(tableCatalogSocialMediaContact, 
                       TableCatalogSocialMediaContact.Column.BOOLEAN.getNumber(),
                       TableCatalogSocialMediaContact.Column.DESCRIPTION.getNumber());
               
               txtDescription.setText(valueToModify);
               
               btnSave.setEnabled(true);
               txtDescription.requestFocus();
        
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);   
        }
    }

    
    private void save () {
        
        if (txtDescription.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa un valor.", 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        
        if (!txtDescription.getText().isEmpty() && txtDescription.getText().length() > 400 ) {
            JOptionPane.showMessageDialog(this, "Límite de caracteres excedido [400].", 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        
        CatalogSocialMediaContactModel model = 
                new CatalogSocialMediaContactModel();
        
        model.setDescription(txtDescription.getText().trim());
        if (idToUpdate != null) {
            model.setId(Long.parseLong(String.valueOf(idToUpdate)));
        }
        
        try {
            catalogSocialMediaContactService.saveOrUpdate(model);        
            init();
            successfulChangesDetected = true;
            fillTable();
        } catch (DataOriginException e) {
            JOptionPane.showMessageDialog(this, e, 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
            Toolkit.getDefaultToolkit().beep();
            successfulChangesDetected = false;
        }
    }
    
    private void fillTable () {
        
        tableCatalogSocialMediaContact.format();
        
        try {
            List<CatalogSocialMediaContactModel> list 
                = catalogSocialMediaContactService.getAll();
            
            DefaultTableModel tableModel = (DefaultTableModel) tableCatalogSocialMediaContact.getModel();
            
            for (CatalogSocialMediaContactModel catalog : list) {
                Object row[] = {
                    false,
                    catalog.getId(),
                    catalog.getDescription(),
                    catalog.getCreatedAt(),
                    catalog.getUpdatedAt()
                };
              tableModel.addRow(row);
            }
        } catch (DataOriginException e) {
            JOptionPane.showMessageDialog(this, e, 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
            Toolkit.getDefaultToolkit().beep();
            successfulChangesDetected = false;
        }
    }
    
    private void init () {
        txtDescription.setText("");
        txtDescription.requestFocus();
        btnSave.setEnabled(false);
        idToUpdate = null;
    }
    
    public Boolean showDialog () {
        setVisible(true);
        return successfulChangesDetected;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnUpdate = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        txtDescription = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        panelTable = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        btnUpdate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnUpdate.setText("Modificar");
        btnUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnSave.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnSave.setText("Guardar");
        btnSave.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnDelete.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnDelete.setText("Eliminar");
        btnDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnUpdate))
                    .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnUpdate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelete)
                .addContainerGap(359, Short.MAX_VALUE))
        );

        txtDescription.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDescriptionKeyPressed(evt);
            }
        });

        jLabel1.setText("Descripcion:");

        javax.swing.GroupLayout panelTableLayout = new javax.swing.GroupLayout(panelTable);
        panelTable.setLayout(panelTableLayout);
        panelTableLayout.setHorizontalGroup(
            panelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelTableLayout.setVerticalGroup(
            panelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 2, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(387, 387, 387))
                    .addComponent(panelTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtDescription))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        edit();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        save();        
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        delete();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void txtDescriptionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescriptionKeyPressed
        if (evt.getKeyCode() == 10) {
            save();
        }
    }//GEN-LAST:event_txtDescriptionKeyPressed

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
            java.util.logging.Logger.getLogger(AddCatalogSocialMediaFormDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AddCatalogSocialMediaFormDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AddCatalogSocialMediaFormDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AddCatalogSocialMediaFormDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AddCatalogSocialMediaFormDialog dialog = new AddCatalogSocialMediaFormDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel panelTable;
    private javax.swing.JTextField txtDescription;
    // End of variables declaration//GEN-END:variables
}
