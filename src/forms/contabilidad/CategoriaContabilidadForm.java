/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms.contabilidad;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import mobiliario.ApplicationConstants;
import forms.inventario.InventarioForm;
import model.CategoriaContabilidad;
import services.ContabilidadServices;


/**
 *
 * @author Carlos Alberto
 */
public class CategoriaContabilidadForm extends java.awt.Dialog {
    private ContabilidadServices contabilidadService = new ContabilidadServices();
    private String g_id_categoria=null;

    /**
     * Creates new form Colores
     */
    public CategoriaContabilidadForm(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        formato_tabla();
        tabla_categorias();
        jbtn_guardar.setEnabled(false);
        txt_buscar.requestFocus();
        this.setLocationRelativeTo(null);
    }
   
    public void tabla_categorias() {
        
        List<CategoriaContabilidad> list = new ArrayList<>();
        list = contabilidadService.getAllCategoriasContabilidad();
        
        if(list == null || list.isEmpty() || list.size()<=0)
            return;
        for(CategoriaContabilidad categoria : list){
         DefaultTableModel temp = (DefaultTableModel) tabla_categorias.getModel();
         Object fila[] = {
             categoria.getCategoriaContabilidadId(),
             categoria.getDescripcion(),
             categoria.getFechaRegistro()
         };
         temp.addRow(fila); 
        } // end for
       
    }

    public void tabla_categorias_like() {
       
    }

    public void agregar() {
        // insertar
        if(this.txt_categoria.getText().toString().equals(""))
        {
            JOptionPane.showMessageDialog(null, "ingresa una descripcion ", "Error", JOptionPane.INFORMATION_MESSAGE);
            return ;
        }
        CategoriaContabilidad isCategory = 
                contabilidadService.getCategoryByName(txt_categoria.getText().toString());
        if(isCategory!=null)
        {
            JOptionPane.showMessageDialog(null, "Error , ya existe una categoria con ese nombre ", "Error", JOptionPane.INFORMATION_MESSAGE);
            return ;
        }
        
        CategoriaContabilidad category = new CategoriaContabilidad();
        category.setDescripcion(this.txt_categoria.getText().toString());
        
        contabilidadService.saveCategory(category);
      
        JOptionPane.showMessageDialog(null, ApplicationConstants.MESSAGE_SAVE_SUCCESSFUL, "Sucessful", JOptionPane.INFORMATION_MESSAGE);
        formato_tabla();
        tabla_categorias();
        this.txt_categoria.setText("");
    }
    
    public void formato_tabla(){
     Object[][] data = {{"", "", ""}};
        String[] columnNames = {"id_categoria_contabilidad", "descripcion", "fecha_registro"};
        DefaultTableModel TableModel = new DefaultTableModel(data, columnNames);
        tabla_categorias.setModel(TableModel);

        int[] anchos = {20, 100, 100};

        for (int inn = 0; inn < tabla_categorias.getColumnCount(); inn++) {
            tabla_categorias.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) tabla_categorias.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        tabla_categorias.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla_categorias.getColumnModel().getColumn(0).setMinWidth(0);
        tabla_categorias.getColumnModel().getColumn(0).setPreferredWidth(0);

        tabla_categorias.getColumnModel().getColumn(1).setCellRenderer(centrar);
        tabla_categorias.getColumnModel().getColumn(2).setCellRenderer(centrar);
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

    public void actualizar(){
       String data = this.txt_categoria.getText().toString();
       if(data == null || data.equals(""))
       {
          JOptionPane.showMessageDialog(null, "No puede ir vacio", "Error", JOptionPane.INFORMATION_MESSAGE);
          return ;
       }
       
        CategoriaContabilidad isCategory = 
                contabilidadService.getCategoryByName(txt_categoria.getText().toString());
        if(isCategory!=null)
        {
            JOptionPane.showMessageDialog(null, "Error , ya existe una categoria con ese nombre ", "Error", JOptionPane.INFORMATION_MESSAGE);
            return ;
        }
       
       CategoriaContabilidad categoriaContabilidad = new CategoriaContabilidad();
       categoriaContabilidad.setCategoriaContabilidadId(Integer.parseInt(g_id_categoria));
       categoriaContabilidad.setDescripcion(txt_categoria.getText().toString());
       
       contabilidadService.updateCategory(categoriaContabilidad);
       
       JOptionPane.showMessageDialog(null, ApplicationConstants.MESSAGE_UPDATE_SUCCESSFUL, "Sucess update", JOptionPane.INFORMATION_MESSAGE);
       txt_categoria.setText("");
        txt_categoria.requestFocus();
        jbtn_guardar.setEnabled(false);
        jbtn_agregar.setEnabled(true);
        formato_tabla();
        tabla_categorias();
    }
    
    private void jbtn_guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_guardarActionPerformed
        // TODO add your handling code here:
        actualizar();
       
    }//GEN-LAST:event_jbtn_guardarActionPerformed

    private void txt_categoriaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_categoriaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10 ) {
            agregar();
        } else if (evt.getKeyCode() == 10) {
            
        }
    }//GEN-LAST:event_txt_categoriaKeyPressed

    private void editar(){
        
        jbtn_guardar.setEnabled(true);
        jbtn_agregar.setEnabled(false);

        g_id_categoria = this.tabla_categorias.getValueAt(tabla_categorias.getSelectedRow(), 0).toString();
        this.txt_categoria.setText(String.valueOf(tabla_categorias.getValueAt(tabla_categorias.getSelectedRow(), 1)));
        txt_categoria.requestFocus();
    
    }
    
    private void jbtn_editarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_editarActionPerformed
        // TODO add your handling code here:
        if (tabla_categorias.getSelectedRow() != -1) {
           editar();

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
                CategoriaContabilidadForm dialog = new CategoriaContabilidadForm(new java.awt.Frame(), true);
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
