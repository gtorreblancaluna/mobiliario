package mobiliario;

import forms.inventario.InventarioForm;
import clases.conectate;
import clases.sqlclass;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Categoria extends java.awt.Dialog {

    sqlclass funcion = new sqlclass();
    conectate conexion = new conectate();
    Object[][] dtconduc;
    boolean existe, editar = false;
    String id_categoria;

    public Categoria(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        tabla_categorias();
        jbtn_guardar.setEnabled(false);
        txt_buscar.requestFocus();
        this.setLocationRelativeTo(null);
    }

    public void tabla_categorias() {
        funcion.conectate();
        tabla_categorias.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        String[] columNames = {"Id", "Descripcion"};
        String[] colName = {"id_categoria", "descripcion"};
        //nombre de columnas, tabla, instruccion sql  
        try {       
            dtconduc = funcion.GetTabla(colName, "categoria", "SELECT * FROM categoria ORDER BY descripcion");
        } catch (SQLNonTransientConnectionException e) {
            funcion.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }
        
        DefaultTableModel datos = new DefaultTableModel(dtconduc, columNames);
        tabla_categorias.setModel(datos);
        

        int[] anchos = {10, 200};

        for (int inn = 0; inn < tabla_categorias.getColumnCount(); inn++) {
            tabla_categorias.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        tabla_categorias.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla_categorias.getColumnModel().getColumn(0).setMinWidth(0);
        tabla_categorias.getColumnModel().getColumn(0).setPreferredWidth(0);

        funcion.desconecta();
    }

    public void tabla_categorias_like() {
        funcion.conectate();
        tabla_categorias.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        String[] columNames = {"Id", "Descripcion"};
        String[] colName = {"id_categoria", "descripcion"};
        //nombre de columnas, tabla, instruccion sql        
        try {       
             dtconduc = funcion.GetTabla(colName, "categoria", "SELECT * FROM categoria WHERE descripcion like '%" + txt_buscar.getText() + "%' ");
        } catch (SQLNonTransientConnectionException e) {
            funcion.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }
       
        DefaultTableModel datos = new DefaultTableModel(dtconduc, columNames);
        tabla_categorias.setModel(datos);

        int[] anchos = {10, 200};

        for (int inn = 0; inn < tabla_categorias.getColumnCount(); inn++) {
            tabla_categorias.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        tabla_categorias.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla_categorias.getColumnModel().getColumn(0).setMinWidth(0);
        tabla_categorias.getColumnModel().getColumn(0).setPreferredWidth(0);

        funcion.desconecta();
    }

    public void agregar() {

        if (txt_categoria.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Faltan parametros", "Error", JOptionPane.INFORMATION_MESSAGE);
        } else {
            existe = false;
            for (int i = 0; i < tabla_categorias.getRowCount(); i++) {
                if (txt_categoria.getText().toString().equals(tabla_categorias.getValueAt(i, 1).toString())) {
                    existe = true;
                    break;
                }
            }
            if (existe == true) {
                JOptionPane.showMessageDialog(null, "No se permiten duplicados ", "Error de duplicidad", JOptionPane.INFORMATION_MESSAGE);

            } else {
                try {
                    funcion.conectate();
                    String datos[] = {txt_categoria.getText()};
                    
                    funcion.InsertarRegistro(datos, "insert into categoria (descripcion) values(?)");
                    
                    tabla_categorias();
                    funcion.desconecta();
                    txt_categoria.setText("");
                    txt_categoria.requestFocus();
                } catch (SQLException ex) {
                    Logger.getLogger(Categoria.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, "Error al insertar registro ", "Error", JOptionPane.ERROR);
                }
            }
        }
    }

    public void guardar() {
        if (txt_categoria.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "No puede ir vacio...", "Error", JOptionPane.INFORMATION_MESSAGE);
        } else {
            funcion.conectate();

            String datos[] = {txt_categoria.getText().toString(), id_categoria};

            try {       
             funcion.UpdateRegistro(datos, "update categoria set descripcion=? where id_categoria=?");
            } catch (SQLNonTransientConnectionException e) {
                funcion.conectate();
                JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo "+e, "Error", JOptionPane.ERROR_MESSAGE); 
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
            }
            

            txt_categoria.setText("");
            txt_categoria.requestFocus();
            jbtn_guardar.setEnabled(false);
            jbtn_agregar.setEnabled(true);
            tabla_categorias();
            funcion.desconecta();
            editar = false;

            //jbtn_agregar.setEnabled(false);
        }
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
        tabla_categorias = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txt_categoria = new javax.swing.JTextField();
        txt_buscar = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        setLocationRelativeTo(jLabel1);
        setTitle("Categorias");
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

        tabla_categorias.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        tabla_categorias.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tabla_categorias);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 40, 240, 240));

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel1.setText("Categoria:");
        jLabel1.setToolTipText("");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, -1, -1));

        txt_categoria.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        txt_categoria.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_categoriaKeyPressed(evt);
            }
        });
        jPanel1.add(txt_categoria, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 32, 110, -1));

        jPanel2.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 130, 270));

        txt_buscar.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        txt_buscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_buscarKeyReleased(evt);
            }
        });
        jPanel2.add(txt_buscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 10, 200, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/search-icon.png"))); // NOI18N
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 10, -1, -1));

        add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        InventarioForm.validar_categorias = true;
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

    private void txt_categoriaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_categoriaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10 && editar == false) {
            agregar();
        } else if (evt.getKeyCode() == 10 && editar == true) {
            guardar();
        }
    }//GEN-LAST:event_txt_categoriaKeyPressed

    private void jbtn_editarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_editarActionPerformed
        // TODO add your handling code here:
        if (tabla_categorias.getSelectedRow() != -1) {
            editar = true;
            jbtn_guardar.setEnabled(true);
            jbtn_agregar.setEnabled(false);

            id_categoria = tabla_categorias.getValueAt(tabla_categorias.getSelectedRow(), 0).toString();
            this.txt_categoria.setText(String.valueOf(tabla_categorias.getValueAt(tabla_categorias.getSelectedRow(), 1)));
            txt_categoria.requestFocus();

        } else {
            JOptionPane.showMessageDialog(null, "Selecciona una fila para editar", "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jbtn_editarActionPerformed

    private void jbtn_nuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_nuevoActionPerformed
        // TODO add your handling code here:
        txt_categoria.setText("");
        txt_categoria.requestFocus();
        jbtn_agregar.setEnabled(true);
        jbtn_guardar.setEnabled(false);
    }//GEN-LAST:event_jbtn_nuevoActionPerformed

    private void txt_buscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_buscarKeyReleased
        // TODO add your handling code here:
        tabla_categorias_like();
    }//GEN-LAST:event_txt_buscarKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Categoria dialog = new Categoria(new java.awt.Frame(), true);
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton jbtn_agregar;
    private javax.swing.JButton jbtn_editar;
    private javax.swing.JButton jbtn_guardar;
    private javax.swing.JButton jbtn_nuevo;
    private javax.swing.JTable tabla_categorias;
    private javax.swing.JTextField txt_buscar;
    private javax.swing.JTextField txt_categoria;
    // End of variables declaration//GEN-END:variables
}
