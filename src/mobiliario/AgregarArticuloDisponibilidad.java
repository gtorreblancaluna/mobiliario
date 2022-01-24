/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobiliario;

import clases.conectate;
import clases.sqlclass;
import java.awt.Toolkit;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import static forms.rentas.AgregarRenta.tabla_articulos;
import static mobiliario.inventario.cmb_color;
import model.Articulo;
import services.ItemService;

/**
 *
 * @author Carlos Alberto
 */
public class AgregarArticuloDisponibilidad extends java.awt.Dialog {

    sqlclass funcion = new sqlclass();   
    ItemService itemService = ItemService.getInstance();
//    conectate conexion = new conectate();
    Object[][] dtconduc;
    boolean existe, editar = false;
    String id_color;
    float cant = 0; 
   
    
    public String conviertemoneda(String valor) {

        DecimalFormatSymbols simbolo = new DecimalFormatSymbols();
        simbolo.setDecimalSeparator('.');
        simbolo.setGroupingSeparator(',');

        float entero = Float.parseFloat(valor);
        DecimalFormat formateador = new DecimalFormat("###,###.##", simbolo);
        String entero2 = formateador.format(entero);

        if (entero2.contains(".")) {
            entero2 = "$" + entero2;

        } else {
            entero2 = "$" + entero2 + ".00";
        }

        return entero2;

    }
    public AgregarArticuloDisponibilidad(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();       
        funcion.conectate();
        txt_buscar.requestFocus();
        
        this.setLocationRelativeTo(null);
        this.lblEncontrados.setText("");
        this.setTitle("Buscar articulo ");
        formato_tabla();
        
    }
    
    public void formato_tabla() {
        Object[][] data = {{"", "", "", "", ""}};
        String[] columnNames = {"Id", "Categoria", "Descripcion", "Color", "P.Unitario", "Stock"};       
        DefaultTableModel TableModel = new DefaultTableModel(data, columnNames);
        tablaArticulos.setModel(TableModel);

         int[] anchos = {10, 120, 250, 100, 90, 90};

        for (int inn = 0; inn < tablaArticulos.getColumnCount(); inn++) {
            tablaArticulos.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        DefaultTableCellRenderer TablaRenderer = new DefaultTableCellRenderer();
        TablaRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            DefaultTableModel temp = (DefaultTableModel) tablaArticulos.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
//        tablaArticulos.getColumnModel().getColumn(1).setMaxWidth(0);
//        tablaArticulos.getColumnModel().getColumn(1).setMinWidth(0);
//        tablaArticulos.getColumnModel().getColumn(1).setPreferredWidth(0);

        

    }

     public void tabla_articulos_like() {
        
        tablaArticulos.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        String[] columNames = {"Id", "Categoria", "Descripcion", "Color", "P.Unitario", "Stock"};
        String[] colName = {"id_articulo", "categoria", "descripcion", "color", "precio_renta", "cantidad"};
        //nombre de columnas, tabla, instruccion sql        

//        String querySql = "SELECT a.id_articulo, ca.descripcion as categoria, a.descripcion, c.color, a.precio_renta,a.cantidad "
//                + "FROM articulo a, color c, categoria ca "
//                +"+INNER JOIN color color ON (color.id_color = a.id_color) "
//                + "WHERE a.activo =1 AND a.id_categoria=ca.id_categoria "
//                + "AND a.descripcion like '%" + txt_buscar.getText().toString() + "%' ";
       
       try {       
            dtconduc = funcion.GetTabla(colName, "articulo", "SELECT a.`id_articulo`, ca.`descripcion` as categoria, a.`descripcion`, c.`color`, a.`precio_renta`,a.`cantidad` "
                   + "FROM articulo a, color c, categoria ca\n"
                   + "WHERE a.id_color=c.id_color and activo =1 AND a.id_categoria=ca.id_categoria "
                   + "AND a.`descripcion` like '%" + txt_buscar.getText().toString() + "%' "
                   +" AND a.activo=1 "
           );
        } catch (SQLNonTransientConnectionException e) {
            funcion.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }
       
//        List<Articulo> articulos = itemService.obtenerArticulos(funcion,querySql);

        if(dtconduc!= null && dtconduc.length > 0)
            this.lblEncontrados.setText("Articulos encontrados: "+dtconduc.length);
        else
            this.lblEncontrados.setText("Sin resultados :( ");
       
        
        for (int i = 0; i < dtconduc.length; i++) {
            String valor = dtconduc[i][4].toString();
            dtconduc[i][4] = conviertemoneda(valor).toString();
            System.out.println(conviertemoneda(valor));

        }

        DefaultTableModel datos = new DefaultTableModel(dtconduc, columNames);
        DefaultTableCellRenderer TablaRenderer = new DefaultTableCellRenderer();
        TablaRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        tablaArticulos.setModel(datos);

        int[] anchos = {10, 120, 250, 100, 90, 90};

        for (int inn = 0; inn < tablaArticulos.getColumnCount(); inn++) {
            tablaArticulos.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        tablaArticulos.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaArticulos.getColumnModel().getColumn(0).setMinWidth(0);
        tablaArticulos.getColumnModel().getColumn(0).setPreferredWidth(0);

        //tablaArticulos.getColumnModel().getColumn(4).setCellRenderer(TablaRenderer);
        tablaArticulos.getColumnModel().getColumn(5).setCellRenderer(centrar);

        tablaArticulos.getColumnModel().getColumn(4).setCellRenderer(centrar);


    }


    public void agregar() {
        
    }

    public void guardar() {
      
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaArticulos = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        txt_buscar = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        lblEncontrados = new javax.swing.JLabel();

        setLocationRelativeTo(lblEncontrados);
        setTitle("Colores");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tablaArticulos.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        tablaArticulos.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaArticulos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaArticulosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaArticulos);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 680, 240));

        txt_buscar.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        txt_buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_buscarActionPerformed(evt);
            }
        });
        txt_buscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_buscarKeyReleased(evt);
            }
        });
        jPanel2.add(txt_buscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 630, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/search-icon.png"))); // NOI18N
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        lblEncontrados.setText("jLabel1");
        jPanel2.add(lblEncontrados, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 290, 680, -1));

        add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
//        inventario.validar_colores = true;
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    private void txt_buscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_buscarKeyReleased
        // TODO add your handling code here:
           tabla_articulos_like();
    }//GEN-LAST:event_txt_buscarKeyReleased

    private void txt_buscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_buscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_buscarActionPerformed

    private void tablaArticulosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaArticulosMouseClicked
         if (evt.getClickCount() == 2) {
             String idArticulo = null;
             idArticulo = tablaArticulos.getValueAt(tablaArticulos.getSelectedRow(), 0).toString();
            if (idArticulo == null || idArticulo.equals("")) {
                JOptionPane.showMessageDialog(null, "Ocurrio un error inesperado, porfavor intentalo de nuevo, si el problema sigue, reinicia el sistema :P ", "Error", JOptionPane.INFORMATION_MESSAGE);
                Toolkit.getDefaultToolkit().beep();
             }else{
               if(inventario.agregarArticulo(idArticulo))
                   this.dispose();
                 
            }
         }
    }//GEN-LAST:event_tablaArticulosMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AgregarArticuloDisponibilidad dialog = new AgregarArticuloDisponibilidad(new java.awt.Frame(), true);
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblEncontrados;
    private javax.swing.JTable tablaArticulos;
    private javax.swing.JTextField txt_buscar;
    // End of variables declaration//GEN-END:variables
}
