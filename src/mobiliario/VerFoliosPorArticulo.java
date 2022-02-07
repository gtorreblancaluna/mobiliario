/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobiliario;

import forms.inventario.InventarioForm;
import services.SaleService;
import clases.sqlclass;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import model.DetalleRenta;
import model.Faltante;
import model.Renta;
import services.ItemService;
import services.SystemService;

/**
 *
 * @author Gerardo Torreblanca
 * Ventana que mostrara los folios donde se encuentra un articulo
 */
public class VerFoliosPorArticulo extends java.awt.Dialog {

    sqlclass funcion = new sqlclass();
    
    Object[][] dtconduc;
    boolean existe, editar = false;
    String id_color;
    float cant = 0; 
    private final SaleService saleService;
    private final SystemService systemService = SystemService.getInstance();
    ItemService itemService = ItemService.getInstance();
    public static String g_rentaId;
    public static int g_articuloId;
   
    
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
     public void mostrar_faltantes() {
        VerFaltantes ventana_faltantes = new VerFaltantes(null, true);
        ventana_faltantes.setVisible(true);
        ventana_faltantes.setLocationRelativeTo(null);
    }
    public VerFoliosPorArticulo(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        
        initComponents();
        saleService = SaleService.getInstance();
        funcion.conectate();
        this.setLocationRelativeTo(null);
        this.lblEncontrados.setText("");
        this.setTitle("Folios por articulo");
        formato_tabla();
        tabla_articulos_inicio();       
        this.llenar_combo_estado();
        this.llenar_combo_tipo();
        this.llenar_tabla_articulos_faltantes();
        
    }

    
     public void formato_tabla() {
        Object[][] data = {{"","","", "", "", "", "",""}};
        String[] columnNames = {"id_articulo", "id_renta", "folio","codigo articulo", "articulo", "cantidad pedido", "tipo evento", "estado evento"};
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        tablaArticulos.setModel(tableModel);
        
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
        tablaArticulos.setRowSorter(ordenarTabla);
        
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        int[] anchos = {20,20,20,20,100,40,60,60};

        for (int inn = 0; inn < tablaArticulos.getColumnCount(); inn++){
            tablaArticulos.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
            tablaArticulos.getColumnModel().getColumn(inn).setCellRenderer(centrar);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) tablaArticulos.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
       

        tablaArticulos.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaArticulos.getColumnModel().getColumn(0).setMinWidth(0);
        tablaArticulos.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        tablaArticulos.getColumnModel().getColumn(1).setMaxWidth(0);
        tablaArticulos.getColumnModel().getColumn(1).setMinWidth(0);
        tablaArticulos.getColumnModel().getColumn(1).setPreferredWidth(0);

//        tablaArticulos.getColumnModel().getColumn(2).setCellRenderer(centrar);
//        tablaArticulos.getColumnModel().getColumn(3).setCellRenderer(centrar);

    }
     
      public void formato_tabla_articulos_faltantes() {
        Object[][] data = {{"","","","", "","",""}};
        String[] columnNames = {"id_renta","id_articulo", "folio", "cantidad","articulo","estado","comentario"};
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        tablaArticulosFaltantes.setModel(tableModel);
        
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
        tablaArticulosFaltantes.setRowSorter(ordenarTabla);
        
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        int[] anchos = {20,20,30,30,100,60,100};

        for (int inn = 0; inn < tablaArticulosFaltantes.getColumnCount(); inn++){
            tablaArticulosFaltantes.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
            tablaArticulosFaltantes.getColumnModel().getColumn(inn).setCellRenderer(centrar);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) tablaArticulosFaltantes.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
       

        tablaArticulosFaltantes.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaArticulosFaltantes.getColumnModel().getColumn(0).setMinWidth(0);
        tablaArticulosFaltantes.getColumnModel().getColumn(0).setPreferredWidth(0);
        
          tablaArticulosFaltantes.getColumnModel().getColumn(1).setMaxWidth(0);
        tablaArticulosFaltantes.getColumnModel().getColumn(1).setMinWidth(0);
        tablaArticulosFaltantes.getColumnModel().getColumn(1).setPreferredWidth(0);

    }
     
      public void llenar_combo_estado() {
        int i = 0;       
        Object[] datos_combo = funcion.GetColumna("estado", "descripcion", "SELECT descripcion FROM estado");
        this.cmbEstadoEvento.removeAllItems();
        cmbEstadoEvento.addItem("-sel-");    
        for (i = 0; i <= datos_combo.length - 1; i++)
            cmbEstadoEvento.addItem(datos_combo[i].toString());

    }

    public void llenar_combo_tipo() {
        int i = 0;        
        Object[] datos_combo = funcion.GetColumna("tipo", "tipo", "SELECT tipo FROM tipo");
        this.cmbTipoEvento.removeAllItems();
        cmbTipoEvento.addItem("-sel-");
        for (i = 0; i <= datos_combo.length - 1; i++)
            cmbTipoEvento.addItem(datos_combo[i].toString());      

    }
   
    public void tabla_articulos_inicio(){
         String query = ""+
       "SELECT articulo.id_articulo,renta.id_renta, "+
       "renta.folio, articulo.codigo AS codigo_articulo, articulo.descripcion AS articulo_descripcion, "+
       "detalle.cantidad AS cantidad_pedido,tipo.tipo AS tipo_pedido, "+
       "estado.descripcion AS descripcion_estado "+
        "FROM renta renta " +
        "INNER JOIN detalle_renta detalle ON (renta.id_renta = detalle.id_renta) " +
        "INNER JOIN articulo articulo ON (detalle.id_articulo = articulo.id_articulo) " +
        "INNER JOIN estado estado ON (estado.id_estado = renta.id_estado) " +
        "INNER JOIN tipo tipo ON (tipo.id_tipo = renta.id_tipo) " +
        "WHERE articulo.id_articulo = "+InventarioForm.g_articuloId+" " +
        "ORDER BY renta.folio DESC " +
        "LIMIT 100 ";
         
        List<Renta> rentas = saleService.obtenerFoliosPorArticulo(query, funcion);
        
        if(rentas == null || rentas.size() <= 0){
            JOptionPane.showMessageDialog(null, "Sin resultados ", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        DefaultTableModel temp = (DefaultTableModel) tablaArticulos.getModel();
        for(Renta renta : rentas){
            for(DetalleRenta detalle : renta.getDetalleRenta()){                
            Object fila[] = {
                   detalle.getArticulo().getArticuloId()+"",
                   renta.getRentaId()+"",
                   renta.getFolio()+"",
                   detalle.getArticulo().getCodigo(),
                   detalle.getArticulo().getDescripcion(),
                   detalle.getCantidad(),
                   renta.getTipo().getTipo(),
                   renta.getEstado().getDescripcion()
               };
               temp.addRow(fila);
            } // fin for detalle
        } // fin for renta
        this.lblEncontrados.setText("Se obtuvieron "+rentas.size()+" registros, con un limite de 100 registros ");
    }
    
    public void llenar_tabla_articulos_faltantes(){
        this.formato_tabla_articulos_faltantes();
        List<Faltante> faltantes = itemService.obtenerFaltantesPorArticuloId(funcion, InventarioForm.g_articuloId);
        
        if(faltantes == null || faltantes.size()<= 0)
            return;
        
        DefaultTableModel temp = (DefaultTableModel) tablaArticulosFaltantes.getModel();
        for(Faltante faltante : faltantes){   
            String descripcionFaltante=null;
            
            if(faltante.getFgFaltante() == 1  && faltante.getFgAccidenteTrabajo() == 0 && faltante.getFgDevolucion() == 0)
                descripcionFaltante = ApplicationConstants.DS_FALTANTES_FALTANTE;
            else if(faltante.getFgFaltante() == 0 && faltante.getFgDevolucion() == 0 && faltante.getFgAccidenteTrabajo() == 0)
                descripcionFaltante = ApplicationConstants.DS_FALTANTES_REPARACION;
            else if(faltante.getFgFaltante() == 0 && faltante.getFgDevolucion() == 1 && faltante.getFgAccidenteTrabajo() == 0)
                descripcionFaltante = ApplicationConstants.DS_FALTANTES_DEVOLUCION;
            else if(faltante.getFgFaltante() == 0 && faltante.getFgDevolucion() == 0 && faltante.getFgAccidenteTrabajo() == 1)
                descripcionFaltante = ApplicationConstants.DS_FALTANTES_ACCIDENTE;
            Object fila[] = {
                  faltante.getRenta().getRentaId()+"",
                  faltante.getArticulo().getArticuloId()+"",
                  faltante.getRenta().getFolio(),
                  faltante.getCantidad(),
                  faltante.getArticulo().getDescripcion()+" "+faltante.getArticulo().getColor().getColor(),
                  descripcionFaltante,
                  faltante.getComentario()
               };
               temp.addRow(fila);          
        } // fin for renta
        
    }
    
    public void tabla_articulos(){
        this.formato_tabla();
        StringBuilder query = new StringBuilder();
        String limit = this.cmbLimite.getSelectedItem().toString();
        if(this.checkIncluirTodosEstado.isSelected() && 
                this.checkIncluirTodosEventos.isSelected()
        ){
            query.append("SELECT articulo.id_articulo,renta.id_renta, ");
            query.append("renta.folio, articulo.codigo AS codigo_articulo,articulo.descripcion AS articulo_descripcion, ");
            query.append("detalle.cantidad AS cantidad_pedido,tipo.tipo AS tipo_pedido, ");
            query.append("estado.descripcion AS descripcion_estado ");
            query.append("FROM renta renta ");
            query.append("INNER JOIN detalle_renta detalle ON (renta.id_renta = detalle.id_renta) ");
            query.append("INNER JOIN articulo articulo ON (detalle.id_articulo = articulo.id_articulo) ");
            query.append("INNER JOIN estado estado ON (estado.id_estado = renta.id_estado) ");
            query.append("INNER JOIN tipo tipo ON (tipo.id_tipo = renta.id_tipo) ");
            query.append("WHERE articulo.id_articulo = "+InventarioForm.g_articuloId+" ");
            query.append("ORDER BY renta.folio DESC ");
            query.append("LIMIT "+limit+" ");
        }else{
            String estadoEvento = null;
            String tipoEvento = null;
            
            if(this.cmbEstadoEvento.getSelectedIndex() > 0 && !this.checkIncluirTodosEstado.isSelected())
                estadoEvento = this.cmbEstadoEvento.getSelectedItem().toString();
            if(this.cmbTipoEvento.getSelectedIndex() > 0 && !this.checkIncluirTodosEventos.isSelected())
                tipoEvento = this.cmbTipoEvento.getSelectedItem().toString();
            
            query.append("SELECT articulo.id_articulo,renta.id_renta, ");
            query.append("renta.folio, articulo.codigo AS codigo_articulo,articulo.descripcion AS articulo_descripcion, ");
            query.append("detalle.cantidad AS cantidad_pedido,tipo.tipo AS tipo_pedido, ");
            query.append("estado.descripcion AS descripcion_estado ");
            query.append("FROM renta renta ");
            query.append("INNER JOIN detalle_renta detalle ON (renta.id_renta = detalle.id_renta) ");
            query.append("INNER JOIN articulo articulo ON (detalle.id_articulo = articulo.id_articulo) ");
            query.append("INNER JOIN estado estado ON (estado.id_estado = renta.id_estado) ");
            query.append("INNER JOIN tipo tipo ON (tipo.id_tipo = renta.id_tipo) ");
            query.append("WHERE articulo.id_articulo = "+InventarioForm.g_articuloId+" ");
            if(estadoEvento != null)
                query.append("AND estado.descripcion = '"+estadoEvento+"' ");
            if(tipoEvento != null)
                query.append("AND tipo.tipo = '"+tipoEvento+"' ");
            query.append("ORDER BY renta.folio DESC ");
            query.append("LIMIT "+limit+" ");
        
        }
         
        List<Renta> rentas = saleService.obtenerFoliosPorArticulo(query.toString(), funcion);
        
        if(rentas == null || rentas.size() <= 0){
            JOptionPane.showMessageDialog(null, "Sin resultados ", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        DefaultTableModel temp = (DefaultTableModel) tablaArticulos.getModel();
        for(Renta renta : rentas){
            for(DetalleRenta detalle : renta.getDetalleRenta()){                
            Object fila[] = {
                   detalle.getArticulo().getArticuloId()+"",
                   renta.getRentaId()+"",
                   renta.getFolio()+"",
                   detalle.getArticulo().getCodigo(),
                   detalle.getArticulo().getDescripcion(),
                   detalle.getCantidad(),
                   renta.getTipo().getTipo(),
                   renta.getEstado().getDescripcion()
               };
               temp.addRow(fila);
            } // fin for detalle
        } // fin for renta
        this.lblEncontrados.setText("Se obtuvieron "+rentas.size()+" registros, con un limite de "+limit+" registros ");
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
        jtbPaneFoliosPorArticulo = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaArticulos = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaArticulosFaltantes = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        cmbTipoEvento = new javax.swing.JComboBox<>();
        cmbEstadoEvento = new javax.swing.JComboBox<>();
        cmbLimite = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnExportarExcel = new javax.swing.JButton();
        checkIncluirTodosEstado = new javax.swing.JCheckBox();
        checkIncluirTodosEventos = new javax.swing.JCheckBox();

        setLocationRelativeTo(lblEncontrados);
        setTitle("Colores");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblEncontrados.setText("jLabel1");
        jPanel2.add(lblEncontrados, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 600, 530, 20));

        jtbPaneFoliosPorArticulo.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos"));

        tablaArticulos.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
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

        jtbPaneFoliosPorArticulo.addTab("articulos en renta", jScrollPane1);

        tablaArticulosFaltantes.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tablaArticulosFaltantes.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaArticulosFaltantes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tablaArticulosFaltantes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaArticulosFaltantesMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tablaArticulosFaltantes);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1146, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jtbPaneFoliosPorArticulo.addTab("articulos faltantes", jPanel3);

        jPanel2.add(jtbPaneFoliosPorArticulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 1170, 480));
        jtbPaneFoliosPorArticulo.getAccessibleContext().setAccessibleName("Folios por articulo");
        jtbPaneFoliosPorArticulo.getAccessibleContext().setAccessibleDescription("");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Consultar articulos por tipo de pedido"));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/search-icon.png"))); // NOI18N
        jButton1.setToolTipText("Realizar busqueda");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 40, 30, 30));

        cmbTipoEvento.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbTipoEvento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbTipoEvento.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel1.add(cmbTipoEvento, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 160, -1));

        cmbEstadoEvento.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbEstadoEvento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbEstadoEvento.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel1.add(cmbEstadoEvento, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 40, 190, -1));

        cmbLimite.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbLimite.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "100", "500", "1000" }));
        cmbLimite.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel1.add(cmbLimite, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 40, 190, -1));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Estado:");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 20, 180, -1));

        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Tipo de evento:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 180, 20));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Limite de resultados:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 20, 190, -1));

        btnExportarExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/excel-icon.png"))); // NOI18N
        btnExportarExcel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExportarExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarExcelActionPerformed(evt);
            }
        });
        jPanel1.add(btnExportarExcel, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 30, 40, 40));

        checkIncluirTodosEstado.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        checkIncluirTodosEstado.setText("Incluir todos");
        checkIncluirTodosEstado.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        checkIncluirTodosEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkIncluirTodosEstadoActionPerformed(evt);
            }
        });
        jPanel1.add(checkIncluirTodosEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 60, 120, -1));

        checkIncluirTodosEventos.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        checkIncluirTodosEventos.setText("Incluir todos");
        checkIncluirTodosEventos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        checkIncluirTodosEventos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkIncluirTodosEventosActionPerformed(evt);
            }
        });
        jPanel1.add(checkIncluirTodosEventos, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 120, -1));

        jPanel2.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 1160, 100));

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

    private void checkIncluirTodosEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkIncluirTodosEstadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_checkIncluirTodosEstadoActionPerformed

    private void checkIncluirTodosEventosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkIncluirTodosEventosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_checkIncluirTodosEventosActionPerformed

    private void btnExportarExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarExcelActionPerformed
        // TODO add your handling code here:
        systemService.exportarExcel(tablaArticulos);
    }//GEN-LAST:event_btnExportarExcelActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
            // TODO add your handling code here:
            this.tabla_articulos();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tablaArticulosFaltantesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaArticulosFaltantesMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            this.g_rentaId = tablaArticulosFaltantes.getValueAt(tablaArticulosFaltantes.getSelectedRow(), 0).toString();
            g_articuloId = Integer.parseInt(tablaArticulosFaltantes.getValueAt(tablaArticulosFaltantes.getSelectedRow(), 1).toString());
            this.mostrar_faltantes();
        }
    }//GEN-LAST:event_tablaArticulosFaltantesMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                VerFoliosPorArticulo dialog = new VerFoliosPorArticulo(new java.awt.Frame(), true);
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
    private javax.swing.JButton btnExportarExcel;
    private javax.swing.JCheckBox checkIncluirTodosEstado;
    private javax.swing.JCheckBox checkIncluirTodosEventos;
    private javax.swing.JComboBox<String> cmbEstadoEvento;
    private javax.swing.JComboBox<String> cmbLimite;
    private javax.swing.JComboBox<String> cmbTipoEvento;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jtbPaneFoliosPorArticulo;
    private javax.swing.JLabel lblEncontrados;
    private javax.swing.JTable tablaArticulos;
    private javax.swing.JTable tablaArticulosFaltantes;
    // End of variables declaration//GEN-END:variables
}
