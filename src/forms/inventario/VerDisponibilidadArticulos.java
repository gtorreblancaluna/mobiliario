package forms.inventario;

import services.SaleService;
import common.constants.ApplicationConstants;
import common.services.UtilityService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import model.querys.AvailabilityItemResult;


public final class VerDisponibilidadArticulos extends java.awt.Dialog {

    private final SaleService saleService;
    private UtilityService utilityService;
    
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
        ORDER_EVENT_DATE(4),
        ORDER_ELABORATION_DATE(5),
        ORDER_DELIVERY_DATE(6),
        ORDER_DELIVERY_HOUR(7),
        ORDER_RETURN_DATE(8),
        ORDER_RETURN_HOUR(9),
        ORDER_CUSTOMER(10),
        ORDER_FOLIO(11),
        ORDER_DESCRIPTION(12),
        ORDER_TYPE(13),
        ORDER_STATUS(14);
        
        private final Integer column;

        public Integer getColumn() {
            return column;
        }

        private HeaderTable(Integer column) {
            this.column = column;
        }
        
    }
    
   
    public VerDisponibilidadArticulos(
            java.awt.Frame parent, //0
            boolean modal,//1
            String initialDate,//2
            String endDate,//3
            Boolean showOnlyNegatives,//4
            Boolean showByDeliveryDate, //5
            Boolean showByReturnDate,//6
            List<Long> filterByItems,//7
            List<AvailabilityItemResult> itemsFromNewFolio,//8
            // incluir la renta id
            Long includeRentaId//9
    ) {
        super(parent, modal);
        initComponents();    
        saleService = SaleService.getInstance();
        this.setLocationRelativeTo(null);
        this.lblEncontrados.setText("");
        this.setTitle("Ver disponibilidad de articulos ");
        formato_tabla();
        formato_tabla_unicos();
        
        if (itemsFromNewFolio != null && !itemsFromNewFolio.isEmpty()) {
            for (AvailabilityItemResult availabilityItemResult : itemsFromNewFolio) {
                addAvailabilityItemResultToDetailTable(availabilityItemResult);
                addAvailabilityItemResultToTableUniques(availabilityItemResult);
            }
        }
        
        executeMainProccess(
                initialDate,//0
                endDate,//1
                showOnlyNegatives,//2
                showByDeliveryDate,//3
                showByReturnDate,//4
                filterByItems,//5
                includeRentaId//6
        );
        
    }
   
    private void mostrarSoloNegativosTablaUnicos(){
        
         for(int j=tablaArticulosUnicos.getRowCount() - 1 ; j >=0 ; j--){
             float disponible = Float.parseFloat(tablaArticulosUnicos.getValueAt(j, HeaderTableUnicos.ITEM_DISPONIBLE.getColumn()).toString());
             if(disponible >= 0){
                 DefaultTableModel temp = (DefaultTableModel) tablaArticulosUnicos.getModel();
                 temp.removeRow(j);
             }
         }
    
    }

    private void addAvailabilityItemResultToTableUniques (AvailabilityItemResult availabilityItemResult) {
        DefaultTableModel tablaUnicosModel = (DefaultTableModel) tablaArticulosUnicos.getModel();
        Object unico[] = {
            availabilityItemResult.getItem().getArticuloId()+"", // 0
            availabilityItemResult.getNumberOfItems(), // 1
            availabilityItemResult.getItem().getUtiles(), // 2
            "", // 3
            availabilityItemResult.getItem().getDescripcion()+" "+availabilityItemResult.getItem().getColor().getColor() //4
        };
        tablaUnicosModel.addRow(unico);
    }
    
    private void addAvailabilityItemResultToDetailTable (AvailabilityItemResult availabilityItemResult) {
    
        DefaultTableModel temp = (DefaultTableModel) tablaArticulos.getModel();
        Object nuevo[] = {

               availabilityItemResult.getItem().getArticuloId()+"",
               availabilityItemResult.getNumberOfItems(),
               // mostrar utiles
               availabilityItemResult.getItem().getUtiles(),
               availabilityItemResult.getItem().getDescripcion()+" "+availabilityItemResult.getItem().getColor().getColor(),
               availabilityItemResult.getEventDateOrder(),
               availabilityItemResult.getEventDateElaboration(),
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
    }
    
    private void executeMainProccess(
            String initialDate,//0
            String endDate,//1
            Boolean showOnlyNegatives,//2
            Boolean showByDeliveryDate,//3
            Boolean showByReturnDate,//4
            List<Long> filterByItems,//5
            Long includeRentaId//6
    ){       
        
        StringBuilder mensaje = new StringBuilder();
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("includeRentaId", includeRentaId);
        // MOSTRAR POR FECHA DE ENTREGA
        parameters.put("showByDeliveryDate", showByDeliveryDate);
        
        // MOSTRAR POR FECHA DE DEVOLUCION
        parameters.put("showByReturnDate", showByReturnDate);
        
        if (showByDeliveryDate) {
            mensaje.append("Se incluyen por fecha de entrega -");
        } else if (showByReturnDate) {
            mensaje.append("Se incluyen por fecha de devolucion -");
        } else {
            mensaje.append("Se incluyen por fecha de entrega y fecha de devolucion -");
        }
        
        
        List<AvailabilityItemResult> availabilityItemResults;
        
        parameters.put("tipo_pedido_id", ApplicationConstants.TIPO_PEDIDO);
        parameters.put("estado_apartado_id", ApplicationConstants.ESTADO_APARTADO);
        parameters.put("estado_en_renta_id", ApplicationConstants.ESTADO_EN_RENTA);
        parameters.put("initDate", initialDate);
        parameters.put("endDate", endDate);
        if (!filterByItems.isEmpty())
            parameters.put("filterByItems", filterByItems);
        
        try {
            availabilityItemResults = saleService.obtenerDisponibilidadRentaPorConsulta(parameters);
        } catch (Exception e) {
            Logger.getLogger(VerDisponibilidadArticulos.class.getName()).log(Level.SEVERE, null, e);
            JOptionPane.showMessageDialog(null, "Ocurrio un inesperado\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
            return;
        }        
        
        
        if(availabilityItemResults == null || availabilityItemResults.isEmpty()){
            lblEncontrados.setText("No se obtuvieron ordenes en las fechas indicadas, fecha de entrega: "+initialDate+", fecha devolución: "+endDate);
            calculateAvailableInTable();
            return;
        }
        
          
         for(AvailabilityItemResult availabilityItemResult : availabilityItemResults){
            
                // vamos agregar el articulo encontrado en la tabla detalle
                addAvailabilityItemResultToDetailTable(availabilityItemResult);

                if(this.tablaArticulosUnicos.getRowCount() == 0){
                    // es la primer agregado, procedemos a agregar
                    addAvailabilityItemResultToTableUniques(availabilityItemResult);
                } else {
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
                        addAvailabilityItemResultToTableUniques(availabilityItemResult);
                     } 
                }            
          
        }// en for renta  

        calculateAvailableInTable();


        if(showOnlyNegatives) {
          mensaje.append("Mostrando solo los negativos - ");
          mostrarSoloNegativosTablaUnicos();
        }
        mensaje.append("Total de articulos: ").append(tablaArticulos.getRowCount()).append(" - ");
        mensaje.append("Folios únicos: ").append(getDistictByFolioFromTable()).append(" - ");
        this.lblEncontrados.setText(mensaje.toString());
          
    }// en funcion mostrarDisponibilidad
    
    private void calculateAvailableInTable () {
                // calculando la disponibilidad para la tabla unicos
        for(int j=0 ; j < tablaArticulosUnicos.getRowCount() ; j++){
          float pedidos = Float.parseFloat(tablaArticulosUnicos.getValueAt(j, HeaderTableUnicos.ORDER_AMOUNT.getColumn()).toString());
          float utiles = Float.parseFloat(tablaArticulosUnicos.getValueAt(j, HeaderTableUnicos.ITEM_UTILES.getColumn()).toString());
          tablaArticulosUnicos.setValueAt( ( utiles - pedidos ), j, HeaderTableUnicos.ITEM_DISPONIBLE.getColumn());
        }
    }
    
    private int getDistictByFolioFromTable () {
        Set<String> distinctFolios = new HashSet<>();
        for (int i = 0 ; i < tablaArticulos.getRowCount() ; i++) {
            distinctFolios.add(tablaArticulos.getValueAt(i, HeaderTable.ORDER_FOLIO.getColumn()).toString());
        }
        return distinctFolios.size();
    }
     
    public void formato_tabla() {
        Object[][] data = {{"","","", "", "", "", "", "", "","","","","",""}};
        String[] columnNames = {"id_articulo", "cantidad pedido", "Utiles", "articulo", "fecha evento","fecha elaboración","fecha entrega","hora entrega", "fecha_devolucion","hora devoluci\u00F3n" ,"cliente","folio","descripci\u00F3n evento","tipo","estado"};
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        tablaArticulos.setModel(tableModel);
        
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<>(tableModel); 
        tablaArticulos.setRowSorter(ordenarTabla);

        int[] anchos = {20, 80, 80, 160,100, 100,100, 100, 80,240,80,80,80,80,80};

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
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<>(tableModel); 
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

        lblEncontrados.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblEncontrados.setForeground(new java.awt.Color(204, 0, 51));
        lblEncontrados.setText("jLabel1");
        jPanel2.add(lblEncontrados, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 590, 1050, 20));

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
        jbtnExportarDetalle.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jbtnExportarDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnExportarDetalleActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtnExportarDetalle);

        jbtnExportarUnicos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/excel-icon.png"))); // NOI18N
        jbtnExportarUnicos.setToolTipText("Exportar tabla unicos a Excel");
        jbtnExportarUnicos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
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
        utilityService = UtilityService.getInstance();
        utilityService.exportarExcel(tablaArticulos);
    }//GEN-LAST:event_jbtnExportarDetalleActionPerformed

    private void jbtnExportarUnicosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExportarUnicosActionPerformed
        
        utilityService = UtilityService.getInstance();
        utilityService.exportarExcel(tablaArticulosUnicos);
    }//GEN-LAST:event_jbtnExportarUnicosActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                VerDisponibilidadArticulos dialog = new VerDisponibilidadArticulos(new java.awt.Frame(), true, "", "",false, false, false, null, null, null);
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
