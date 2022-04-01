package mobiliario;

import forms.inventario.InventarioForm;
import forms.rentas.ConsultarRentas;
import services.SaleService;
import clases.sqlclass;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import model.querys.AvailabilityItemResult;
import services.ItemService;
import services.SystemService;

public class VerDisponibilidadArticulos extends java.awt.Dialog {

    sqlclass funcion = new sqlclass();
    
    Object[][] dtconduc;
    boolean existe, editar = false;
    String id_color;
    float cant = 0; 
    private final SaleService saleService;
    private final SystemService systemService = SystemService.getInstance();
    
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
    
    private enum HeaderTableUnicos {
    
        ITEM_ID(0),
        ORDER_AMOUNT(1),
        ITEM_UTILES(2),
        ITEM_DISPONIBLE(3),
        ITEM_DESCRIPTION(4);
        
        private final Integer column;

        public Integer getColumn() {
            return column;
        }

        private HeaderTableUnicos(Integer column) {
            this.column = column;
        }
        
    }
    
    private enum HeaderTable {
    
        ITEM_ID(0),
        ORDER_AMOUNT(1),
        ITEM_UTILES(2),
        ITEM_DESCRIPTION(3),
        ORDER_DELIVERY_DATE(4),
        ORDER_DELIVERY_HOUR(5),
        ORDER_RETURN_DATE(6),
        ORDER_RETURN_HOUR(7),
        ORDER_CUSTOMER(8),
        ORDER_FOLIO(9),
        ORDER_DESCRIPTION(10),
        ORDER_TYPE(11),
        ORDER_STATUS(12);
        
        private final Integer column;

        public Integer getColumn() {
            return column;
        }

        private HeaderTable(Integer column) {
            this.column = column;
        }
        
    }
    
    public VerDisponibilidadArticulos(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        
        initComponents();    
        saleService = SaleService.getInstance();
        funcion.conectate();
        this.setLocationRelativeTo(null);
        this.lblEncontrados.setText("");
        this.setTitle("Ver disponibilidad de articulos ");
        formato_tabla();
        formato_tabla_unicos();
      
        try {
           mostrarDisponibilidad();
        } catch (Exception e) {
            System.out.println(e);
        }
        
        System.out.println("MOSTRAR DISPONIBILIDAD SUCCESS");
        
    }
   
    
    
    public void mostrarSoloNegativosTablaUnicos(){
        
         for(int j=tablaArticulosUnicos.getRowCount() - 1 ; j >=0 ; j--){
             float disponible = Float.parseFloat(tablaArticulosUnicos.getValueAt(j, HeaderTableUnicos.ITEM_DISPONIBLE.getColumn()).toString());
             if(disponible >= 0){
                 DefaultTableModel temp = (DefaultTableModel) tablaArticulosUnicos.getModel();
                 temp.removeRow(j);
             }
         }
    
    }

    public void mostrarDisponibilidad(){       
        
        String fechaInicial = new SimpleDateFormat("dd/MM/yyyy").format(InventarioForm.txtDisponibilidadFechaInicial.getDate());
        String fechaFinal = new SimpleDateFormat("dd/MM/yyyy").format(InventarioForm.txtDisponibilidadFechaFinal.getDate());
        StringBuilder mensaje = new StringBuilder();
        Map<String,Object> parameters = new HashMap<>();
        
        if(InventarioForm.radioBtnFechaEntrega.isSelected()){
            // MOSTRAR POR FECHA DE ENTREGA
            parameters.put("showByDeliveryDate", true);
             mensaje.append("Se incluyen por fecha de entrega - ");
        }
        
        if(InventarioForm.radioBtnFechaDevolucion.isSelected()){
            // MOSTRAR POR FECHA DE DEVOLUCION
            parameters.put("showByReturnDate", true);
            mensaje.append("Se incluyen por fecha devolucion - ");
        }
        
        List<AvailabilityItemResult> availabilityItemResults;
        
        parameters.put("typePedido", ApplicationConstants.TIPO_PEDIDO);
        parameters.put("statusApartado", ApplicationConstants.ESTADO_APARTADO);
        parameters.put("statusEnRenta", ApplicationConstants.ESTADO_EN_RENTA);
        parameters.put("initDate", fechaInicial);
        parameters.put("endDate", fechaFinal);
        
        try {
            availabilityItemResults = saleService.obtenerDisponibilidadRentaPorConsulta(parameters);
        } catch (Exception e) {
            Logger.getLogger(ConsultarRentas.class.getName()).log(Level.SEVERE, null, e);
            JOptionPane.showMessageDialog(null, "Ocurrio un inesperado\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
            return;
        }
        
        
        if(availabilityItemResults == null || availabilityItemResults.size()<=0){
            lblEncontrados.setText("No se obtuvieron resultados :(");
            return;
        }
        
        
        
         mensaje.append("Total de folios "+availabilityItemResults.size()+" - ");
          for(AvailabilityItemResult availabilityItemResult : availabilityItemResults){
            
              String id = availabilityItemResult.getItem().getArticuloId()+"";
                
              boolean itemFound = false;
                if (!InventarioForm.jcheckIncluirTodos.isSelected() ) {
                    for (int i = 0; i < InventarioForm.tablaDisponibilidadArticulos.getRowCount(); i++) {
                        if (id.equals(InventarioForm.tablaDisponibilidadArticulos.getValueAt(i, 0).toString())) {
                            itemFound = true;
                            break;
                        }
                    }
                }
                
                if (!InventarioForm.jcheckIncluirTodos.isSelected() && !itemFound) {
                    continue;
                }
                                    
                  
                    // vamos agregar el articulo encontrado en la tabla detalle
                    DefaultTableModel temp = (DefaultTableModel) tablaArticulos.getModel();
                     Object nuevo[] = {
                         
                            availabilityItemResult.getItem().getArticuloId()+"",
                            availabilityItemResult.getNumberOfItems(),
                            // mostrar utiles
                            availabilityItemResult.getItem().getUtiles(),
                            availabilityItemResult.getItem().getDescripcion()+" "+availabilityItemResult.getItem().getColor().getColor(),
                            availabilityItemResult.getEventDateOrder(),
                            availabilityItemResult.getDeliveryDateOrder(),
                            availabilityItemResult.getDeliveryHourOrder(),
                            availabilityItemResult.getReturnDateOrder(),
                            availabilityItemResult.getReturnHourOrder(),
                            availabilityItemResult.getCustomerName(),
                            availabilityItemResult.getFolioOrder(),
                            availabilityItemResult.getDescriptionOrder(),
                            availabilityItemResult.getTypeOrder(),
                            availabilityItemResult.getStatusOrder()
                        };
                        temp.addRow(nuevo);

                        if(this.tablaArticulosUnicos.getRowCount() == 0){
                            // es la primer agregado, procedemos a agregar
                            
                            DefaultTableModel tablaUnicosModel = (DefaultTableModel) tablaArticulosUnicos.getModel();
                            Object unico[] = {
                                availabilityItemResult.getItem().getArticuloId()+"", // 0
                                availabilityItemResult.getNumberOfItems(), // 1
                                availabilityItemResult.getItem().getUtiles(), // 2
                                "", // 3
                                availabilityItemResult.getItem().getDescripcion()+" "+availabilityItemResult.getItem().getColor().getColor() //4
                            };
                            tablaUnicosModel.addRow(unico);
                        }else{
                            // recorremos la tabla para encontrar algun articulo y sumar y calcular la disponibilidad
                            boolean encontrado = false;
                            for(int j=0 ; j < tablaArticulosUnicos.getRowCount() ; j++){
                                
                                if(tablaArticulosUnicos.getValueAt(j, HeaderTableUnicos.ITEM_ID.getColumn()).toString().equals(availabilityItemResult.getItem().getArticuloId()+"") ){
                                    // articulo encontrado :)
                                    float cantidadPedido = Float.parseFloat(tablaArticulosUnicos.getValueAt(j, HeaderTableUnicos.ORDER_AMOUNT.getColumn()).toString());
                                    tablaArticulosUnicos.setValueAt((cantidadPedido + availabilityItemResult.getNumberOfItems()), j, HeaderTableUnicos.ORDER_AMOUNT.getColumn());
                                    encontrado = true;
                                }
                            } // end for tablaArticulosUnicos, para realizar la busqueda
                            
                            if(!encontrado){
                                // si no se encontro en la tabla, procedemos a agregar el articulo
                                DefaultTableModel tablaUnicosModel = (DefaultTableModel) tablaArticulosUnicos.getModel();
                                Object unico1[] = {
                                    availabilityItemResult.getItem().getArticuloId()+"",
                                    availabilityItemResult.getNumberOfItems(),
                                    availabilityItemResult.getItem().getUtiles(),
                                    "",                           
                                    availabilityItemResult.getItem().getDescripcion()+" "+availabilityItemResult.getItem().getColor().getColor()
                                };
                                tablaUnicosModel.addRow(unico1);
                             } // fin if, encontrado!
                        
                  
              }               
          
        }    // en for renta  
          
           
          // calculando la disponibilidad para la tabla unicos
          for(int j=0 ; j < tablaArticulosUnicos.getRowCount() ; j++){
                float pedidos = Float.parseFloat(tablaArticulosUnicos.getValueAt(j, HeaderTableUnicos.ORDER_AMOUNT.getColumn()).toString());
                float utiles = Float.parseFloat(tablaArticulosUnicos.getValueAt(j, HeaderTableUnicos.ITEM_UTILES.getColumn()).toString());
                tablaArticulosUnicos.setValueAt( ( utiles - pedidos ), j, HeaderTableUnicos.ITEM_DISPONIBLE.getColumn());
                
           }
          
          if(InventarioForm.check_solo_negativos.isSelected()) {
                mensaje.append("Mostrando solo los negativos - ");
                mostrarSoloNegativosTablaUnicos();
           }
          
            this.lblEncontrados.setText(mensaje.toString());
          
    }// en funcion mostrarDisponibilidad
     
    public void formato_tabla() {
        Object[][] data = {{"","","", "", "", "", "", "", "","","","",""}};
        String[] columnNames = {"id_articulo", "cantidad pedido", "Utiles", "articulo", "fecha evento","fecha entrega","hora entrega", "fecha_devolucion","hora devoluci\u00F3n" ,"cliente","folio","descripci\u00F3n evento","tipo","estado"};
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        tablaArticulos.setModel(tableModel);
        
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
        tablaArticulos.setRowSorter(ordenarTabla);

        int[] anchos = {20, 80, 80, 160,100, 100, 100, 80,240,80,80,80,80,80};

        for (int inn = 0; inn < tablaArticulos.getColumnCount(); inn++) {
            tablaArticulos.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) tablaArticulos.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        tablaArticulos.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaArticulos.getColumnModel().getColumn(0).setMinWidth(0);
        tablaArticulos.getColumnModel().getColumn(0).setPreferredWidth(0);

        tablaArticulos.getColumnModel().getColumn(1).setCellRenderer(centrar);
        tablaArticulos.getColumnModel().getColumn(2).setCellRenderer(centrar);

    }
     
     public void formato_tabla_unicos() {
        
         Object[][] data = {{"", "", "", "", ""}};
        String[] columnNames = {"id_articulo", "Cantidad pedido", "Utiles","disponible", "articulo"};
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        tablaArticulosUnicos.setModel(tableModel);
        
        // Instanciamos el TableRowSorter y lo añadimos al JTable
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
        tablaArticulosUnicos.setRowSorter(ordenarTabla);

        int[] anchos = {20, 60, 60, 60,120};

        for (int inn = 0; inn < tablaArticulosUnicos.getColumnCount(); inn++) {
            tablaArticulosUnicos.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) tablaArticulosUnicos.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        tablaArticulosUnicos.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaArticulosUnicos.getColumnModel().getColumn(0).setMinWidth(0);
        tablaArticulosUnicos.getColumnModel().getColumn(0).setPreferredWidth(0);

        tablaArticulosUnicos.getColumnModel().getColumn(1).setCellRenderer(centrar);
        tablaArticulosUnicos.getColumnModel().getColumn(2).setCellRenderer(centrar);
        tablaArticulosUnicos.getColumnModel().getColumn(3).setCellRenderer(centrar);
        tablaArticulosUnicos.getColumnModel().getColumn(4).setCellRenderer(centrar);

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
        lblEncontrados = new javax.swing.JLabel();
        jtbPaneVerDetalle = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaArticulos = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaArticulosUnicos = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        jToolBar1 = new javax.swing.JToolBar();
        jbtnExportarDetalle = new javax.swing.JButton();
        jbtnExportarUnicos = new javax.swing.JButton();

        setLocationRelativeTo(lblEncontrados);
        setTitle("Colores");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblEncontrados.setText("jLabel1");
        jPanel2.add(lblEncontrados, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 590, 530, -1));

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

        jtbPaneVerDetalle.addTab("Detalle", jScrollPane1);

        tablaArticulosUnicos.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        tablaArticulosUnicos.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaArticulosUnicos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaArticulosUnicosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tablaArticulosUnicos);

        jtbPaneVerDetalle.addTab("Unicos", jScrollPane2);

        jPanel2.add(jtbPaneVerDetalle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 1170, 560));

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jbtnExportarDetalle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/excel-icon.png"))); // NOI18N
        jbtnExportarDetalle.setToolTipText("Exportar tabla detalle a Excel");
        jbtnExportarDetalle.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtnExportarDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnExportarDetalleActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtnExportarDetalle);

        jbtnExportarUnicos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/excel-icon.png"))); // NOI18N
        jbtnExportarUnicos.setToolTipText("Exportar tabla unicos a Excel");
        jbtnExportarUnicos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtnExportarUnicos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnExportarUnicosActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtnExportarUnicos);

        jPanel2.add(jToolBar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 590, 120, 40));

        add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
//        InventarioForm.validar_colores = true;
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    private void tablaArticulosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaArticulosMouseClicked
        
    }//GEN-LAST:event_tablaArticulosMouseClicked

    private void tablaArticulosUnicosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaArticulosUnicosMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tablaArticulosUnicosMouseClicked

    private void jbtnExportarDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExportarDetalleActionPerformed
        // TODO add your handling code here:
        systemService.exportarExcel(tablaArticulos);
    }//GEN-LAST:event_jbtnExportarDetalleActionPerformed

    private void jbtnExportarUnicosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExportarUnicosActionPerformed
        // TODO add your handling code here:
        systemService.exportarExcel(tablaArticulosUnicos);
    }//GEN-LAST:event_jbtnExportarUnicosActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                VerDisponibilidadArticulos dialog = new VerDisponibilidadArticulos(new java.awt.Frame(), true);
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
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton jbtnExportarDetalle;
    private javax.swing.JButton jbtnExportarUnicos;
    private javax.swing.JTabbedPane jtbPaneVerDetalle;
    private javax.swing.JLabel lblEncontrados;
    private javax.swing.JTable tablaArticulos;
    private javax.swing.JTable tablaArticulosUnicos;
    // End of variables declaration//GEN-END:variables
}
