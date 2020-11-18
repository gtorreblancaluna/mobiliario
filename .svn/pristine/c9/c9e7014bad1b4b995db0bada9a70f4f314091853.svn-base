/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms.tipo.abonos.cuentas;

import clases.conectate;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import mobiliario.ApplicationConstants;
import mobiliario.agregar_renta;
import model.Cuenta;
import model.TipoAbono;
import services.TipoAbonosService;
import services.AccountService;

/**
 *
 * @author Carlos Alberto
 */
public class TiposAbonosForm extends java.awt.Dialog {

    
    conectate conexion = new conectate();
    Object[][] dtconduc;
    boolean existe, editar = false;
    TipoAbonosService abonosService = new TipoAbonosService();
    static AccountService accountService = new AccountService();
    String id_tipo_abono;

    /**
     * Creates new form Colores
     */
    public TiposAbonosForm(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        jbtn_guardar.setEnabled(false);
        txt_tipo_abono.requestFocus();
        this.setLocationRelativeTo(null);
        
        this.llenar_combo_cuentas();
        this.tabla_tipo_abonos();
    }
    
    public static void llenar_combo_cuentas(){
        
        List<Cuenta> list = accountService.getAccounts();
        
        cmbCuenta.removeAllItems();
        cmbCuenta.addItem(ApplicationConstants.CMB_SELECCIONE);
        if(list== null)
            return;
        for(Cuenta cuenta : list){
            cmbCuenta.addItem(cuenta.getDescripcion());
        }
    }
    
    public void mostrar_cuentas_form(){
        CuentasBancariasForm ventana = new CuentasBancariasForm(null, true);
        ventana.setVisible(true);
        ventana.setLocationRelativeTo(null);
    
    }
public void formato_tabla(){
     Object[][] data = {{"", "", "","",""}};
        String[] columnNames = {"id", "Descripción", "Cuenta","cuenta_id","Registro"};
        DefaultTableModel TableModel = new DefaultTableModel(data, columnNames);
        tabla.setModel(TableModel);

        int[] anchos = {20, 400, 300,20,300};

        for (int inn = 0; inn < tabla.getColumnCount(); inn++) {
            tabla.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) tabla.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
//        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
//        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        tabla.getColumnModel().getColumn(3).setMaxWidth(0);
        tabla.getColumnModel().getColumn(3).setMinWidth(0);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(0);

//        tabla.getColumnModel().getColumn(1).setCellRenderer(centrar);
//        tabla.getColumnModel().getColumn(2).setCellRenderer(centrar);
    }
    public void tabla_tipo_abonos() {
        this.formato_tabla();
       List<TipoAbono> list = abonosService.getAbonos();
        if(list == null || list.isEmpty() || list.size()<=0)
            return;
        
        for(TipoAbono tipo : list){
         DefaultTableModel temp = (DefaultTableModel) tabla.getModel();
         Object fila[] = {
             tipo.getTipoAbonoId(),
             tipo.getDescripcion(),
             tipo.getCuenta().getDescripcion(),
             tipo.getCuenta().getId(),             
             tipo.getFechaRegistro()
         };
         temp.addRow(fila); 
        } // end for
    }

    public void tabla_tipo_abonos_like() {
        this.formato_tabla();
        String search = txt_buscar.getText().toString();
         List<TipoAbono> list = abonosService.getAbonosLike(search);
        if(list == null || list.isEmpty() || list.size()<=0)
            return;
        
        for(TipoAbono tipo : list){
         DefaultTableModel temp = (DefaultTableModel) tabla.getModel();
         Object fila[] = {
             tipo.getTipoAbonoId(),
             tipo.getDescripcion(),
             tipo.getCuenta().getDescripcion(),
             tipo.getCuenta().getId(),             
             tipo.getFechaRegistro()
         };
         temp.addRow(fila); 
        } // end for
        
    }

    public void agregar() {
        
        String tipoAbono = txt_tipo_abono.getText().toString();
        String cuenta = this.cmbCuenta.getSelectedItem().toString();
        
        if( (tipoAbono == null || tipoAbono.equals("")) 
             || (cuenta == null || cuenta.equals(ApplicationConstants.CMB_SELECCIONE)) )
        {
            JOptionPane.showMessageDialog(null, ApplicationConstants.MESSAGE_MISSING_PARAMETERS, "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if(abonosService.getTipoAbonoByDescription(tipoAbono) != null)
        {
         JOptionPane.showMessageDialog(null, "No se permiten duplicados ", "Error de duplicidad", JOptionPane.INFORMATION_MESSAGE);
         return;
        }
//        for (int i = 0; i < tabla.getRowCount(); i++) {
//            if (tipoAbono.equals(tabla.getValueAt(i, 1).toString())) {
//               JOptionPane.showMessageDialog(null, "No se permiten duplicados ", "Error de duplicidad", JOptionPane.INFORMATION_MESSAGE);
//                return;
//            }
//        }
       Cuenta account = accountService.getAccountByDescription(cuenta);
       if(account == null)
       {
        JOptionPane.showMessageDialog(null, ApplicationConstants.MESSAGE_NOT_PARAMETER_RECEIVED, "Error", JOptionPane.INFORMATION_MESSAGE);
        return;
       }
       TipoAbono tAbono = new TipoAbono();
       tAbono.setDescripcion(tipoAbono);
       tAbono.setCuenta(account);
       abonosService.insert(tAbono);
       
       this.tabla_tipo_abonos();
       this.limpiar();
        
    }

    public void guardar() {
       
            JOptionPane.showMessageDialog(null, "Funcion no disponible :(", "Error", JOptionPane.INFORMATION_MESSAGE);
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jbtn_nuevo = new javax.swing.JButton();
        jbtn_agregar = new javax.swing.JButton();
        jbtn_editar = new javax.swing.JButton();
        jbtn_guardar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        jPanel1 = new javax.swing.JPanel();
        txt_tipo_abono = new javax.swing.JTextField();
        lblDescripcion = new javax.swing.JLabel();
        cmbCuenta = new javax.swing.JComboBox();
        lblDescripcion1 = new javax.swing.JLabel();
        lblAgregarCuenta = new javax.swing.JLabel();
        txt_buscar = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        setTitle("Tipos de pago");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar1.setRollover(true);

        jbtn_nuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Folder-New-Folder-icon.png"))); // NOI18N
        jbtn_nuevo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_nuevo.setFocusable(false);
        jbtn_nuevo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_nuevo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_nuevoActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtn_nuevo);

        jbtn_agregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Document-Add-icon.png"))); // NOI18N
        jbtn_agregar.setToolTipText("Agregar");
        jbtn_agregar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_agregar.setFocusable(false);
        jbtn_agregar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_agregar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_agregarActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtn_agregar);

        jbtn_editar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Actions-edit-icon.png"))); // NOI18N
        jbtn_editar.setToolTipText("Editar");
        jbtn_editar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_editar.setFocusable(false);
        jbtn_editar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_editar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_editar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_editarActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtn_editar);

        jbtn_guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/floppy-disk-icon.png"))); // NOI18N
        jbtn_guardar.setToolTipText("Guardar");
        jbtn_guardar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_guardar.setFocusable(false);
        jbtn_guardar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_guardar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_guardarActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtn_guardar);

        jPanel2.add(jToolBar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 280));

        tabla.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tabla);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 60, 540, 240));

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt_tipo_abono.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_tipo_abono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_tipo_abonoKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_tipo_abonoKeyReleased(evt);
            }
        });
        jPanel1.add(txt_tipo_abono, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 180, -1));

        lblDescripcion.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        lblDescripcion.setText("Cuenta:");
        jPanel1.add(lblDescripcion, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 90, -1));

        cmbCuenta.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbCuenta.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbCuenta.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbCuenta.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbCuentaItemStateChanged(evt);
            }
        });
        cmbCuenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCuentaActionPerformed(evt);
            }
        });
        jPanel1.add(cmbCuenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 190, 20));

        lblDescripcion1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        lblDescripcion1.setText("Tipo abono:");
        jPanel1.add(lblDescripcion1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 90, -1));

        lblAgregarCuenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Add-icon.png"))); // NOI18N
        lblAgregarCuenta.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblAgregarCuenta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAgregarCuentaMouseClicked(evt);
            }
        });
        lblAgregarCuenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lblAgregarCuentaKeyPressed(evt);
            }
        });
        jPanel1.add(lblAgregarCuenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 60, -1, -1));

        jPanel2.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 190, 140));

        txt_buscar.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        txt_buscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_buscarKeyReleased(evt);
            }
        });
        jPanel2.add(txt_buscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 30, 200, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/search-icon.png"))); // NOI18N
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 30, -1, -1));

        add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        agregar_renta.validad_tipo_abonos = true;
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    private void jbtn_agregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_agregarActionPerformed
        // TODO add your handling code here:

        agregar();
    }//GEN-LAST:event_jbtn_agregarActionPerformed

    private void jbtn_guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_guardarActionPerformed
        // TODO add your handling code here:
        guardar();
    }//GEN-LAST:event_jbtn_guardarActionPerformed

    private void txt_tipo_abonoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_tipo_abonoKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10 && editar == false) {
            agregar();
        } else if (evt.getKeyCode() == 10 && editar == true) {
            guardar();
        }
    }//GEN-LAST:event_txt_tipo_abonoKeyPressed

    private void jbtn_editarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_editarActionPerformed
        // TODO add your handling code here:
        if (tabla.getSelectedRow() != -1) {
            editar = true;
            jbtn_guardar.setEnabled(true);
            jbtn_agregar.setEnabled(false);

            id_tipo_abono = tabla.getValueAt(tabla.getSelectedRow(), 0).toString();
            this.txt_tipo_abono.setText(String.valueOf(tabla.getValueAt(tabla.getSelectedRow(), 1)));
            txt_tipo_abono.requestFocus();

        } else {
            JOptionPane.showMessageDialog(null, "Selecciona una fila para editar", "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jbtn_editarActionPerformed
    
    public void limpiar(){
        txt_tipo_abono.setText("");
        txt_tipo_abono.requestFocus();
        jbtn_agregar.setEnabled(true);
        jbtn_guardar.setEnabled(false);
        this.llenar_combo_cuentas();
    }
    
    private void jbtn_nuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_nuevoActionPerformed
        // TODO add your handling code here:
        
        limpiar();
       
    }//GEN-LAST:event_jbtn_nuevoActionPerformed

    private void txt_buscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_buscarKeyReleased
        // TODO add your handling code here:
        tabla_tipo_abonos_like();
    }//GEN-LAST:event_txt_buscarKeyReleased

    private void txt_tipo_abonoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_tipo_abonoKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_txt_tipo_abonoKeyReleased

    private void cmbCuentaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbCuentaItemStateChanged
        // TODO add your handling code here:

    }//GEN-LAST:event_cmbCuentaItemStateChanged

    private void cmbCuentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCuentaActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_cmbCuentaActionPerformed

    private void lblAgregarCuentaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAgregarCuentaMouseClicked
        // TODO add your handling code here:
        this.mostrar_cuentas_form();
    }//GEN-LAST:event_lblAgregarCuentaMouseClicked

    private void lblAgregarCuentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lblAgregarCuentaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_lblAgregarCuentaKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                TiposAbonosForm dialog = new TiposAbonosForm(new java.awt.Frame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JComboBox cmbCuenta;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton jbtn_agregar;
    private javax.swing.JButton jbtn_editar;
    private javax.swing.JButton jbtn_guardar;
    private javax.swing.JButton jbtn_nuevo;
    private javax.swing.JLabel lblAgregarCuenta;
    private javax.swing.JLabel lblDescripcion;
    private javax.swing.JLabel lblDescripcion1;
    private javax.swing.JTable tabla;
    private javax.swing.JTextField txt_buscar;
    private javax.swing.JTextField txt_tipo_abono;
    // End of variables declaration//GEN-END:variables
}
